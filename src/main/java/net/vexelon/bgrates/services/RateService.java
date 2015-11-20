package net.vexelon.bgrates.services;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import net.vexelon.bgrates.AppSettings;
import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.db.DataSource;
import net.vexelon.bgrates.db.DataSourceException;
import net.vexelon.bgrates.db.SQLiteDataSource;
import net.vexelon.bgrates.db.models.CurrencyData;
import net.vexelon.bgrates.db.models.CurrencyLocales;
import net.vexelon.bgrates.remote.BNBSource;
import net.vexelon.bgrates.remote.Source;
import net.vexelon.bgrates.remote.SourceException;
import net.vexelon.bgrates.utils.IOUtils;

public class RateService extends Service {

	public List<CurrencyData> listCurrency;

	@Override
	public void onCreate() {

		// TODO Auto-generated method stub
		// Toast.makeText(this, "MyAlarmService.onCreate()",
		// Toast.LENGTH_LONG).show();

	}

	@Override
	public IBinder onBind(Intent intent) {

		// TODO Auto-generated method stub

		// Toast.makeText(this, "MyAlarmService.onBind()",
		// Toast.LENGTH_LONG).show();

		return null;

	}

	@Override
	public void onDestroy() {

		// TODO Auto-generated method stub

		super.onDestroy();

		// Toast.makeText(this, "MyAlarmService.onDestroy()",
		// Toast.LENGTH_LONG).show();

	}

	@Override
	public void onStart(Intent intent, int startId) {

		// TODO Auto-generated method stub

		super.onStart(intent, startId);

		// Toast.makeText(this, "MyAlarmService.onStart()",
		// Toast.LENGTH_LONG).show();
		if (!isCurrenciesToDate()) {

		}
		new DownloadWebpageTask().execute("");

	}

	@Override
	public boolean onUnbind(Intent intent) {

		// TODO Auto-generated method stub

		// Toast.makeText(this, "MyAlarmService.onUnbind()",
		// Toast.LENGTH_LONG).show();

		return super.onUnbind(intent);

	}

	/**
	 * Checks whether have for sysdate currencies.
	 * 
	 * @return: true-Have, false-Haven't
	 */
	private boolean isCurrenciesToDate() {
		Context ctx = RateService.this;
		DataSource source = null;
		try {
			source = new SQLiteDataSource();
			source.connect(ctx);
			listCurrency = source.getRates(getSelectedCurrenciesLocale(), Calendar.getInstance().getTime());
		} catch (DataSourceException e) {
			Log.e(Defs.LOG_TAG, "Could not save currencies to database!", e);
		} finally {
			IOUtils.closeQuitely(source);
		}

		if (listCurrency.size() > 0)
			return true;

		return false;
	}

	private class DownloadWebpageTask extends AsyncTask<String, Void, Map<CurrencyLocales, List<CurrencyData>>> {

		@Override
		protected Map<CurrencyLocales, List<CurrencyData>> doInBackground(String... urls) {
			Map<CurrencyLocales, List<CurrencyData>> rates = Maps.newHashMap();
			try {
				Log.v(Defs.LOG_TAG, "Loading rates from remote source...");
				Source source = new BNBSource();
				rates = source.downloadRates();
			} catch (SourceException e) {
				Log.e(Defs.LOG_TAG, "Could not load rates from remote!", e);
			}
			return rates;
		}

		// onPostExecute results of the AsyncTask - add currencies in DB if not
		// exists
		@Override
		protected void onPostExecute(Map<CurrencyLocales, List<CurrencyData>> result) {
			Context ctx = RateService.this;
			DataSource source = null;
			try {
				source = new SQLiteDataSource();
				source.connect(ctx);
				source.addRates(result);
			} catch (DataSourceException e) {
				Log.e(Defs.LOG_TAG, "Could not save currencies to database!", e);
			} finally {
				IOUtils.closeQuitely(source);
			}

		}
	}

	protected CurrencyLocales getSelectedCurrenciesLocale() {
		Context ctx = RateService.this;
		return new AppSettings(ctx).getCurrenciesLanguage();
	}

}
