package net.vexelon.bgrates.remote;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.db.models.CurrencyData;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class BNBSource implements Source {

	// private final static String TAG = Defs.LOG_TAG;
	List<CurrencyData> listCurrencyData;
	private CurrencyData currencyData;
	private String text;
	private int header = 0;// Default value

	public BNBSource() {
		listCurrencyData = new ArrayList<CurrencyData>();
	}

	@Override
	public List<CurrencyData> fetchRates() throws SourceException {
		XmlPullParserFactory factory = null;
		XmlPullParser parser = null;
		URL url = null;
		try {
			url = new URL(Defs.URL_BNB_FORMAT_BG);

			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			InputStream is = null;
			if (httpConn.getResponseCode() >= 400) {
				is = httpConn.getErrorStream();
			} else {
				is = httpConn.getInputStream();
			}
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newPullParser();

			parser.setInput(is, "UTF_8");

			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {

				String tagname = parser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if (tagname.equalsIgnoreCase(Defs.XML_TAG_ROW)) {

						if (header == 0)
							header = 1;
						else
							header = 2;

						// create a new instance of CurrencyData
						if (header > 1)
							currencyData = new CurrencyData();
					}
					break;

				case XmlPullParser.TEXT:
					if (header > 1)
						text = parser.getText();
					break;

				case XmlPullParser.END_TAG:
					if (header > 1) {
						if (tagname.equalsIgnoreCase(Defs.XML_TAG_ROW)) {
							// add employee object to list
							listCurrencyData.add(currencyData);
						} else if (tagname.equalsIgnoreCase(Defs.XML_TAG_GOLD)) {
							currencyData.setGold(Integer.parseInt(text));
						} else if (tagname.equalsIgnoreCase(Defs.XML_TAG_NAME)) {
							currencyData.setName(text);
							eventType = parser.next();// ?
						} else if (tagname.equalsIgnoreCase(Defs.XML_TAG_CODE)) {
							currencyData.setCode(text);
						} else if (tagname.equalsIgnoreCase(Defs.XML_TAG_RATIO)) {
							currencyData.setRatio(Integer.parseInt(text));
						} else if (tagname.equalsIgnoreCase(Defs.XML_TAG_REVERSERATE)) {
							currencyData.setReverseRate(Float.parseFloat(text));
						} else if (tagname.equalsIgnoreCase(Defs.XML_TAG_RATE)) {
							currencyData.setRate(Float.parseFloat(text));
						} else if (tagname.equalsIgnoreCase(Defs.XML_TAG_EXTRAINFO)) {
							currencyData.setExtraInfo(text);
						} else if (tagname.equalsIgnoreCase(Defs.XML_TAG_CURR_DATE)) {
							DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
							Date currencyDate = null;
							try {
								currencyDate = df.parse(text);
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							currencyData.setCurrDate(currencyDate);
						} else if (tagname.equalsIgnoreCase(Defs.XML_TAG_TITLE)) {
							currencyData.setTitle(text);
						} else if (tagname.equalsIgnoreCase(Defs.XML_TAG_F_STAR)) {
							currencyData.setfStar(Integer.parseInt(text));
						}
					}

					break;

				default:
					break;
				}
				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return listCurrencyData;
	}

}
