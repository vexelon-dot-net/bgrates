package net.vexelon.bgrates.db;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.db.models.CurrencyData;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteDataSource implements DataSource {

	// Database fields
	private SQLiteDatabase database;
	private CurrenciesSQLiteDB dbHelper;
	private String[] allColumns = { Defs.COLUMN_ID, Defs.COLUMN_GOLD, Defs.COLUMN_NAME, Defs.COLUMN_CODE,
			Defs.COLUMN_RATIO, Defs.COLUMN_REVERSERATE, Defs.COLUMN_RATE, Defs.COLUMN_EXTRAINFO, Defs.COLUMN_CURR_DATE,
			Defs.COLUMN_TITLE, Defs.COLUMN_F_STAR };

	public SQLiteDataSource(Context context) {
		dbHelper = new CurrenciesSQLiteDB(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	private Date parseStringToDate(String date, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.parse(date);
	}

	private String parseDateToString(Date date, String dateFormat) {
		DateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(date);
	}

	public void addRates(List<CurrencyData> rates) throws DataSourceException {

		ContentValues values = new ContentValues();
		for (int i = 0; i < rates.size(); i++) {

			values.put(Defs.COLUMN_GOLD, rates.get(i).getGold());
			values.put(Defs.COLUMN_NAME, rates.get(i).getName());
			values.put(Defs.COLUMN_CODE, rates.get(i).getCode());
			values.put(Defs.COLUMN_RATIO, rates.get(i).getRatio());
			values.put(Defs.COLUMN_REVERSERATE, rates.get(i).getReverseRate());
			values.put(Defs.COLUMN_RATE, rates.get(i).getRate());
			values.put(Defs.COLUMN_EXTRAINFO, rates.get(i).getExtraInfo());
			values.put(Defs.COLUMN_CURR_DATE, parseDateToString(rates.get(i).getCurrDate(), "yyyy-MM-dd"));
			values.put(Defs.COLUMN_TITLE, rates.get(i).getTitle());
			values.put(Defs.COLUMN_F_STAR, rates.get(i).getfStar());

			database.insert(Defs.TABLE_CURRENCY, null, values);
			values = new ContentValues();
		}
	}

	public List<Date> getAvailableRatesDates() throws DataSourceException {
		List<Date> resultCurrency = new ArrayList<Date>();
		String[] tableColumns = new String[] { Defs.COLUMN_CURR_DATE };

		Cursor cursor = database.query(true, Defs.TABLE_CURRENCY, tableColumns, null, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			try {
				resultCurrency.add(parseStringToDate(cursor.getString(cursor.getColumnIndex(Defs.COLUMN_CURR_DATE)),
						"yyyy-MM-dd"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				throw new DataSourceException(e);
			}

			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return resultCurrency;
	}

	public List<CurrencyData> getRates(Date dateOfCurrency) {
		List<CurrencyData> resultCurrency = new ArrayList<CurrencyData>();
		String whereClause = "curr_date = ? ";
		String[] whereArgs = new String[] { parseDateToString(dateOfCurrency, "yyyy-MM-dd") };

		Cursor cursor = database.query(Defs.TABLE_CURRENCY, allColumns, whereClause, whereArgs, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CurrencyData comment = cursorToCurrency(cursor);
			resultCurrency.add(comment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return resultCurrency;

	}

	public List<CurrencyData> getRates() {
		List<CurrencyData> currencies = new ArrayList<CurrencyData>();

		Cursor cursor = database.query(Defs.TABLE_CURRENCY, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			CurrencyData comment = cursorToCurrency(cursor);
			currencies.add(comment);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return currencies;
	}

	private CurrencyData cursorToCurrency(Cursor cursor) {
		CurrencyData currency = new CurrencyData();
		currency.setGold(cursor.getInt(cursor.getColumnIndex(Defs.COLUMN_GOLD)));
		currency.setName(cursor.getString(cursor.getColumnIndex(Defs.COLUMN_NAME)));
		currency.setCode(cursor.getString(cursor.getColumnIndex(Defs.COLUMN_CODE)));
		currency.setRatio(cursor.getInt(cursor.getColumnIndex(Defs.COLUMN_RATIO)));
		currency.setReverseRate(cursor.getFloat(cursor.getColumnIndex(Defs.COLUMN_REVERSERATE)));
		currency.setRate(cursor.getFloat(cursor.getColumnIndex(Defs.COLUMN_RATE)));
		currency.setExtraInfo(cursor.getString(cursor.getColumnIndex(Defs.COLUMN_EXTRAINFO)));
		try {
			currency.setCurrDate(parseStringToDate(cursor.getString(cursor.getColumnIndex(Defs.COLUMN_CURR_DATE)),
					"yyyy-MM-dd"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		currency.setTitle(cursor.getString(cursor.getColumnIndex(Defs.COLUMN_TITLE)));
		currency.setfStar(cursor.getInt(cursor.getColumnIndex(Defs.COLUMN_F_STAR)));
		return currency;
	}
}
