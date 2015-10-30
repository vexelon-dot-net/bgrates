package net.vexelon.bgrates.db;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.db.models.CurrencyData;
import net.vexelon.bgrates.db.models.CurrencyLocales;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteDataSource implements DataSource {

	private static final String[] ALL_COLUMNS = { Defs.COLUMN_ID, Defs.COLUMN_GOLD, Defs.COLUMN_NAME, Defs.COLUMN_CODE,
			Defs.COLUMN_RATIO, Defs.COLUMN_REVERSERATE, Defs.COLUMN_RATE, Defs.COLUMN_EXTRAINFO, Defs.COLUMN_CURR_DATE,
			Defs.COLUMN_TITLE, Defs.COLUMN_F_STAR };

	// Database fields
	private SQLiteDatabase database;
	private CurrenciesSQLiteDB dbHelper;

	@Override
	public void connect(Context context) throws DataSourceException {
		try {
			dbHelper = new CurrenciesSQLiteDB(context);
			database = dbHelper.getWritableDatabase();
		} catch (SQLException e) {
			throw new DataSourceException("Could not open SQLite database!", e);
		}
	}

	@Override
	public void close() {
		if (dbHelper != null) {
			dbHelper.close();
		}
	}

	private Date parseStringToDate(String date, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.parse(date);
	}

	private String parseDateToString(Date date, String dateFormat) {
		DateFormat formatter = new SimpleDateFormat(dateFormat);
		return formatter.format(date);
	}

	@Override
	public void addRates(Map<CurrencyLocales, List<CurrencyData>> rates) throws DataSourceException {

		ContentValues values = new ContentValues();
		ContentValues valuesDate = new ContentValues();
		for (Map.Entry<CurrencyLocales, List<CurrencyData>> currenciesData : rates.entrySet()) {
			if (!isHaveRates(currenciesData.getKey(), currenciesData.getValue().get(1).getCurrDate())) {
				for (int i = 0; i < currenciesData.getValue().size(); i++) {
					values.put(Defs.COLUMN_GOLD, currenciesData.getValue().get(i).getGold());
					values.put(Defs.COLUMN_NAME, currenciesData.getValue().get(i).getName());
					values.put(Defs.COLUMN_CODE, currenciesData.getValue().get(i).getCode());
					values.put(Defs.COLUMN_RATIO, currenciesData.getValue().get(i).getRatio());
					values.put(Defs.COLUMN_REVERSERATE, currenciesData.getValue().get(i).getReverseRate());
					values.put(Defs.COLUMN_RATE, currenciesData.getValue().get(i).getRate());
					values.put(Defs.COLUMN_EXTRAINFO, currenciesData.getValue().get(i).getExtraInfo());
					values.put(Defs.COLUMN_CURR_DATE,
							parseDateToString(currenciesData.getValue().get(i).getCurrDate(), "yyyy-MM-dd"));
					values.put(Defs.COLUMN_TITLE, currenciesData.getValue().get(i).getTitle());
					values.put(Defs.COLUMN_F_STAR, currenciesData.getValue().get(i).getfStar());
					values.put(Defs.COLUMN_LOCALE, currenciesData.getKey().toString());

					database.insert(Defs.TABLE_CURRENCY, null, values);
					values = new ContentValues();

				}

				valuesDate.put(Defs.COLUMN_CURR_DATE,
						parseDateToString(currenciesData.getValue().get(1).getCurrDate(), "yyyy-MM-dd"));
				valuesDate.put(Defs.COLUMN_LOCALE, currenciesData.getKey().toString());
				database.insert(Defs.TABLE_CURRENCY_DATE, null, valuesDate);

				valuesDate = new ContentValues();
			}
		}
	}

	private Boolean isHaveRates(CurrencyLocales locale, Date dateOfCurrency) {
		String[] tableColumns = new String[] { Defs.COLUMN_CURR_DATE };
		String whereClause = Defs.COLUMN_CURR_DATE + " = ? AND " + Defs.COLUMN_LOCALE + " = ? ";
		String[] whereArgs = new String[] { parseDateToString(dateOfCurrency, "yyyy-MM-dd"), locale.toString() };

		Cursor cursor = database
				.query(Defs.TABLE_CURRENCY_DATE, tableColumns, whereClause, whereArgs, null, null, null);
		if (cursor.moveToFirst()) {
			cursor.close();
			return true;
		} else {
			cursor.close();
			return false;
		}
	}

	@Override
	public List<CurrencyData> getLastRates(CurrencyLocales locale) throws DataSourceException {
		List<CurrencyData> lastRates = new ArrayList<CurrencyData>();
		String[] tableColumns = new String[] { Defs.COLUMN_CURR_DATE };
		String whereClause = Defs.COLUMN_LOCALE + " = ? ";
		String[] whereArgs = new String[] { locale.toString() };

		Cursor cursor = database.query(Defs.TABLE_CURRENCY_DATE, tableColumns, whereClause, whereArgs, null, null,
				Defs.COLUMN_CURR_DATE + " DESC");

		if (cursor.moveToFirst()) {
			String whereClause2 = Defs.COLUMN_CURR_DATE + " = ? AND " + Defs.COLUMN_LOCALE + " = ? ";
			String[] whereArgs2 = new String[] { cursor.getString(cursor.getColumnIndex(Defs.COLUMN_CURR_DATE)),
					locale.toString() };

			Cursor cursor2 = database.query(Defs.TABLE_CURRENCY, ALL_COLUMNS, whereClause2, whereArgs2, null, null,
					null);

			cursor2.moveToFirst();
			while (!cursor2.isAfterLast()) {
				CurrencyData comment = cursorToCurrency(cursor2);
				lastRates.add(comment);
				cursor2.moveToNext();
			}
			// make sure to close the cursor
			cursor2.close();

		}
		cursor.close();

		return lastRates;
	}

	@Override
	public List<Date> getAvailableRatesDates(CurrencyLocales locale) throws DataSourceException {
		List<Date> resultCurrency = new ArrayList<Date>();
		String[] tableColumns = new String[] { Defs.COLUMN_CURR_DATE };
		String whereClause = Defs.COLUMN_LOCALE + " = ? ";
		String[] whereArgs = new String[] { locale.toString() };

		Cursor cursor = database.query(true, Defs.TABLE_CURRENCY_DATE, tableColumns, whereClause, whereArgs, null,
				null, null, null);

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

	@Override
	public List<CurrencyData> getRates(CurrencyLocales locale, Date dateOfCurrency) throws DataSourceException {
		List<CurrencyData> resultCurrency = new ArrayList<CurrencyData>();
		String whereClause = Defs.COLUMN_CURR_DATE + " = ? AND " + Defs.COLUMN_LOCALE + " = ? ";
		String[] whereArgs = new String[] { parseDateToString(dateOfCurrency, "yyyy-MM-dd"), locale.toString() };

		Cursor cursor = database.query(Defs.TABLE_CURRENCY, ALL_COLUMNS, whereClause, whereArgs, null, null, null);

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

	@Override
	public List<CurrencyData> getRates(CurrencyLocales locale) throws DataSourceException {
		List<CurrencyData> currencies = new ArrayList<CurrencyData>();
		String whereClause = Defs.COLUMN_LOCALE + " = ? ";
		String[] whereArgs = new String[] { locale.toString() };

		Cursor cursor = database.query(Defs.TABLE_CURRENCY, ALL_COLUMNS, whereClause, whereArgs, null, null, null);

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
		currency.setReverseRate(cursor.getString(cursor.getColumnIndex(Defs.COLUMN_REVERSERATE)));
		currency.setRate(cursor.getString(cursor.getColumnIndex(Defs.COLUMN_RATE)));
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
