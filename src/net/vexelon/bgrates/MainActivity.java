package net.vexelon.bgrates;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends Activity {
	
	private final static String TAG = Defs.LOG_TAG;
	private ListView _listView;
	private ProgressDialog _progressDialog;
	private CurrencyListAdapter _adapter;
	private ExchangeRate _myRates = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.v(TAG, "@onCreate()");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		_listView = (ListView)findViewById(R.id.ListView01);

		init();
		//refresh();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, Defs.MENU_REFRESH, 0, "Refresh").setIcon(R.drawable.ic_menu_refresh);
		menu.add(0, Defs.MENU_ABOUT, 0, "About").setIcon(R.drawable.ic_menu_info_details);
		return true;
	}
	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		switch(resultCode) {
//		case Defs.ACTIVITYRESULT_CLOSE:
//			this.setResult(Defs.ACTIVITYRESULT_CLOSE);
//			this.finish();
//			break;
//		}
//		super.onActivityResult(requestCode, resultCode, data);
//	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
		case Defs.MENU_REFRESH:
			refresh();
			break;
		case Defs.MENU_ABOUT:
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			break;
		}

		return false;
	}
	
	private void init() {
		Log.v(TAG, "@init()");
		
		// load defaults
		InputStream is = this.getResources().openRawResource(R.raw.exchangerates);
		_myRates = new ExchangeRate();
		parseRates(is, _myRates);
	
		// populate ListView UI
		this.setTitle(_myRates.getHeader().getTitle());
		_adapter = new CurrencyListAdapter(this, R.layout.currency_row_layout, _myRates.items());
		_listView.setAdapter(_adapter);				
	}
	
	private void refresh() {
		Log.v(TAG, "@refresh()");
		
		_progressDialog = ProgressDialog.show(this, "Please wait ...", "Updating rates...", true);
//		final Activity thisActivity = this;
		
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
		
		_myRates = new ExchangeRate();
		if ( !parseRates(Defs.INTERNAL_STORAGE_FILE, _myRates) ) {
			
			if ( !downloadFile(
					this.getResources().getString(R.string.URL_BNB_RATES), 
					Defs.INTERNAL_STORAGE_FILE) )
			{
				_progressDialog.dismiss();
				
				alertBuilder.setTitle("Error updating!")
					.setMessage("Could not download file !")
					.setIcon(R.drawable.alert)
					.setOnKeyListener(new DialogInterface.OnKeyListener() {
						
						@Override
						public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
							dialog.dismiss();
//							thisActivity.setResult(Defs.ACTIVITYRESULT_CLOSE);
//							thisActivity.finish();
							return false;
						}
					})
					.create()
					.show();
				return;
			}
			else {
				_myRates = new ExchangeRate();
				if ( !parseRates(Defs.INTERNAL_STORAGE_FILE, _myRates) ) {
					_progressDialog.dismiss();
					
					alertBuilder.setTitle("Error parsing!")
					.setMessage("Could not parse file !")
					.setIcon(R.drawable.alert)
					.setOnKeyListener(new DialogInterface.OnKeyListener() {
						
						@Override
						public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
							dialog.dismiss();
//							thisActivity.setResult(Defs.ACTIVITYRESULT_CLOSE);
//							thisActivity.finish();
							return false;
						}
					})					
					.create()
					.show();
					return;
				}
			}
			
			
		}
		
		_progressDialog.dismiss();

		// populate ListView UI
		this.setTitle(_myRates.getHeader().getTitle());
		_adapter = new CurrencyListAdapter(this, R.layout.currency_row_layout, _myRates.items());
		_listView.setAdapter(_adapter);		
	}
	
	private boolean parseRates(String path, ExchangeRate rates) {
		
		FileInputStream fis = null;
		try {
			fis = openFileInput(path);
			
			return parseRates(fis, rates);
		}
		catch(FileNotFoundException e) {
			Log.e(TAG, "Faild to parse file!", e);
		}
		return false;
	}
		
	private boolean parseRates(InputStream is, ExchangeRate rates) {		
		Log.v(TAG, "@parseRates");
	
		boolean ret = false;
		
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			XmlPullParser xpp = factory.newPullParser();
			
			xpp.setInput(is, null);
			
			int eventType = xpp.getEventType();
			boolean parsingRow = false,
					parsingHeader = false;
			CurrencyInfo curInfo = null;
			HeaderInfo headerInfo = null;
			
			while( eventType != XmlPullParser.END_DOCUMENT ) {
				
				String tagName = null;
				
				switch(eventType) {
				case XmlPullParser.START_DOCUMENT:
					//Log.d(TAG, "Creating new ExchangeRate() object ...");
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
						Log.d(TAG, "Adding currency header info ...");
						parsingHeader = false;
						rates.addHeader(headerInfo);
					}					
					break;
				}
				
				// advance to next tag
				eventType = xpp.next();
			}
			
			ret = true;
			
		}
		catch(Exception e) {
			Log.e(TAG, "Error while parsing XML !", e);
		}
		finally {
			try { 
				if ( is != null ) is.close(); 
			} catch (IOException e) { }
		}
		
		return ret;
	}
	
	private boolean downloadFile(String url, String destFile) {
		Log.v(TAG, "@downloadFile()");
		Log.d(TAG, "Downloading " + url);
		
		boolean ret = false;
		
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
			
			ret = true;
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
		
		return ret;
	}

}
