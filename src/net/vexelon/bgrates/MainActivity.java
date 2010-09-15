package net.vexelon.bgrates;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.util.ByteArrayBuffer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	
	//private final static String TAG = Defs.LOG_TAG;
	private Activity _context = null;
	private ListView _listView;
	private ProgressDialog _progressDialog = null;
	private CurrencyListAdapter _adapter;
	private ExchangeRate _myRates = null;
	private String _downloadUrlSuffix;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Log.v(TAG, "@onCreate()");

		_context = this;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		_listView = (ListView)findViewById(R.id.ListView01);
		_downloadUrlSuffix = String.format(Defs.URL_BNB_FORMAT, this.getResources().getString(R.string.URL_BNB_RATES_SUFFIX));

		init();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Log.v(TAG, "@onCreateOptionsMenu()");
		
		menu.add(0, Defs.MENU_BG_RATES, 0, R.string.menu_bg_rates).setIcon(R.drawable.bg);
		menu.add(0, Defs.MENU_EN_RATES, 0, R.string.menu_en_rates).setIcon(R.drawable.gb);
		menu.add(1, Defs.MENU_REFRESH, 10, R.string.menu_refresh).setIcon(R.drawable.ic_menu_refresh);
		//menu.add(1, Defs.MENU_CONVERT, 10, R.string.menu_convert).setIcon(R.drawable.ic_menu_info_details);
		menu.add(1, Defs.MENU_ABOUT, 15, R.string.menu_about).setIcon(R.drawable.ic_menu_info_details);
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
		//Log.v(TAG, "@onOptionsItemSelected()");
		
		Intent intent = null;
		
		switch(item.getItemId()) {
		case Defs.MENU_REFRESH:
			refresh();
			break;
		case Defs.MENU_ABOUT:
			intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			break;
		case Defs.MENU_BG_RATES:
			_downloadUrlSuffix = String.format(Defs.URL_BNB_FORMAT, Defs.URL_BNB_SUFFIX_BG);
			refresh();
			break;
		case Defs.MENU_EN_RATES:
			_downloadUrlSuffix = String.format(Defs.URL_BNB_FORMAT, Defs.URL_BNB_SUFFIX_EN);
			refresh();
			break;
		case Defs.MENU_CONVERT:
			intent = new Intent(this, ConvertActivity.class);
			startActivity(intent);
			break;
		}

		return false;
	}
	
	private void init() {
		//Log.v(TAG, "@init()");
		
		// load defaults
		_myRates = new ExchangeRate();
		if ( !parseRates(this.getResources().getString(R.string.INTERNAL_STORAGE_CACHE), _myRates) ) {
			InputStream is = this.getResources().openRawResource(R.raw.exchangerates);
			if ( !parseRates(is, _myRates) ) {

				// something's really wrong !
				
				AlertDialog.Builder alertBuilder = new AlertDialog.Builder(_context);
				alertBuilder.setTitle(
						_context.getResources().getString(
								R.string.dlg_parse_error_title)).setMessage(
										_context.getResources().getString(R.string.dlg_parse_error_msg)).setIcon(
						R.drawable.alert).setOnKeyListener(
						new DialogInterface.OnKeyListener() {

							@Override
							public boolean onKey(DialogInterface dialog,
									int keyCode, KeyEvent event) {
								dialog.dismiss();
								return false;
							}
						}).create().show();				
			}
		}
		
		this.setTitle(_myRates.getHeader().getTitle());
		
		// check if download should be performed
		if (isUpdateRequired()) {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(_context);
			alertBuilder.setTitle(
					_context.getResources().getString(
							R.string.dlg_lastupdate_title)).setMessage(
									_context.getResources().getString(R.string.dlg_lastupdate_msg)).setIcon(
					R.drawable.help);
		
			alertBuilder.setPositiveButton(this.getResources().getString(R.string.dlg_yes), new OnClickListener() {
		
				@Override
				public void onClick(DialogInterface dialog, int which) {
					refresh();
				}
			});
			alertBuilder.setNegativeButton(this.getResources().getString(R.string.dlg_no), null);
			alertBuilder.show();
		}
		
		// populate ListView UI
		_adapter = new CurrencyListAdapter(this, R.layout.currency_row_layout, _myRates.items());
		_listView.setAdapter(_adapter);	
		
		_listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				CurrencyInfo ci = (CurrencyInfo)_listView.getItemAtPosition(arg2);
				if ( ci != null ) {
					Toast.makeText(_context, 
							String.format("%s:\t%s", ci.getName(), ci.getCode() ), Defs.MAX_TOAST_INFO_TIME).show();
				}
			}
		});
	}
	
	private void refresh() {
		//Log.v(TAG, "@refresh()");
		
		_progressDialog = ProgressDialog.show(this, 
				this.getResources().getString(R.string.dlg_update_title),
				this.getResources().getString(R.string.dlg_update_msg), 
				true);
		
		new Thread() {
			public void run() {
				try {
					
					// DOWNLOAD //
					if (!downloadFile(
							_downloadUrlSuffix,
							_context.getResources().getString(R.string.INTERNAL_STORAGE_CACHE))) {
						
						// SHOW ERROR ALERT //
						_context.runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								AlertDialog.Builder alertBuilder = new AlertDialog.Builder(_context);
								alertBuilder.setTitle(
										_context.getResources().getString(
												R.string.dlg_update_error_title)).setMessage(
														_context.getResources().getString(R.string.dlg_update_error_msg)).setIcon(
										R.drawable.alert).setOnKeyListener(
										new DialogInterface.OnKeyListener() {

											@Override
											public boolean onKey(DialogInterface dialog,
													int keyCode, KeyEvent event) {
												dialog.dismiss();
												return false;
											}
										}).create().show();								
							}
						});
					} 
					else {
						
						// PARSE //
						
						_myRates = new ExchangeRate();
						if (!parseRates(_context.getResources().getString(R.string.INTERNAL_STORAGE_CACHE), _myRates)) {
							
							// SHOW ERROR ALERT //
							_context.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									
									AlertDialog.Builder alertBuilder = new AlertDialog.Builder(_context);
									alertBuilder.setTitle(
											_context.getResources().getString(
													R.string.dlg_parse_error_title)).setMessage(
															_context.getResources().getString(R.string.dlg_parse_error_msg)).setIcon(
											R.drawable.alert).setOnKeyListener(
											new DialogInterface.OnKeyListener() {

												@Override
												public boolean onKey(DialogInterface dialog,
														int keyCode, KeyEvent event) {
													dialog.dismiss();
													return false;
												}
											}).create().show();
								}
							});
						}
						else {
							
							// UPDATE VIEW //
							
							_context.runOnUiThread(new Runnable() {
								
								@Override
								public void run() {
									saveStatus(_myRates.getHeader().getTitle());
									_context.setTitle(_myRates.getHeader().getTitle());
									_adapter = new CurrencyListAdapter(_context, R.layout.currency_row_layout, _myRates.items());
									_listView.setAdapter(_adapter);							
								}
							});							
						}
					}
					
					Thread.sleep(1000);
				}
				catch(Exception e) {
					//Log.e(TAG, e.getMessage());
				}
				_progressDialog.dismiss();
			};
		}.start();
	}
	
	private void saveStatus(String lastUpdate) {
		//this.setTitle(lastUpdate);
		// save last update nfo
		SharedPreferences prefs = this.getSharedPreferences(Defs.PREFS_NAME, 0);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(Defs.PREFS_KEY_LASTUPDATE, lastUpdate);
		
		String theDate = DateFormat.format(new String("yyyyMMdd"), Calendar.getInstance()).toString();
		editor.putString(Defs.PREFS_KEY_LASTUPDATE_TIME, theDate);
		//Log.v(TAG, "Saving last update date - " + theDate);
		
		editor.commit();
	}
	
	private boolean isUpdateRequired() {
		SharedPreferences prefs = this.getSharedPreferences(Defs.PREFS_NAME, 0);
		String lastUpdateTime = prefs.getString(Defs.PREFS_KEY_LASTUPDATE_TIME, "");
		
		String now = DateFormat.format(new String("yyyyMMdd"), Calendar.getInstance()).toString();
		//Log.v(TAG, String.format("Reading last update date - %s / Now is %s ", lastUpdateTime, now));
		return lastUpdateTime.length() == 0 || lastUpdateTime.compareTo(now) < 0;
	}
	
	private boolean parseRates(String path, ExchangeRate rates) {
		
		FileInputStream fis = null;
		try {
			fis = openFileInput(path);
			
			return parseRates(fis, rates);
		}
		catch(FileNotFoundException e) {
			//Log.e(TAG, "Faild to parse file!", e);
		}
		return false;
	}
		
	private boolean parseRates(InputStream is, ExchangeRate rates) {		
		//Log.v(TAG, "@parseRates");
	
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
					rates.clear();
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
						//Log.v(TAG, "Adding currency header info ...");
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
			//Log.e(TAG, "Error while parsing XML !", e);
		}
		finally {
			try { 
				if ( is != null ) is.close(); 
			} catch (IOException e) { }
		}
		
		return ret;
	}
	
	private boolean downloadFile(String url, String destFile) {
		//Log.v(TAG, "@downloadFile()");
		//Log.d(TAG, "Downloading " + url);
		
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
			//Log.v(TAG, "Saving downloaded file ...");
			fos = openFileOutput(destFile, Context.MODE_PRIVATE);
			fos.write(baf.toByteArray());
			fos.close();
			//Log.v(TAG, "File saved successfully.");
			
			ret = true;
		}
		catch(Exception e) {
			//Log.e(TAG, "Error while downloading and saving file !", e);
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
