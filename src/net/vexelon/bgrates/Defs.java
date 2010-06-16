package net.vexelon.bgrates;

public class Defs {

	final static String LOG_TAG = "net.vexelon.bgrates";
	final static String PREFS_NAME = "BGratesPreferences";
	final static String PREFS_KEY_LASTUPDATE = "lastUpdate";
	final static String URL_BNB_FORMAT = "http://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERForeignCurrencies/?download=xml&lang=%s";
	final static String URL_BNB_SUFFIX_BG = "BG";
	final static String URL_BNB_SUFFIX_EN = "EN";
	
	final static int MAX_RATE_CHARS_SIZE = 8;
	final static int MAX_TOAST_INFO_TIME = 2500;
	
	final static String XML_TAG_ROW = "ROW";
	final static String XML_TAG_NAME = "NAME_";
	final static String XML_TAG_CODE = "CODE";
	final static String XML_TAG_RATIO = "RATIO";
	final static String XML_TAG_REVERSERATE = "REVERSERATE";
	final static String XML_TAG_RATE = "RATE";
	final static String XML_TAG_EXTRAINFO = "EXTRAINFO";
	final static String XML_TAG_CURR_DATE = "CURR_DATE";
	final static String XML_TAG_TITLE = "TITLE";
	
	final static int MENU_REFRESH = 10;
	final static int MENU_BG_RATES = 12;
	final static int MENU_EN_RATES = 14; 
	final static int MENU_ABOUT = 20;
	
	final static int ACTIVITYRESULT_CLOSE = 100;

}
