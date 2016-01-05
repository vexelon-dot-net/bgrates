package net.vexelon.bgrates.db;

import net.vexelon.bgrates.Defs;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CurrenciesSQLiteDB extends SQLiteOpenHelper {

	// Table Create Statements
	// TABLE_CURRENCY table create statement
	private static final String CREATE_TABLE_CURRENCY_OLD = "create table " + Defs.TABLE_CURRENCY + "(" + Defs.COLUMN_ID
			+ " integer primary key autoincrement, " + Defs.COLUMN_GOLD + " integer not null, " + Defs.COLUMN_NAME
			+ " text not null, " + Defs.COLUMN_CODE + " text not null, " + Defs.COLUMN_RATIO + " integer not null, "
			+ Defs.COLUMN_REVERSERATE + " text, " + Defs.COLUMN_RATE + " text not null, " + Defs.COLUMN_EXTRAINFO
			+ " text, " + Defs.COLUMN_CURR_DATE + " text not null, " + Defs.COLUMN_TITLE + " text, "
			+ Defs.COLUMN_F_STAR + " integer not null," + Defs.COLUMN_LOCALE + " text " + ");";

	//TODO - Create String.format for all statements
	private static final String CREATE_TABLE_CURRENCY = String.format("create table %s(%s integer primary key autoincrement, %s integer not null, %s text not null, %s text not null, %s integer not null, %s text, %s text not null, %s text, %s text not null, %s text, %s integer not null, %s text);",Defs.TABLE_CURRENCY,Defs.COLUMN_ID,Defs.COLUMN_GOLD,Defs.COLUMN_NAME,Defs.COLUMN_CODE,Defs.COLUMN_RATIO,Defs.COLUMN_REVERSERATE,Defs.COLUMN_RATE,Defs.COLUMN_EXTRAINFO,Defs.COLUMN_CURR_DATE,Defs.COLUMN_TITLE,Defs.COLUMN_F_STAR,Defs.COLUMN_LOCALE);
//	String.format("create table %s(%s integer primary key autoincrement, %s integer not null, ", Defs.TABLE_CURRENCY,Defs.COLUMN_ID);

	// TABLE_CURRENCY_DATE table create statement
	private static final String CREATE_TABLE_CURRENCY_DATE_OLD = "create table " + Defs.TABLE_CURRENCY_DATE + "("
			+ Defs.COLUMN_ID + " integer primary key autoincrement, " + Defs.COLUMN_CURR_DATE + " text not null, "
			+ Defs.COLUMN_LOCALE + " text not null " + ");";
	private static final String CREATE_TABLE_CURRENCY_DATE = String.format("create table %s(%s integer primary key autoincrement, %s text not null, %s text not null);",Defs.TABLE_CURRENCY_DATE,Defs.COLUMN_ID,Defs.COLUMN_CURR_DATE,Defs.COLUMN_LOCALE);

	//CREATE_TABLE_FIXED_CURRENCY table create statement
	//TODO change from private to public
	public static final String CREATE_TABLE_FIXED_CURRENCY_OLD = "create table " + Defs.TABLE_FIXED_CURRENCY+ "(" + Defs.COLUMN_ID
			+ " integer primary key autoincrement, " + Defs.COLUMN_GOLD + " integer not null, " + Defs.COLUMN_NAME
			+ " text not null, " + Defs.COLUMN_CODE + " text not null, " + Defs.COLUMN_RATIO + " integer not null, "
			+ Defs.COLUMN_REVERSERATE + " text, " + Defs.COLUMN_RATE + " text not null, " + Defs.COLUMN_EXTRAINFO
			+ " text, " + Defs.COLUMN_CURR_DATE + " text not null, " + Defs.COLUMN_TITLE + " text, "
			+ Defs.COLUMN_F_STAR + " integer not null," + Defs.COLUMN_LOCALE + " text " + ");";
	public static final String CREATE_TABLE_FIXED_CURRENCY = String.format("create table %s(%s integer primary key autoincrement, %s integer not null, %s text not null, %s text not null, %s integer not null, %s text, %s text not null, %s text, %s text not null, %s text, %s integer not null, %s text);",Defs.TABLE_FIXED_CURRENCY,Defs.COLUMN_ID,Defs.COLUMN_GOLD,Defs.COLUMN_NAME,Defs.COLUMN_CODE,Defs.COLUMN_RATIO,Defs.COLUMN_REVERSERATE,Defs.COLUMN_RATE,Defs.COLUMN_EXTRAINFO,Defs.COLUMN_CURR_DATE,Defs.COLUMN_TITLE,Defs.COLUMN_F_STAR,Defs.COLUMN_LOCALE);



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
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		Log.w(CurrenciesSQLiteDB.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		//TOOD - remove drop Execution
		db.execSQL("DROP TABLE IF EXISTS " + Defs.TABLE_CURRENCY);
		db.execSQL("DROP TABLE IF EXISTS " + Defs.TABLE_CURRENCY_DATE);
		db.execSQL("DROP TABLE IF EXISTS " + Defs.TABLE_FIXED_CURRENCY);

		// create new tables
		onCreate(db);
	}

}