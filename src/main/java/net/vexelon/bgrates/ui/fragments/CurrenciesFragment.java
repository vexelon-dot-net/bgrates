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
import net.vexelon.bgrates.db.models.CurrencyData;
import net.vexelon.bgrates.remote.BNBSource;
import net.vexelon.bgrates.remote.Source;
import net.vexelon.bgrates.remote.SourceException;
import net.vexelon.bgrates.ui.UIUtils;
import net.vexelon.bgrates.ui.components.CurrencyListAdapter;
import net.vexelon.bgrates.utils.DateTimeUtils;

public class CurrenciesFragment extends AbstractFragment {

	private ListView listView1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		init(rootView);
		updateRates();
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
			updateRates();
			setRefreshActionButtonState(true);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void init(View view) {
		listView1 = (ListView) view.findViewById(R.id.ListView01);
	}

	/**
	 * Reloads currencies from a remote source.
	 */
	public void updateRates() {
		new UpdateRatesTask().execute();
	}

	private class UpdateRatesTask extends AsyncTask<Void, Void, List<CurrencyData>> {

		private Activity activity;
		private boolean updateOK = false;

		public UpdateRatesTask() {
			activity = CurrenciesFragment.this.getActivity();
		}

		@Override
		protected List<CurrencyData> doInBackground(Void... params) {
			// TODO: Invoke remote source ...
			List<CurrencyData> ratesList = Lists.newArrayList();
			try {
				// Source source = new LocalRawSource(getActivity());
				Source source = new BNBSource();
				ratesList = source.fetchRates();
				updateOK = true;
			} catch (SourceException e) {
				Log.e(Defs.LOG_TAG, "Error loading rates from RAW file 11!", e);
			}
			return ratesList;
		}

		@Override
		protected void onPostExecute(List<CurrencyData> result) {
			// notifyListeners(Notifications.UPDATE_RATES_DONE);
			setRefreshActionButtonState(false);
			if (updateOK) {
				CurrencyListAdapter adapter = new CurrencyListAdapter(activity, R.layout.currency_row_layout, result);
				listView1.setAdapter(adapter);
				TextView tv = (TextView) activity.findViewById(R.id.tvLastUpdate);
				tv.setText(DateTimeUtils.toString(activity, new Date()));

				// lv.setOnItemClickListener(new OnItemClickListener() {
				// @Override
				// public void onItemClick(AdapterView<?> arg0, View arg1, int
				// arg2,
				// long arg3) {
				// CurrencyInfo ci = (CurrencyInfo) lv.getItemAtPosition(arg2);
				// if (ci != null) {
				// // Log.d(TAG, "Old Rates data: " + _oldRates.getTimeStamp()
				// // + " New rates date: " + _myRates.getTimeStamp());
				// if (_oldRates != null &&
				// !_oldRates.getTimeStamp().equals(_myRates.getTimeStamp())) {
				// CurrencyInfo oldCurrencyRate =
				// _oldRates.getCurrencyByCode(ci.getCode());
				// // some currencies are new, and now old records exist!
				// if (oldCurrencyRate != null) {
				// // message =
				// // String.format("%s\t\t%s\t%s\n%s\t\t%s\t%s",
				// // oldCurrencyRate.getExtraInfo(),
				// // oldCurrencyRate.getRatio(),
				// // oldCurrencyRate.getRate(),
				// // ci.getExtraInfo(), ci.getRatio(), ci.getRate() );
				// Intent intent = new Intent(_context, RateInfoActivity.class);
				// intent.putExtra(Defs.INTENT_FLAG_ID,
				// ExchangeRate.getResourceFromCode(ci));
				// intent.putExtra(Defs.INTENT_OLD_RATEINFO,
				// String.format("%s %s %s", oldCurrencyRate.getExtraInfo(),
				// oldCurrencyRate.getRatio(), oldCurrencyRate.getRate()));
				// intent.putExtra(Defs.INTENT_NEW_RATEINFO,
				// String.format("%s %s %s", ci.getExtraInfo(), ci.getRatio(),
				// ci.getRate()));
				// intent.putExtra(Defs.INTENT_NEW_RATEINFO_TENDENCY_ICONID,
				// ExchangeRate.getResourceFromTendency(ci.getTendency()));
				// startActivity(intent);
				// }
				// } else {
				// Toast.makeText(_context, String.format("%s:\t%s",
				// ci.getName(),
				// ci.getCode()),
				// Defs.MAX_TOAST_INFO_TIME).show();
				// }
				// }
				// }
				// });
			} else {
				UIUtils.showAlertDialog(activity, R.string.dlg_parse_error_msg, R.string.dlg_parse_error_title);
			}
		}

	}

}
