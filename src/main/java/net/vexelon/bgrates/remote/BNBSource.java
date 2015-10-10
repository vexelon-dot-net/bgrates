package net.vexelon.bgrates.remote;

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
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;

import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.db.models.CurrencyData;

public class BNBSource implements Source {

	public final static String XML_TAG_ROWSET = "ROWSET";
	public final static String XML_TAG_ROW = "ROW";
	public final static String XML_TAG_GOLD = "GOLD";
	public final static String XML_TAG_NAME = "NAME_";
	public final static String XML_TAG_CODE = "CODE";
	public final static String XML_TAG_RATIO = "RATIO";
	public final static String XML_TAG_REVERSERATE = "REVERSERATE";
	public final static String XML_TAG_RATE = "RATE";
	public final static String XML_TAG_EXTRAINFO = "EXTRAINFO";
	public final static String XML_TAG_CURR_DATE = "CURR_DATE";
	public final static String XML_TAG_TITLE = "TITLE";
	public final static String XML_TAG_F_STAR = "F_STAR";

	// private final static String TAG = Defs.LOG_TAG;
	List<CurrencyData> listCurrencyData;
	private CurrencyData currencyData;
	private String text;

	public BNBSource() {
		listCurrencyData = new ArrayList<CurrencyData>();
	}

	public List<CurrencyData> getRatesFromUrl(String ratesUrl) throws SourceException {
		XmlPullParserFactory factory = null;
		XmlPullParser parser = null;
		URL url = null;
		int header = 0;
		try {
			url = new URL(ratesUrl);
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

			parser.setInput(is, Charsets.UTF_8.name());

			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {

				String tagname = parser.getName();
				switch (eventType) {
				case XmlPullParser.START_TAG:
					if (tagname.equalsIgnoreCase(XML_TAG_ROW)) {
						if (header == 0) {
							header = 1;
						} else {
							header = 2;
						}
						// create a new instance of CurrencyData
						if (header > 1)
							currencyData = new CurrencyData();
					}
					break;

				case XmlPullParser.TEXT:
					if (header > 1) {
						text = parser.getText();
					}
					break;

				case XmlPullParser.END_TAG:
					if (header > 1) {
						if (tagname.equalsIgnoreCase(XML_TAG_ROW)) {
							// add employee object to list
							listCurrencyData.add(currencyData);
						} else if (tagname.equalsIgnoreCase(XML_TAG_GOLD)) {
							currencyData.setGold(Integer.parseInt(text));
						} else if (tagname.equalsIgnoreCase(XML_TAG_NAME)) {
							currencyData.setName(text);
							eventType = parser.next();// ?
						} else if (tagname.equalsIgnoreCase(XML_TAG_CODE)) {
							currencyData.setCode(text);
						} else if (tagname.equalsIgnoreCase(XML_TAG_RATIO)) {
							currencyData.setRatio(Integer.parseInt(text));
						} else if (tagname.equalsIgnoreCase(XML_TAG_REVERSERATE)) {
							currencyData.setReverseRate(Float.parseFloat(text));
						} else if (tagname.equalsIgnoreCase(XML_TAG_RATE)) {
							currencyData.setRate(Float.parseFloat(text));
						} else if (tagname.equalsIgnoreCase(XML_TAG_EXTRAINFO)) {
							currencyData.setExtraInfo(text);
						} else if (tagname.equalsIgnoreCase(XML_TAG_CURR_DATE)) {
							DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
							Date currencyDate = null;
							try {
								currencyDate = df.parse(text);
							} catch (ParseException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							currencyData.setCurrDate(currencyDate);
						} else if (tagname.equalsIgnoreCase(XML_TAG_TITLE)) {
							currencyData.setTitle(text);
						} else if (tagname.equalsIgnoreCase(XML_TAG_F_STAR)) {
							currencyData.setfStar(Integer.parseInt(text));
						}
					}
					break;

				default:
					break;
				}
				eventType = parser.next();
			}
			return listCurrencyData;
		} catch (Exception e) {
			throw new SourceException("Failed loading currencies from BNB source!", e);
		}
	}

	@Override
	public Map<String, List<CurrencyData>> downloadRates() throws SourceException {
		Map<String, List<CurrencyData>> result = Maps.newHashMap();
		result.put(Defs.LANG_EN, getRatesFromUrl(Defs.URL_BNB_FORMAT_EN));
		result.put(Defs.LANG_BG, getRatesFromUrl(Defs.URL_BNB_FORMAT_BG));
		return result;
	}

}
