/*
 * The MIT License
 * 
 * Copyright (c) 2010 Petar Petrov
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package net.vexelon.bgrates;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	
	private final static String TAG = Defs.LOG_TAG;
	private Activity _context = null;
	private ListView _listView;
	private ProgressDialog _progressDialog = null;
	private CurrencyListAdapter _adapter;
	private ExchangeRate _myRates = null, _oldRates = null;
	private String _downloadUrlSuffix;
	private boolean _forceDownload = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Log.v(TAG, "@onCreate()");

		_context = this;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		_listView = (ListView)findViewById(R.id.ListView01);
		_downloadUrlSuffix = String.format(Defs.URL_BNB_FORMAT, getResString(R.string.URL_BNB_RATES_SUFFIX));

		loadSettings();
		init();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Log.v(TAG, "@onCreateOptionsMenu()");
		
		menu.add(0, Defs.MENU_BG_RATES, 0, R.string.menu_bg_rates).setIcon(R.drawable.bg);
		menu.add(0, Defs.MENU_EN_RATES, 0, R.string.menu_en_rates).setIcon(R.drawable.gb);
		menu.add(1, Defs.MENU_REFRESH, 10, R.string.menu_refresh).setIcon(R.drawable.ic_menu_refresh);
		menu.add(1, Defs.MENU_CONVERT, 10, R.string.menu_convert).setIcon(R.drawable.money);
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
		String newDownloadUrlSuffix;
		
		switch(item.getItemId()) {
		case Defs.MENU_REFRESH:
			refresh();
			break;
		case Defs.MENU_ABOUT:
			intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			break;
		case Defs.MENU_BG_RATES:
			newDownloadUrlSuffix = String.format(Defs.URL_BNB_FORMAT, Defs.URL_BNB_SUFFIX_BG);
			_forceDownload = !_downloadUrlSuffix.equals(newDownloadUrlSuffix);
			_downloadUrlSuffix = newDownloadUrlSuffix;
			refresh();
			break;
		case Defs.MENU_EN_RATES:
			newDownloadUrlSuffix = String.format(Defs.URL_BNB_FORMAT, Defs.URL_BNB_SUFFIX_EN);
			_forceDownload = !_downloadUrlSuffix.equals(newDownloadUrlSuffix);
			_downloadUrlSuffix = newDownloadUrlSuffix;
			refresh();
			break;
		case Defs.MENU_CONVERT:
			intent = new Intent(this, ConvertActivity.class);
			intent.putExtra(Defs.INT_EXCHANGERATES, _myRates);
			startActivity(intent);
			break;
		}

		return false;
	}
	
	private void init() {
		//Log.v(TAG, "@init()");
		
		// attempt to load latest stored exchange rates file
		_myRates = new ExchangeRate();
		
		if ( !parseRates(getResString(R.string.INTERNAL_STORAGE_CACHE), _myRates) ) {
			
			// Default - try to load locally stored raw resource 
			InputStream is = this.getResources().openRawResource(R.raw.exchangerates);
			if ( !parseRates(is, _myRates) ) {
				// something's really wrong !
				Utils.showAlertDialog(_context, R.string.dlg_parse_error_msg, R.string.dlg_parse_error_title);
			}
		}
		
		// attempt to load stored previous rates file 
		_oldRates = new ExchangeRate();
		
		if ( !parseRates(getResString(R.string.PREVIOUS_INTERNAL_STORAGE_CACHE),_oldRates) ) {
			// Default - try to load locally stored raw resource 
			InputStream is = this.getResources().openRawResource(R.raw.exchangerates);
			if ( !parseRates(is, _oldRates) ) {
				_oldRates = null;
			}
		}
		
		this.setTitle(_myRates.getHeader().getTitle());
		
		// calculate tendencies
		if ( _oldRates != null ) {
			_myRates.evaluateTendencies(_oldRates);
		}
		
		// check if download should be performed
		if (isUpdateRequired()) {
			
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(_context);
			alertBuilder.setTitle(
					_context.getResources().getString(
							R.string.dlg_lastupdate_title)).setMessage(
									_context.getResources().getString(R.string.dlg_lastupdate_msg)).setIcon(
					R.drawable.help);
		
			alertBuilder.setPositiveButton(getResString(R.string.dlg_yes), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					refresh();
				}
			});
			alertBuilder.setNegativeButton(getResString(R.string.dlg_no), null);
			alertBuilder.show();
		}
		
		// populate ListView UI
		_adapter = new CurrencyListAdapter(this, R.layout.currency_row_layout, _myRates.getItems());
		
		_listView.setAdapter(_adapter);	
		_listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				CurrencyInfo ci = (CurrencyInfo)_listView.getItemAtPosition(arg2);
				if ( ci != null ) {
					//Log.d(TAG, "Old Rates data: " + _oldRates.getTimeStamp() + " New rates date: " + _myRates.getTimeStamp());
					if ( _oldRates != null && ! _oldRates.getTimeStamp().equals(_myRates.getTimeStamp()) ) {
						CurrencyInfo oldCurrencyRate = _oldRates.getCurrencyByCode(ci.getCode());
						// some currencies are new, and now old records exist!
						if (oldCurrencyRate != null) {
	//						message = String.format("%s\t\t%s\t%s\n%s\t\t%s\t%s", 
	//								oldCurrencyRate.getExtraInfo(), oldCurrencyRate.getRatio(), oldCurrencyRate.getRate(), 
	//								ci.getExtraInfo(), ci.getRatio(), ci.getRate() );
							Intent intent = new Intent(_context, RateInfoActivity.class);
							intent.putExtra(Defs.INTENT_FLAG_ID, ExchangeRate.getResourceFromCode(ci));
							intent.putExtra(Defs.INTENT_OLD_RATEINFO, String.format("%s    %s  %s", oldCurrencyRate.getExtraInfo(), oldCurrencyRate.getRatio(), oldCurrencyRate.getRate()));
							intent.putExtra(Defs.INTENT_NEW_RATEINFO, String.format("%s    %s  %s", ci.getExtraInfo(), ci.getRatio(), ci.getRate()));
							intent.putExtra(Defs.INTENT_NEW_RATEINFO_TENDENCY_ICONID, ExchangeRate.getResourceFromTendency(ci.getTendency()));
							startActivity(intent);
						}
					}
					else {
						Toast.makeText(_context, String.format("%s:\t%s", ci.getName(), ci.getCode() ), 
								Defs.MAX_TOAST_INFO_TIME).show();
					}
				}
			}
		});
	}
	
	private void refresh() {
		//Log.v(TAG, "@refresh()");
		
		_progressDialog = ProgressDialog.show(this, getResString(R.string.dlg_update_title), getResString(R.string.dlg_update_msg), true);
		
		new Thread() {
			public void run() {
				try {
					
					File cacheFile = new File(_context.getCacheDir().getAbsolutePath() + File.separator + Defs.URI_CACHE_NAME);
					
					// DOWNLOAD //
					if (!Utils.downloadFile(_context,
							_downloadUrlSuffix,
							cacheFile)) {
							//_context.getResources().getString(R.string.INTERNAL_STORAGE_CACHE))) {
						
						// SHOW ERROR ALERT //
						_context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Utils.showAlertDialog(_context, R.string.dlg_update_error_msg, R.string.dlg_update_error_title);
							}
						});
					} 
					else {
						
						//Issue #1: EUR currency is not presented in the currency list
						// Try to download index.htm page
						File cacheFileHtml = new File(_context.getCacheDir().getAbsolutePath() + File.separator + Defs.URI_CACHE_NAME_INDEXHTM);
						if (Utils.downloadFile(_context, Defs.URL_BNB_INDEX, cacheFileHtml)) {
							CurrencyInfo currency = parseEuro(cacheFileHtml);
							if (currency == null || !injectCurrency(currency, cacheFile)) {
								// save downloaded file and remove cache
								Log.e(TAG, "Failed to load EUR currency rate!");
							}
						}
						// -------------
						
						ExchangeRate newRates = new ExchangeRate();
						FileInputStream fis = new FileInputStream(cacheFile);
						
						if (!parseRates(fis, newRates)) {
							// SHOW ERROR ALERT //
							_context.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									Utils.showAlertDialog(_context, R.string.dlg_parse_error_msg, R.string.dlg_parse_error_title);
								}
							});
						}
						else {
							
							Log.v(TAG, "ABOUT TO SHOW");
							
							// check if the newly downloaded file is really newer than the current
							if ( _forceDownload || !newRates.getHeader().getTitle().equals(_myRates.getHeader().getTitle()) ) {
								
								Log.v(TAG, "SHOWING");
								// clear flag
								_forceDownload = false; 
								
								// save current exchange rates file to storage as previous (only if timestamps differ)
								Log.d(TAG, "[2] Current Rates data: " + _myRates.getTimeStamp() + " New rates date: " + newRates.getTimeStamp());
								
								if ( ! newRates.getTimeStamp().equals(_myRates.getTimeStamp()) ) {
									savePreviousRates();
									
									// swap exchange rates objects and calculate tendencies
									newRates.evaluateTendencies(_myRates);
									_oldRates = _myRates;
									_myRates = newRates;									
								}
								else {
									// new language file => just evaluate against the old rates
									newRates.evaluateTendencies(_oldRates);
									_myRates = newRates;
								}
								
								// save downloaded file and remove cache
								Utils.moveCacheFile(_context, cacheFile, _context.getResources().getString(R.string.INTERNAL_STORAGE_CACHE));
								
								// UPDATE VIEW //
								
								_context.runOnUiThread(new Runnable() {
									
									@Override
									public void run() {
										saveSettings(_myRates.getHeader().getTitle());
										_context.setTitle(_myRates.getHeader().getTitle());
										_adapter = new CurrencyListAdapter(_context, R.layout.currency_row_layout, _myRates.getItems());
										_listView.setAdapter(_adapter);							
									}
								});							
								
							}
						}
					}
					
					Thread.sleep(1000);
				}
				catch(Exception e) {
					Log.e(TAG, e.getMessage());
				}
				_progressDialog.dismiss();
			};
		}.start();
	}
	
	/**
	 * Save activity defaults & settings
	 * @param lastUpdate
	 */
	private void saveSettings(String lastUpdate) {
		// save last update nfo
		SharedPreferences prefs = this.getSharedPreferences(Defs.PREFS_NAME, 0);
		SharedPreferences.Editor editor = prefs.edit();

		editor.putString(Defs.PREFS_KEY_LASTUPDATE, lastUpdate);
		String theDate = DateFormat.format("yyyyMMdd", Calendar.getInstance()).toString();
		editor.putString(Defs.PREFS_KEY_LASTUPDATE_TIME, theDate);
		
		editor.commit();
	}
	
	/**
	 * Save previous exchange rates file 
	 */
	private void savePreviousRates() {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		try {
			fis = openFileInput(getResString(R.string.INTERNAL_STORAGE_CACHE));
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			byte[] buffer = new byte[1024];
			int read = -1;
			while( (read = fis.read(buffer) ) != -1 ) {
				baos.write(buffer, 0, read);
			}
			baos.close();
			fis.close();

			// write to old rates cache
			fos = openFileOutput(getResString(R.string.PREVIOUS_INTERNAL_STORAGE_CACHE), MODE_PRIVATE);
			baos.writeTo(fos);
			fos.close();
		}
		catch(Exception e) {
			//Log.e(TAG, "Error saving previous rates!");
		}
		finally {
			try { if ( fis != null ) fis.close(); } catch (IOException e) { }
			try { if ( fos != null ) fos.close(); } catch (IOException e) { }
		}
	}
	
	/**
	 * Load last saved activity defaults and settings
	 */
	private void loadSettings() {
//		SharedPreferences prefs = this.getSharedPreferences(Defs.PREFS_NAME, 0);
//		
//		String fileData = prefs.getString(Defs.PREFS_KEY_PREV_RATES_FILE, "");
//		Log.d(TAG, "Loaded last " + fileData);
//		if ( fileData.length() > 0 ) {
//			ByteArrayInputStream bias = new ByteArrayInputStream(fileData.getBytes());
//			_oldRates = new ExchangeRate();
//			parseRates(bias, _oldRates);
//		}
	}
	
	private boolean isUpdateRequired() {
		SharedPreferences prefs = this.getSharedPreferences(Defs.PREFS_NAME, 0);
		String lastUpdateTime = prefs.getString(Defs.PREFS_KEY_LASTUPDATE_TIME, "");
		
		String now = DateFormat.format("yyyyMMdd", Calendar.getInstance()).toString();
		//Log.v(TAG, String.format("Reading last update date - %s / Now is %s ", lastUpdateTime, now));
		return lastUpdateTime.length() == 0 || lastUpdateTime.compareTo(now) < 0;
	}
	
	private boolean parseRates(String internalStoragePath, ExchangeRate rates) {
		
		FileInputStream fis = null;
		try {
			fis = openFileInput(internalStoragePath);
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
				if ( is != null ) is.close(); } catch (IOException e) { }
		}
		
		return ret;
	}
	
	private CurrencyInfo parseEuro(File file) {		
		Log.v(TAG, "@parseEuro");
	
		CurrencyInfo currency = null;
		
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while((line = br.readLine()) != null) {
//				<em>1 EUR = </em> <strong>1.95583 BGN</strong>
				Pattern p = Pattern.compile("1\\sEUR(.[^\\d]*)(\\d+.\\d+)\\sBGN(.*)", Pattern.CASE_INSENSITIVE);
				Matcher m = p.matcher(line);
				if (m.find()) {
//					Log.v(TAG, "MATCH1: " + m.group(0));
//					Log.v(TAG, "MATCH2: " + m.group(1));
//					Log.v(TAG, "MATCH3: " + m.group(2));
//					Log.v(TAG, "MATCH4: " + m.group(3));

					currency = new CurrencyInfo();
					currency.setName("Euro");
					currency.setCode("EUR");
					currency.setRatio("1");
					currency.setRate(m.group(2));
//					currency.setReverseRate("0");
					currency.setExtraInfo(DateFormat.format("dd.MM.yyyy", Calendar.getInstance()).toString());
//					rates.add(currency);
					
					break; // no need to search for more
				}
			}
		}
		catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		finally {
			try { if ( br != null ) br.close(); } catch(IOException e) { }
		}
		
		return currency;
	}
	
	private boolean injectCurrency(CurrencyInfo currency, File xmlFile) {
		Log.v(TAG, "@injectCurrency");
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList items = doc.getElementsByTagName(Defs.XML_TAG_ROW);
			
			// find position to insert
			for(int i = 0; i < items.getLength(); i++) {
				Node node = items.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					boolean added = false;
					
					NodeList childList = element.getElementsByTagName(Defs.XML_TAG_CODE);
					if (childList.getLength() > 0) {
						
						Element child = (Element) childList.item(0);
					
						int result = child.getTextContent().compareTo(currency.getCode());
						if (result > 0) {
							Element newElement = doc.createElement(Defs.XML_TAG_ROW);
							
							Element subElement = doc.createElement(Defs.XML_TAG_GOLD);
							subElement.setTextContent("1");
							newElement.appendChild(subElement);
							
							subElement = doc.createElement(Defs.XML_TAG_NAME);
							subElement.setTextContent(currency.getName());
							newElement.appendChild(subElement);
							
							subElement = doc.createElement(Defs.XML_TAG_CODE);
							subElement.setTextContent(currency.getCode());
							newElement.appendChild(subElement);
							
							subElement = doc.createElement(Defs.XML_TAG_RATIO);
							subElement.setTextContent(currency.getRatio());
							newElement.appendChild(subElement);
							
							subElement = doc.createElement(Defs.XML_TAG_REVERSERATE);
							subElement.setTextContent(currency.getReverseRate());
							newElement.appendChild(subElement);
							
							subElement = doc.createElement(Defs.XML_TAG_RATE);
							subElement.setTextContent(currency.getRate());
							newElement.appendChild(subElement);
							
							subElement = doc.createElement(Defs.XML_TAG_EXTRAINFO);
							subElement.setTextContent(currency.getExtraInfo());
							newElement.appendChild(subElement);
							
							subElement = doc.createElement(Defs.XML_TAG_F_STAR);
							subElement.setTextContent("");
							newElement.appendChild(subElement);
							
							// insert into DOM
							node.getParentNode().insertBefore(newElement, node);
							
							added = true;
							Log.v(TAG, "ADDED before " + child.getTextContent());
							break;
						}
						else if (result == 0) {
							added = true;
							Log.w(TAG, "EUR currency already in XML! Injection failed.");
							break; // special case, EUR already added !
						}
					}
					
					if (added)
						break; // break the loop					
				}
			}
			
			// save the modified file
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//			StreamResult result = new StreamResult(new StringWriter());
			StreamResult result = new StreamResult(xmlFile);
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, result);
//			String xmlString = result.getWriter().toString();
//			Log.v(TAG, xmlString);
			
			return true;
		}
		catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		
		return false;
	}
	
	String getResString(int id) {
		return this.getResources().getString(id);
	}

}
