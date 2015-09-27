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

public class Defs {

	public final static String LOG_TAG = "net.vexelon.bgrates";
	public final static String PREFS_NAME = "BGratesPreferences";
	public final static String PREFS_KEY_LASTUPDATE = "lastUpdate";
	public final static String PREFS_KEY_LASTUPDATE_TIME = "lastUpdateTime";
	public final static String PREFS_KEY_PREV_RATES_FILE = "previousExchgRatesFile";
	public final static String CONV_PREFS_NAME = "BGratesPreferencesConvert";
	public final static String CONV_PREFS_KEY_CONVITEMS_COUNT = "ConvertItemsCount";
	public final static String CONV_PREFS_KEY_ITEM = "ConvertItem_";

	// //////Tsvetoslav
	// Time for request to BNB
	public static final long NOTIFY_INTERVAL = 60 * 1000; // 10 seconds
	// Parameters which used into SQLite//
	// Address on BNB for get on XML file
	public final static String URL_BNB_FORMAT_BG = "http://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERForeignCurrencies/?download=xml&lang=bg";
	// Name of DB
	public static final String DATABASE_NAME = "currencies.db";
	// Version of DB
	public static final int DATABASE_VERSION = 1;
	// table name
	public static final String TABLE_CURRENCY = "currencies";
	// Name of columns in table
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_GOLD = "gold";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_CODE = "code";
	public static final String COLUMN_RATIO = "ratio";
	public static final String COLUMN_REVERSERATE = "reverserate";
	public static final String COLUMN_RATE = "rate";
	public static final String COLUMN_EXTRAINFO = "extrainfo";
	public static final String COLUMN_CURR_DATE = "curr_date";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_F_STAR = "f_star";
	// ////////////////////////////////////////////////////////////////

	public final static String URL_BNB_FORMAT = "http://www.bnb.bg/Statistics/StExternalSector/StExchangeRates/StERForeignCurrencies/?download=xml&lang=%s";
	public final static String URL_BNB_SUFFIX_BG = "BG";
	public final static String URL_BNB_SUFFIX_EN = "EN";
	public final static String URI_CACHE_NAME = "BGRatesDownloadCache";
	public final static String URL_BNB_INDEX = "http://www.bnb.bg/index.htm";
	public final static String URI_CACHE_NAME_INDEXHTM = "BGRatesDownloadCacheHTM";

	public final static String INT_EXCHANGERATES = "int-exch";

	public final static int MAX_RATE_CHARS_SIZE = 8;
	public final static int MAX_TOAST_INFO_TIME = 3150;

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

	public final static int MENU_REFRESH = 10;
	public final static int MENU_BG_RATES = 12;
	public final static int MENU_EN_RATES = 14;
	public final static int MENU_CONVERT = 15;
	public final static int MENU_ABOUT = 20;

	public final static int ACTIVITYRESULT_CLOSE = 100;

	public final static int SCALE_SHOW_LONG = 5;
	public final static int SCALE_SHOW_SHORT = 3;
	public final static int SCALE_CALCULATIONS = 10;

	public final static int MAX_CONVERT_ROWS = 4;

	public final static String INTENT_FLAG_ID = "_FLAG_ID";
	public final static String INTENT_OLD_RATEINFO = "_OLD_RATEINFO";
	public final static String INTENT_NEW_RATEINFO = "_NEW_RATEINFO";
	public final static String INTENT_NEW_RATEINFO_TENDENCY_ICONID = "_INTENT_NEW_RATEINFO_TENDENCY_ICONID";

}
