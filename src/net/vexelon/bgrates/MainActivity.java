package net.vexelon.bgrates;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import net.vexelon.bgrates.R;

import org.apache.http.util.ByteArrayBuffer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private final static String TAG = Defs.LOG_TAG;
	private ListView _listView;
	private ProgressDialog _progressDialog;
	private CurrencyListAdapter _adapter;
	private ExchangeRate _myRates = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		Log.i(TAG, "Starting up Application...");
		
		refresh();
		
		// populate ListView UI
		_adapter = new CurrencyListAdapter(this, R.layout.currency_row_layout, _myRates.items());
		_listView = (ListView)findViewById(R.id.ListView01);
		_listView.setAdapter(_adapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, Defs.MENU_REFRESH, 0, "Refresh");
		menu.add(0, Defs.MENU_ABOUT, 0, "About");
		return true;
	}
	
	private void refresh() {
		Log.d(TAG, "Refresh contents ...");
		
		this.setTitle(_myRates.getHeader().getTitle());
		
		//_progressDialog = ProgressDialog.show(this, "Please wait ...", "Downloading data...", true);		
		
		_myRates = new ExchangeRate();
		if ( ! parseRates(Defs.INTERNAL_STORAGE_FILE, _myRates) ) {
			downloadFile(Defs.URL_BNB_RATES, Defs.INTERNAL_STORAGE_FILE);
			parseRates(Defs.INTERNAL_STORAGE_FILE, _myRates);
		}
		
		
	}
	
	private boolean parseRates(String path, ExchangeRate rates) {
		Log.d(TAG, "Parsing XML file ...");
	
		boolean bRet = false;
		//ExchangeRate rates = null;
		FileInputStream fis = null;
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			XmlPullParser xpp = factory.newPullParser();
			
			fis = openFileInput(path);
			xpp.setInput(fis, null);
			
			int eventType = xpp.getEventType();
//			boolean done = false;
			boolean parsingRow = false,
					parsingHeader = false;
			CurrencyInfo curInfo = null;
			HeaderInfo headerInfo = null;
			
			while( eventType != XmlPullParser.END_DOCUMENT ) {
				
				String tagName = null;
				
				switch(eventType) {
				case XmlPullParser.START_DOCUMENT:
					Log.i(TAG, "Creating new ExchangeRate() object ...");
					//rates = new ExchangeRate();
					break;
				
				case XmlPullParser.START_TAG:
					tagName = xpp.getName();
					
					//Log.d(TAG, "Reading TAG=" + tagName);
					
					// parse ROW
					if ( tagName.equals(Defs.XML_TAG_ROW) && rates.isHeaderAvailable() ) {
						parsingRow = true;
						curInfo = new CurrencyInfo();
					}
					else if ( tagName.equals(Defs.XML_TAG_ROW) && !rates.isHeaderAvailable() ) {
						parsingHeader = true;
						headerInfo = new HeaderInfo();
					}
					
					if ( parsingRow ) {
						if ( tagName.equals(Defs.XML_TAG_NAME) ) {
							curInfo.setName(xpp.nextText());
						}
						else if ( tagName.equals(Defs.XML_TAG_CODE) ) {
							curInfo.setCode(xpp.nextText());
						}
						else if ( tagName.equals(Defs.XML_TAG_RATIO) ) {
							curInfo.setRatio(xpp.nextText());
						}
						else if ( tagName.equals(Defs.XML_TAG_RATE) ) {
							curInfo.setRate(xpp.nextText());
						}
						else if ( tagName.equals(Defs.XML_TAG_REVERSERATE) ) {
							curInfo.setReverseRate(xpp.nextText());
						}
						else if ( tagName.equals(Defs.XML_TAG_EXTRAINFO) ) {
							curInfo.setExtraInfo(xpp.nextText());
						}
					}
					else if ( parsingHeader ) {
						if ( tagName.equals(Defs.XML_TAG_NAME) ) {
							headerInfo.setName(xpp.nextText());
						}
						else if ( tagName.equals(Defs.XML_TAG_CODE) ) {
							headerInfo.setCode(xpp.nextText());
						}
						else if ( tagName.equals(Defs.XML_TAG_RATIO) ) {
							headerInfo.setRatio(xpp.nextText());
						}
						else if ( tagName.equals(Defs.XML_TAG_RATE) ) {
							headerInfo.setRate(xpp.nextText());
						}
						else if ( tagName.equals(Defs.XML_TAG_REVERSERATE) ) {
							headerInfo.setReverseRate(xpp.nextText());
						}
						else if ( tagName.equals(Defs.XML_TAG_EXTRAINFO) ) {
							headerInfo.setExtraInfo(xpp.nextText());
						}
						else if ( tagName.equals(Defs.XML_TAG_CURR_DATE)) {
							headerInfo.setCurDate(xpp.nextText());
						}
						else if ( tagName.equals(Defs.XML_TAG_TITLE)) {
							headerInfo.setTitle(xpp.nextText());
						}
					}
					
					break;
				
				case XmlPullParser.END_TAG:
					tagName = xpp.getName();
					
					if ( tagName.equals(Defs.XML_TAG_ROW) && parsingRow ) {
						parsingRow = false;
						rates.add(curInfo);
					}
					else if ( tagName.equals(Defs.XML_TAG_ROW) && parsingHeader ) {
						Log.i(TAG, "Adding currency header info ...");
						parsingHeader = false;
						rates.addHeader(headerInfo);
					}					
					break;
				}
				
				// advance to next tag
				eventType = xpp.next();
			}
			
			bRet = true;
			
		}
		catch(Exception e) {
			Log.e(TAG, "Error while parsing XML !", e);
		}
		finally {
			try { 
				if ( fis != null ) fis.close(); 
			} catch (IOException e) { }
		}
		
		return bRet;
	}
	
	private void downloadFile(String url, String destFile) {
		Log.d(TAG, "Downloading file " + url);
		
		BufferedInputStream bis = null;
		FileOutputStream fos = null;
		InputStream is = null;
		
		try {
			URL myUrl = new URL(url);
			URLConnection connection = myUrl.openConnection();
			
			is = connection.getInputStream();
			bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(1024);
			
			int n = 0;
			while( (n = bis.read()) != -1 )
				baf.append((byte) n);
			
			// save to internal storage
			Log.d(TAG, "Saving downloaded file ...");
			fos = openFileOutput(destFile,Context.MODE_PRIVATE);
			fos.write(baf.toByteArray());
			fos.close();
			Log.d(TAG, "File saved successfully.");
		}
		catch(Exception e) {
			Log.e(TAG, "Error while downloading and saving file !", e);
		}
		finally {
			try {
				if ( fos != null ) fos.close();
			} catch( IOException e ) {}
			try {
				if ( bis != null ) bis.close();
			} catch( IOException e ) {}
			try {
				if ( is != null ) is.close();
			} catch( IOException e ) {}
		}
	}

}
