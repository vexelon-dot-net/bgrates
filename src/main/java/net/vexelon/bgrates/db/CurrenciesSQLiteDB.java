package net.vexelon.bgrates.db;

import net.vexelon.bgrates.Defs;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CurrenciesSQLiteDB extends SQLiteOpenHelper {

	// Table Create Statements
	// TABLE_CURRENCY table create statement
	private static final String CREATE_TABLE_CURRENCY = String
			.format("create table %s(%s integer primary key autoincrement, %s integer not null, %s text not null, %s text not null, %s integer not null, %s text, %s text not null, %s text, %s text not null, %s text, %s integer not null, %s text);",
					Defs.TABLE_CURRENCY, Defs.COLUMN_ID, Defs.COLUMN_GOLD, Defs.COLUMN_NAME, Defs.COLUMN_CODE,
					Defs.COLUMN_RATIO, Defs.COLUMN_REVERSERATE, Defs.COLUMN_RATE, Defs.COLUMN_EXTRAINFO,
					Defs.COLUMN_CURR_DATE, Defs.COLUMN_TITLE, Defs.COLUMN_F_STAR, Defs.COLUMN_LOCALE);

	// TABLE_CURRENCY_DATE table create statement
	private static final String CREATE_TABLE_CURRENCY_DATE = String.format(
			"create table %s(%s integer primary key autoincrement, %s text not null, %s text not null);",
			Defs.TABLE_CURRENCY_DATE, Defs.COLUMN_ID, Defs.COLUMN_CURR_DATE, Defs.COLUMN_LOCALE);

	// CREATE_TABLE_FIXED_CURRENCY table create statement
	public static final String CREATE_TABLE_FIXED_CURRENCY = String
			.format("create table %s(%s integer primary key autoincrement, %s integer not null, %s text not null, %s text not null, %s integer not null, %s text, %s text not null, %s text, %s text not null, %s text, %s integer not null, %s text);",
					Defs.TABLE_FIXED_CURRENCY, Defs.COLUMN_ID, Defs.COLUMN_GOLD, Defs.COLUMN_NAME, Defs.COLUMN_CODE,
					Defs.COLUMN_RATIO, Defs.COLUMN_REVERSERATE, Defs.COLUMN_RATE, Defs.COLUMN_EXTRAINFO,
					Defs.COLUMN_CURR_DATE, Defs.COLUMN_TITLE, Defs.COLUMN_F_STAR, Defs.COLUMN_LOCALE);

	public CurrenciesSQLiteDB(Context context) {
		super(context, Defs.DATABASE_NAME, null, Defs.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// creating required tables
		database.execSQL(CREATE_TABLE_CURRENCY);
		database.execSQL(CREATE_TABLE_CURRENCY_DATE);
		database.execSQL(CREATE_TABLE_FIXED_CURRENCY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		Log.i(Defs.LOG_TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
		switch (oldVersion) {
		case 1:
			/**
			 * Upgrade from database v1 to v2
			 */
			database.execSQL(CREATE_TABLE_FIXED_CURRENCY);
			break;
		default:
			Log.w(Defs.LOG_TAG, "Unknown old db version=" + oldVersion);
			break;
		}
	}

}