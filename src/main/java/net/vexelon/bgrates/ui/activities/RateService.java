package net.vexelon.bgrates.ui.activities;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.db.DataSource;
import net.vexelon.bgrates.db.DataSourceException;
import net.vexelon.bgrates.db.SQLiteDataSource;
import net.vexelon.bgrates.db.models.CurrencyData;
import net.vexelon.bgrates.remote.BNBSource;
import net.vexelon.bgrates.remote.Source;
import net.vexelon.bgrates.remote.SourceException;
import net.vexelon.bgrates.utils.IOUtils;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.common.collect.Lists;

public class RateService extends Service {

	public List<CurrencyData> listCurrency;
	private SQLiteDataSource datasource;

	// run on another Thread to avoid crash
	private Handler mHandler = new Handler();
	// timer handling
	private Timer mTimer = null;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onDestroy() {
		mTimer.cancel();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onCreate() {
		// cancel if already existed
		if (mTimer != null) {
			mTimer.cancel();
		} else {
			// recreate new
			mTimer = new Timer();
		}

		// schedule task
		mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, Defs.NOTIFY_INTERVAL);

	}

	class TimeDisplayTimerTask extends TimerTask {

		@Override
		public void run() {
			// run on another thread
			mHandler.post(new Runnable() {

				@Override
				public void run() {
					if (!isCurrenciesToDate())
						new DownloadWebpageTask().execute("");
				}

			});
		}

		/**
		 * Checks whether have for sysdate currencies.
		 * 
		 * @return: true-Have, false-Haven't
		 */
		private boolean isCurrenciesToDate() {

			// Temp code
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			// Temp code

			Context ctx = RateService.this;
			DataSource source = null;
			try {
				source = new SQLiteDataSource();
				source.connect(ctx);
				// listCurrency = source.getRates(cal.getTime());// Temp code
				listCurrency = source.getRates(Calendar.getInstance().getTime());// !!!
				// listCurrency =
				// datasource.getRates(Calendar.getInstance().getTime());
			} catch (DataSourceException e) {
				// TODO: Add UI error msg
				Log.e(Defs.LOG_TAG, "Could not save currencies to database!", e);
			} finally {
				IOUtils.closeQuitely(source);
			}

			if (listCurrency.size() > 0)
				return true;

			return false;
		}

		private class DownloadWebpageTask extends AsyncTask<String, Void, List<CurrencyData>> {

			@Override
			protected List<CurrencyData> doInBackground(String... urls) {
				List<CurrencyData> ratesList = Lists.newArrayList();
				try {
					Log.v(Defs.LOG_TAG, "Loading rates from remote source...");
					Source source = new BNBSource();
					ratesList = source.fetchRates();
				} catch (SourceException e) {
					Log.e(Defs.LOG_TAG, "Could not laod rates from remote!", e);
				}
				return ratesList;
			}

			// onPostExecute displays the results of the AsyncTask.
			@Override
			protected void onPostExecute(List<CurrencyData> result) {
				Context ctx = RateService.this;
				DataSource source = null;
				try {
					source = new SQLiteDataSource();
					source.connect(ctx);
					source.addRates(result);
				} catch (DataSourceException e) {
					// TODO: Add UI error msg
					Log.e(Defs.LOG_TAG, "Could not save currencies to database!", e);
				} finally {
					IOUtils.closeQuitely(source);
				}

			}
		}

	}
}