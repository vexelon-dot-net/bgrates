/*
 * The MIT License
 * 
 * Copyright (c) 2015 Petar Petrov
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
package net.vexelon.bgrates.ui.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.R;
import net.vexelon.bgrates.db.models.old.CurrencyInfo;
import net.vexelon.bgrates.db.models.old.ExchangeRate;
import net.vexelon.bgrates.db.models.old.HeaderInfo;
import net.vexelon.bgrates.ui.UIUtils;
import net.vexelon.bgrates.ui.components.CurrencyListAdapter;

public class CurrenciesFragment extends AbstractFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		init(rootView);
		return rootView;
	}

	private void init(View view) {
		final Context context = getActivity();
		final ListView lv = (ListView) view.findViewById(R.id.ListView01);

		// XXX: Refactor code below

		// load locally stored raw resource
		ExchangeRate rates = new ExchangeRate();
		// Default - try to load locally stored raw resource
		InputStream is = this.getResources().openRawResource(R.raw.exchangerates);
		if (!parseRates(is, rates)) {
			// something's really wrong !
			UIUtils.showAlertDialog(getActivity(), R.string.dlg_parse_error_msg, R.string.dlg_parse_error_title);
		}

		// populate ListView UI
		CurrencyListAdapter adapter = new CurrencyListAdapter(context, R.layout.currency_row_layout, rates.getItems());

		lv.setAdapter(adapter);
		// lv.setOnItemClickListener(new OnItemClickListener() {
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3) {
		// CurrencyInfo ci = (CurrencyInfo) lv.getItemAtPosition(arg2);
		// if (ci != null) {
		// // Log.d(TAG, "Old Rates data: " + _oldRates.getTimeStamp()
		// // + " New rates date: " + _myRates.getTimeStamp());
		// if (_oldRates != null &&
		// !_oldRates.getTimeStamp().equals(_myRates.getTimeStamp())) {
		// CurrencyInfo oldCurrencyRate =
		// _oldRates.getCurrencyByCode(ci.getCode());
		// // some currencies are new, and now old records exist!
		// if (oldCurrencyRate != null) {
		// // message =
		// // String.format("%s\t\t%s\t%s\n%s\t\t%s\t%s",
		// // oldCurrencyRate.getExtraInfo(),
		// // oldCurrencyRate.getRatio(),
		// // oldCurrencyRate.getRate(),
		// // ci.getExtraInfo(), ci.getRatio(), ci.getRate() );
		// Intent intent = new Intent(_context, RateInfoActivity.class);
		// intent.putExtra(Defs.INTENT_FLAG_ID,
		// ExchangeRate.getResourceFromCode(ci));
		// intent.putExtra(Defs.INTENT_OLD_RATEINFO,
		// String.format("%s %s %s", oldCurrencyRate.getExtraInfo(),
		// oldCurrencyRate.getRatio(), oldCurrencyRate.getRate()));
		// intent.putExtra(Defs.INTENT_NEW_RATEINFO,
		// String.format("%s %s %s", ci.getExtraInfo(), ci.getRatio(),
		// ci.getRate()));
		// intent.putExtra(Defs.INTENT_NEW_RATEINFO_TENDENCY_ICONID,
		// ExchangeRate.getResourceFromTendency(ci.getTendency()));
		// startActivity(intent);
		// }
		// } else {
		// Toast.makeText(_context, String.format("%s:\t%s", ci.getName(),
		// ci.getCode()),
		// Defs.MAX_TOAST_INFO_TIME).show();
		// }
		// }
		// }
		// });
	}

	private boolean parseRates(String internalStoragePath, ExchangeRate rates) {

		FileInputStream fis = null;
		try {
			fis = getActivity().openFileInput(internalStoragePath);
			return parseRates(fis, rates);
		} catch (FileNotFoundException e) {
			// Log.e(TAG, "Faild to parse file!", e);
		}
		return false;
	}

	private boolean parseRates(File xmlFile, ExchangeRate rates) {

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(xmlFile);
			return parseRates(fis, rates);
		} catch (FileNotFoundException e) {
			// Log.e(TAG, "Faild to parse file!", e);
		}
		return false;
	}

	private boolean parseRates(InputStream fis, ExchangeRate rates) {
		// Log.v(TAG, "@parseRates");

		boolean ret = false;

		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(fis, null);

			int eventType = xpp.getEventType();
			boolean parsingRow = false, parsingHeader = false;
			CurrencyInfo curInfo = null;
			HeaderInfo headerInfo = null;

			while (eventType != XmlPullParser.END_DOCUMENT) {

				String tagName = null;

				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					// Log.d(TAG, "Creating new ExchangeRate() object ...");
					// rates = new ExchangeRate();
					rates.clear();
					break;

				case XmlPullParser.START_TAG:
					tagName = xpp.getName();

					// Log.d(TAG, "Reading TAG=" + tagName);

					// parse ROW
					if (tagName.equals(Defs.XML_TAG_ROW) && rates.isHeaderAvailable()) {
						parsingRow = true;
						curInfo = new CurrencyInfo();
					} else if (tagName.equals(Defs.XML_TAG_ROW) && !rates.isHeaderAvailable()) {
						parsingHeader = true;
						headerInfo = new HeaderInfo();
					}

					if (parsingRow) {
						if (tagName.equals(Defs.XML_TAG_NAME)) {
							curInfo.setName(xpp.nextText());
						} else if (tagName.equals(Defs.XML_TAG_CODE)) {
							curInfo.setCode(xpp.nextText());
						} else if (tagName.equals(Defs.XML_TAG_RATIO)) {
							curInfo.setRatio(xpp.nextText());
						} else if (tagName.equals(Defs.XML_TAG_RATE)) {
							curInfo.setRate(xpp.nextText());
						} else if (tagName.equals(Defs.XML_TAG_REVERSERATE)) {
							curInfo.setReverseRate(xpp.nextText());
						}
						// Bugfix for <EXTRAINFO> XML tag
						// This tag seems to no longer exist per currency ROW in
						// the XML source
						// Instead the info is now added in a <CURR_DATE> tag
						else if (tagName.equals(Defs.XML_TAG_CURR_DATE)) {
							curInfo.setExtraInfo(xpp.nextText());
						}
						// keep <EXTRAINFO> just in case <CURR_DATE> cannot be
						// found for some reason
						else if (tagName.equals(Defs.XML_TAG_EXTRAINFO)) {
							curInfo.setExtraInfo(xpp.nextText());
						}
					} else if (parsingHeader) {
						if (tagName.equals(Defs.XML_TAG_NAME)) {
							headerInfo.setName(xpp.nextText());
						} else if (tagName.equals(Defs.XML_TAG_CODE)) {
							headerInfo.setCode(xpp.nextText());
						} else if (tagName.equals(Defs.XML_TAG_RATIO)) {
							headerInfo.setRatio(xpp.nextText());
						} else if (tagName.equals(Defs.XML_TAG_RATE)) {
							headerInfo.setRate(xpp.nextText());
						} else if (tagName.equals(Defs.XML_TAG_REVERSERATE)) {
							headerInfo.setReverseRate(xpp.nextText());
						} else if (tagName.equals(Defs.XML_TAG_EXTRAINFO)) {
							headerInfo.setExtraInfo(xpp.nextText());
						} else if (tagName.equals(Defs.XML_TAG_CURR_DATE)) {
							headerInfo.setCurDate(xpp.nextText());
						} else if (tagName.equals(Defs.XML_TAG_TITLE)) {
							headerInfo.setTitle(xpp.nextText());
						}
					}

					break;

				case XmlPullParser.END_TAG:
					tagName = xpp.getName();

					if (tagName.equals(Defs.XML_TAG_ROW) && parsingRow) {
						parsingRow = false;
						rates.add(curInfo);
					} else if (tagName.equals(Defs.XML_TAG_ROW) && parsingHeader) {
						// Log.v(TAG, "Adding currency header info ...");
						parsingHeader = false;
						rates.addHeader(headerInfo);
					}
					break;
				}

				// advance to next tag
				eventType = xpp.next();
			}

			ret = true;

		} catch (Exception e) {
			Log.e(Defs.LOG_TAG, "Error while parsing XML !", e);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
			}
		}

		return ret;
	}

}
