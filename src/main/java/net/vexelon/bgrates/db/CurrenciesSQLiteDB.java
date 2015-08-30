package net.vexelon.bgrates.db;

import net.vexelon.bgrates.Defs;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CurrenciesSQLiteDB extends SQLiteOpenHelper {

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + Defs.TABLE_CURRENCY + "(" + Defs.COLUMN_ID
			+ " integer primary key autoincrement, " + Defs.COLUMN_GOLD + " integer not null, " + Defs.COLUMN_NAME
			+ " text not null, " + Defs.COLUMN_CODE + " text not null, " + Defs.COLUMN_RATIO + " integer not null, "
			+ Defs.COLUMN_REVERSERATE + " integer, " + Defs.COLUMN_RATE + " integer not null, " + Defs.COLUMN_EXTRAINFO
			+ " text, " + Defs.COLUMN_CURR_DATE + " text not null, " + Defs.COLUMN_TITLE + " text, "
			+ Defs.COLUMN_F_STAR + " integer not null" + ");";

	public CurrenciesSQLiteDB(Context context) {
		super(context, Defs.DATABASE_NAME, null, Defs.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(CurrenciesSQLiteDB.class.getName(), "Upgrading database from version " + oldVersion + " to "
				+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + Defs.TABLE_CURRENCY);
		onCreate(db);
	}

}