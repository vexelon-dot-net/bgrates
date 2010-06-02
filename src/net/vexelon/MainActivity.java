package net.vexelon;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private final static String TAG = Defs.LOG_TAG;
	ListView _listView;
	private String lv_arr[]={"Android","iPhone","BlackBerry","AndroidPeople"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		Log.i(TAG, "Starting up Application...");
		
		downloadFile(Defs.URL_BNB_RATES, Defs.INTERNAL_STORAGE_FILE);
		parseRates(Defs.INTERNAL_STORAGE_FILE);
		
		_listView = (ListView)findViewById(R.id.ListView01);
		_listView.setAdapter(
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lv_arr ));
	}
	
	private ExchangeRate parseRates(String path) {

		Log.d(TAG, "Parsing XML file ...");
	
		FileInputStream fis = null;
		ExchangeRate rates = null;
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			XmlPullParser xpp = factory.newPullParser();
			
			fis = openFileInput(path);
			xpp.setInput(fis, null);
			
			int eventType = xpp.getEventType();
//			boolean done = false;
			boolean parsingRow = false;
			CurrencyInfo curInfo = null;
			
			while( eventType != XmlPullParser.END_DOCUMENT ) {
				
				String tagName = null;
				
				switch(eventType) {
				case XmlPullParser.START_DOCUMENT:
					Log.i(TAG, "Creating new ExchangeRate() object ...");
					rates = new ExchangeRate();
					break;
				
				case XmlPullParser.START_TAG:
					tagName = xpp.getName();
					
					Log.d(TAG, "Reading TAG=" + tagName);
					
					// parse ROW
					if ( tagName.equals(Defs.XML_TAG_ROW) ) {
						parsingRow = true;
						curInfo = new CurrencyInfo();
					}
					else if ( tagName.equals(Defs.XML_TAG_NAME) && parsingRow ) {
						curInfo.setName(xpp.nextText());
					}
					else if ( tagName.equals(Defs.XML_TAG_CODE) && parsingRow ) {
						curInfo.setCode(xpp.nextText());
					}
					else if ( tagName.equals(Defs.XML_TAG_RATIO) && parsingRow ) {
						curInfo.setRatio(xpp.nextText());
					}
					else if ( tagName.equals(Defs.XML_TAG_RATE) && parsingRow ) {
						curInfo.setRate(xpp.nextText());
					}
					else if ( tagName.equals(Defs.XML_TAG_REVERSERATE) && parsingRow ) {
						curInfo.setReverseRate(xpp.nextText());
					}
					else if ( tagName.equals(Defs.XML_TAG_EXTRAINFO) && parsingRow ) {
						curInfo.setExtraInfo(xpp.nextText());
					}
					
					break;
				
				case XmlPullParser.END_TAG:
					tagName = xpp.getName();
					
					if ( tagName.equals(Defs.XML_TAG_ROW) ) {
						parsingRow = false;
						
						if ( !rates.isHeaderAvailable() ) {
							Log.i(TAG, "Adding currency header info ...");
							rates.addHeader(curInfo);
						}
						else {
							rates.add(curInfo);
						}
					}					
					break;
				}
				
				// advance to next tag
				eventType = xpp.next();
			}
		}
		catch(Exception e) {
			Log.e(TAG, "Error while parsing XML !", e);
		}
		finally {
			try { 
				if ( fis != null ) fis.close(); 
			} catch (IOException e) { }
		}
		
		return rates;
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
