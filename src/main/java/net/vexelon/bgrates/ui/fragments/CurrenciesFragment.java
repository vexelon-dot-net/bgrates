/*
 * The MIT License
 * 
 * Copyright (c) 2015 Petar Petrov
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
package net.vexelon.bgrates.ui.fragments;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.R;
import net.vexelon.bgrates.db.DataSource;
import net.vexelon.bgrates.db.DataSourceException;
import net.vexelon.bgrates.db.SQLiteDataSource;
import net.vexelon.bgrates.db.models.CurrencyData;
import net.vexelon.bgrates.remote.BNBSource;
import net.vexelon.bgrates.remote.Source;
import net.vexelon.bgrates.remote.SourceException;
import net.vexelon.bgrates.ui.UIUtils;
import net.vexelon.bgrates.ui.components.CurrencyListAdapter;
import net.vexelon.bgrates.utils.DateTimeUtils;

public class CurrenciesFragment extends AbstractFragment {

	private ListView lvCurrencies;
	private TextView tvLastUpdate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		init(rootView);
		reloadRates(false);
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// add refresh currencies menu option
		inflater.inflate(R.menu.currencies, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_refresh) {
			reloadRates(true);
			setRefreshActionButtonState(true);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void init(View view) {
		lvCurrencies = (ListView) view.findViewById(R.id.ListView01);
		tvLastUpdate = (TextView) view.findViewById(R.id.tvLastUpdate);
	}

	/**
	 * Populates the list of currencies
	 * 
	 * @param result
	 */
	private void updateCurrenciesListView(List<CurrencyData> result) {
		final Activity activity = getActivity();
		CurrencyListAdapter adapter = new CurrencyListAdapter(activity, R.layout.currency_row_layout, result);
		lvCurrencies.setAdapter(adapter);
		tvLastUpdate.setText(DateTimeUtils.toString(activity, new Date()));
	}

	/**
	 * Reloads currencies from a remote source.
	 * 
	 * @param useRemoteSource
	 */
	public void reloadRates(boolean useRemoteSource) {
		if (!useRemoteSource) {
			try (DataSource source = new SQLiteDataSource()) {
				source.connect(getActivity());
				List<CurrencyData> ratesList = source.getRates();
				if (!ratesList.isEmpty()) {
					Log.v(Defs.LOG_TAG, "Displaying rates from database...");
					updateCurrenciesListView(ratesList);
				} else {
					useRemoteSource = true;
				}
			} catch (DataSourceException e) {
				// TODO: Add UI error msg
				Log.e(Defs.LOG_TAG, "Could not load currencies from database!", e);
			} catch (IOException e) {
				// We don't throw any IOException
				e.printStackTrace();
			}
		}
		if (useRemoteSource) {
			new UpdateRatesTask().execute();
		}
	}

	private class UpdateRatesTask extends AsyncTask<Void, Void, List<CurrencyData>> {

		private Activity activity;
		private boolean updateOK = false;

		public UpdateRatesTask() {
			activity = CurrenciesFragment.this.getActivity();
		}

		@Override
		protected void onPreExecute() {
			// TODO: show updating msg
		}

		@Override
		protected List<CurrencyData> doInBackground(Void... params) {
			List<CurrencyData> ratesList = Lists.newArrayList();
			try {
				Log.v(Defs.LOG_TAG, "Loading rates from remote source...");
				Source source = new BNBSource();
				ratesList = source.fetchRates();
				updateOK = true;
			} catch (SourceException e) {
				Log.e(Defs.LOG_TAG, "Could not laod rates from remote!", e);
			}
			return ratesList;
		}

		@Override
		protected void onPostExecute(List<CurrencyData> result) {
			// notifyListeners(Notifications.UPDATE_RATES_DONE);
			setRefreshActionButtonState(false);
			if (updateOK) {
				updateCurrenciesListView(result);
			} else {
				UIUtils.showAlertDialog(activity, R.string.dlg_parse_error_msg, R.string.dlg_parse_error_title);
			}
		}

	}

}
