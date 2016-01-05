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

	public final static int TOAST_INFO_TIME = 4000;
	public final static int TOAST_ERR_TIME = 3000;
	public final static int SCALE_SHOW_LONG = 5;
	public final static int SCALE_SHOW_SHORT = 2;
	public final static int SCALE_CALCULATIONS = 10;
	public final static String BGN_CODE = "BGN";

	// //////Tsvetoslav
	// Time for request to BNB
	public static final long NOTIFY_INTERVAL = 3600 * 1000; // 1 hour
	// Parameters which used into SQLite//
	// Name of DB
	public static final String DATABASE_NAME = "currencies.db";
	// Version of DB
	public static final int DATABASE_VERSION = 1;
	// table name
	public static final String TABLE_CURRENCY = "currencies";
	public static final String TABLE_CURRENCY_DATE = "currenciesdate";
	public static final String TABLE_FIXED_CURRENCY = "fixedcurrencies";
	// Name of columns in table TABLE_CURRENCY
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
	public static final String COLUMN_LOCALE = "locale";
}
