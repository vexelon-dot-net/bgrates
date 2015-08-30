package net.vexelon.bgrates.remote;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.common.collect.Lists;

import android.app.Activity;
import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.R;
import net.vexelon.bgrates.db.models.CurrencyData;
import net.vexelon.bgrates.db.models.HeaderInfo;

/**
 * Sample implementation of reading locally stored raw XML rates.
 *
 */
public class LocalRawSource implements Source {

	private Activity activity;

	public LocalRawSource(Activity activity) {
		this.activity = activity;
	}

	@Override
	public List<CurrencyData> fetchRates() throws SourceException {
		// try to load locally stored raw resource
		InputStream is = activity.getResources().openRawResource(R.raw.exchangerates);
		return loadRates(is);
	}

	private List<CurrencyData> loadRates(InputStream fis) throws SourceException {
		List<CurrencyData> rates = Lists.newArrayList();
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(fis, null);

			int eventType = xpp.getEventType();
			boolean parsingRow = false, parsingHeader = false;
			CurrencyData curData = null;
			HeaderInfo headerInfo = null;
			boolean isHeaderAvailable = false;

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
					if (tagName.equals(Defs.XML_TAG_ROW) && isHeaderAvailable) {
						parsingRow = true;
						curData = new CurrencyData();
					} else if (tagName.equals(Defs.XML_TAG_ROW) && !isHeaderAvailable) {
						parsingHeader = true;
						headerInfo = new HeaderInfo();
					}

					if (parsingRow) {
						if (tagName.equals(Defs.XML_TAG_NAME)) {
							curData.setName(xpp.nextText());
						} else if (tagName.equals(Defs.XML_TAG_CODE)) {
							curData.setCode(xpp.nextText());
						} else if (tagName.equals(Defs.XML_TAG_RATIO)) {
							curData.setRatio(Integer.parseInt(xpp.nextText()));
						} else if (tagName.equals(Defs.XML_TAG_RATE)) {
							curData.setRate(Float.parseFloat(xpp.nextText()));
						} else if (tagName.equals(Defs.XML_TAG_REVERSERATE)) {
							curData.setReverseRate(Float.parseFloat(xpp.nextText()));
						}
						// Bugfix for <EXTRAINFO> XML tag
						// This tag seems to no longer exist per currency ROW in
						// the XML source
						// Instead the info is now added in a <CURR_DATE> tag
						else if (tagName.equals(Defs.XML_TAG_CURR_DATE)) {
							curData.setExtraInfo(xpp.nextText());
						}
						// keep <EXTRAINFO> just in case <CURR_DATE> cannot be
						// found for some reason
						else if (tagName.equals(Defs.XML_TAG_EXTRAINFO)) {
							curData.setExtraInfo(xpp.nextText());
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
						rates.add(curData);
					} else if (tagName.equals(Defs.XML_TAG_ROW) && parsingHeader) {
						// Log.v(TAG, "Adding currency header info ...");
						parsingHeader = false;
						isHeaderAvailable = true;
						// rates.addHeader(headerInfo);
					}
					break;
				}

				// advance to next tag
				eventType = xpp.next();
			}
		} catch (Exception e) {
			throw new SourceException(e);
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
			}
		}
		return rates;
	}

}
