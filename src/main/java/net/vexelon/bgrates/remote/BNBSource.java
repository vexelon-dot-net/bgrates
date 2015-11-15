package net.vexelon.bgrates.remote;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;

import net.vexelon.bgrates.db.models.CurrencyData;
import net.vexelon.bgrates.db.models.CurrencyLocales;
import net.vexelon.bgrates.utils.IOUtils;

public class BNBSource implements Source {

	// Addresses on BNB for get on XML file
	public final static String URL_BNB_FORMAT_BG = "http://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERForeignCurrencies/?download=xml&lang=BG";
	public final static String URL_BNB_FORMAT_EN = "http://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERForeignCurrencies/?download=xml&lang=EN";
	public final static String URL_BNB_INDEX = "http://www.bnb.bg/index.htm";
	public final static String URI_CACHE_NAME_INDEXHTM = "BGRatesDownloadCacheHTM";

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

	private CurrencyData currencyData;
	private String text;

	public BNBSource() {
	}

	public List<CurrencyData> getRatesFromUrl(CurrencyLocales localeName, String ratesUrl) throws SourceException {
		List<CurrencyData> listCurrencyData = Lists.newArrayList();
		InputStream is = null;
		XmlPullParserFactory factory = null;
		XmlPullParser parser = null;
		URL url = null;
		int header = 0;
		Date currencyDate = new Date();
		try {
			url = new URL(ratesUrl);
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				// read error and throw it to caller
				is = httpConn.getErrorStream();
				throw new SourceException(new String(ByteStreams.toByteArray(is), Charsets.UTF_8.name()));
			}
			is = httpConn.getInputStream();
			// getEuroValue();

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
						if (header > 1) {
							currencyData = new CurrencyData();
							// defaults
							currencyData.setRate("0");
							currencyData.setReverseRate("0");
						}
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
							currencyData.setReverseRate(text);
						} else if (tagname.equalsIgnoreCase(XML_TAG_RATE)) {
							currencyData.setRate(text);
						} else if (tagname.equalsIgnoreCase(XML_TAG_EXTRAINFO)) {
							currencyData.setExtraInfo(text);
						} else if (tagname.equalsIgnoreCase(XML_TAG_CURR_DATE)) {
							DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
							// Date currencyDate = new Date();//move up
							try {
								currencyDate = df.parse(text);
							} catch (ParseException e1) {
								e1.printStackTrace();
								// use default (today)
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

			listCurrencyData.add(setEuroCurrency(localeName, currencyDate));

			return listCurrencyData;
		} catch (Exception e) {
			throw new SourceException("Failed loading currencies from BNB source!", e);
		} finally {
			IOUtils.closeQuitely(is);
		}
	}

	private CurrencyData setEuroCurrency(CurrencyLocales currencyName, Date currencyDate) throws SourceException {
		CurrencyData euroValue = new CurrencyData();
		String euro = getEuroValue();

		euroValue.setGold(1);
		if (currencyName == CurrencyLocales.BG) {
			euroValue.setName("Евро");
		} else {
			euroValue.setName("Euro");
		}
		euroValue.setCode("EUR");
		euroValue.setRatio(1);
		euroValue.setReverseRate("0.511292");
		euroValue.setRate(euro.substring(0, 7));
		euroValue.setCurrDate(currencyDate);
		euroValue.setfStar(0);

		return euroValue;
	}

	private String getEuroValue() throws SourceException {
		InputStream is = null;
		URL url = null;
		try {
			url = new URL(URL_BNB_INDEX);
			URLConnection connection = url.openConnection();
			connection.setDoInput(true);
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			if (httpConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				// read error and throw it to caller
				is = httpConn.getErrorStream();
				throw new SourceException(new String(ByteStreams.toByteArray(is), Charsets.UTF_8.name()));
			}
			is = httpConn.getInputStream();
			Document doc = Jsoup.parse(is, Charsets.UTF_8.name(), URL_BNB_INDEX);

			Element element = doc.select("div#more_information > div.box > div.top > div > ul > li").first();

			Element euroValue = element.getElementsByTag("strong").first();
			// String euroValuReturn = euroValue.text();
			return euroValue.text();

		} catch (Exception e) {
			throw new SourceException("Failed loading currencies from BNB source!", e);
		} finally {
			IOUtils.closeQuitely(is);
		}
	}

	@Override
	public Map<CurrencyLocales, List<CurrencyData>> downloadRates() throws SourceException {
		Map<CurrencyLocales, List<CurrencyData>> result = Maps.newHashMap();
		result.put(CurrencyLocales.EN, getRatesFromUrl(CurrencyLocales.EN, URL_BNB_FORMAT_EN));
		result.put(CurrencyLocales.BG, getRatesFromUrl(CurrencyLocales.BG, URL_BNB_FORMAT_BG));
		return result;
	}

}
