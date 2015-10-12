package net.vexelon.bgrates.db;

import net.vexelon.bgrates.Defs;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CurrenciesSQLiteDB extends SQLiteOpenHelper {

	// Table Create Statements
	// TABLE_CURRENCY table create statement
	private static final String CREATE_TABLE_CURRENCY = "create table " + Defs.TABLE_CURRENCY + "(" + Defs.COLUMN_ID
			+ " integer primary key autoincrement, " + Defs.COLUMN_GOLD + " integer not null, " + Defs.COLUMN_NAME
			+ " text not null, " + Defs.COLUMN_CODE + " text not null, " + Defs.COLUMN_RATIO + " integer not null, "
			+ Defs.COLUMN_REVERSERATE + " integer, " + Defs.COLUMN_RATE + " integer not null, " + Defs.COLUMN_EXTRAINFO
			+ " text, " + Defs.COLUMN_CURR_DATE + " text not null, " + Defs.COLUMN_TITLE + " text, "
			+ Defs.COLUMN_F_STAR + " integer not null" + ");";

	// TABLE_CURRENCY_DATE table create statement
	private static final String CREATE_TABLE_CURRENCY_DATE = "create table " + Defs.TABLE_CURRENCY_DATE + "("
			+ Defs.COLUMN_ID + " integer primary key autoincrement, " + Defs.COLUMN_CURR_DATE + " text not null, "
			+ Defs.COLUMN_LANGUAGE + " text not null " + ");";

	public CurrenciesSQLiteDB(Context context) {
		super(context, Defs.DATABASE_NAME, null, Defs.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {

		// creating required tables
		database.execSQL(CREATE_TABLE_CURRENCY);
		database.execSQL(CREATE_TABLE_CURRENCY_DATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		Log.w(CurrenciesSQLiteDB.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + Defs.TABLE_CURRENCY);
		db.execSQL("DROP TABLE IF EXISTS " + Defs.TABLE_CURRENCY_DATE);

		// create new tables
		onCreate(db);
	}

}