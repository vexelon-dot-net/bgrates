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

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;
import net.vexelon.bgrates.AppSettings;
import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.R;
import net.vexelon.bgrates.db.DataSource;
import net.vexelon.bgrates.db.DataSourceException;
import net.vexelon.bgrates.db.SQLiteDataSource;
import net.vexelon.bgrates.db.models.CurrencyData;
import net.vexelon.bgrates.db.models.CurrencyLocales;
import net.vexelon.bgrates.remote.BNBSource;
import net.vexelon.bgrates.remote.Source;
import net.vexelon.bgrates.remote.SourceException;
import net.vexelon.bgrates.ui.components.CurrencyListAdapter;
import net.vexelon.bgrates.utils.DateTimeUtils;
import net.vexelon.bgrates.utils.IOUtils;

public class CurrenciesFragment extends AbstractFragment {

	private static boolean sortByAscending = true;

	private ListView lvCurrencies;
	private TextView tvLastUpdate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		init(rootView);
		return rootView;
	}

	@Override
	public void onResume() {
		reloadRates(false);
		super.onResume();
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
		switch (id) {
		case R.id.action_refresh:
			reloadRates(true);
			setRefreshActionButtonState(true);
			return true;
		case R.id.action_sort:
			newSortMenu().show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void init(View view) {
		lvCurrencies = (ListView) view.findViewById(R.id.list_currencies);
		tvLastUpdate = (TextView) view.findViewById(R.id.text_last_update);
	}

	private AlertDialog newSortMenu() {
		final AppSettings appSettings = new AppSettings(getActivity());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.action_sort_title).setSingleChoiceItems(R.array.action_sort_values,
				appSettings.getCurrenciesSortSelection(), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						sortByAscending = appSettings.getCurrenciesSortSelection() != which ? true : !sortByAscending;
						appSettings.setCurrenciesSortSelection(which);
						sortCurrenciesListView(which);
						// notify user
						switch (appSettings.getCurrenciesSortSelection()) {
						case AppSettings.SORTBY_CODE:
							Toast.makeText(getActivity(),
									sortByAscending ? R.string.action_sort_code_asc : R.string.action_sort_code_desc,
									Toast.LENGTH_SHORT).show();
							break;
						case AppSettings.SORTBY_NAME:
						default:
							Toast.makeText(getActivity(),
									sortByAscending ? R.string.action_sort_name_asc : R.string.action_sort_name_desc,
									Toast.LENGTH_SHORT).show();
							break;
						}
						dialog.dismiss();
					}
				});
		return builder.create();
	}

	/**
	 * Populates the list of currencies
	 * 
	 * @param currenciesList
	 */
	private void updateCurrenciesListView(List<CurrencyData> currenciesList) {
		final Activity activity = getActivity();
		CurrencyListAdapter adapter = new CurrencyListAdapter(activity, R.layout.currency_row_layout, currenciesList);
		lvCurrencies.setAdapter(adapter);
		sortCurrenciesListView(new AppSettings(activity).getCurrenciesSortSelection());
		Date lastUpdateDate = currenciesList.iterator().next().getCurrDate();
		tvLastUpdate.setText(DateTimeUtils.toDateText(activity, lastUpdateDate));
	}

	/**
	 * Sorts currencies by given criteria
	 * 
	 * @param sortBy
	 */
	private void sortCurrenciesListView(final int sortBy) {
		CurrencyListAdapter adapter = (CurrencyListAdapter) lvCurrencies.getAdapter();
		adapter.sort(new Comparator<CurrencyData>() {
			@Override
			public int compare(CurrencyData lhs, CurrencyData rhs) {
				switch (sortBy) {
				case AppSettings.SORTBY_CODE:
					if (sortByAscending) {
						return lhs.getCode().compareToIgnoreCase(rhs.getCode());
					}
					return rhs.getCode().compareToIgnoreCase(lhs.getCode());
				case AppSettings.SORTBY_NAME:
				default:
					if (sortByAscending) {
						return lhs.getName().compareToIgnoreCase(rhs.getName());
					}
					return rhs.getName().compareToIgnoreCase(lhs.getName());
				}
			}
		});
		adapter.notifyDataSetChanged();
	}

	/**
	 * Reloads currencies from a remote source.
	 * 
	 * @param useRemoteSource
	 */
	public void reloadRates(boolean useRemoteSource) {
		if (!useRemoteSource) {
			DataSource source = null;
			try {
				source = new SQLiteDataSource();
				source.connect(getActivity());
				List<CurrencyData> ratesList = source.getLastRates(getSelectedCurrenciesLocale());
				if (!ratesList.isEmpty()) {
					Log.v(Defs.LOG_TAG, "Displaying rates from database...");
					updateCurrenciesListView(ratesList);
				} else {
					useRemoteSource = true;
				}
			} catch (DataSourceException e) {
				Log.e(Defs.LOG_TAG, "Could not load currencies from database!", e);
				Toast.makeText(getActivity(), R.string.error_db_load_rates, Defs.TOAST_ERR_TIME).show();
			} finally {
				IOUtils.closeQuitely(source);
			}
		}
		if (useRemoteSource) {
			setRefreshActionButtonState(true);
			new UpdateRatesTask().execute();
		}
	}

	private class UpdateRatesTask extends AsyncTask<Void, Void, Map<CurrencyLocales, List<CurrencyData>>> {

		private Activity activity;
		private boolean updateOK = false;

		public UpdateRatesTask() {
			activity = CurrenciesFragment.this.getActivity();
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Map<CurrencyLocales, List<CurrencyData>> doInBackground(Void... params) {
			Map<CurrencyLocales, List<CurrencyData>> rates = Maps.newHashMap();
			try {
				Log.v(Defs.LOG_TAG, "Loading rates from remote source...");
				Source source = new BNBSource();
				rates = source.downloadRates();
				updateOK = true;
			} catch (SourceException e) {
				Log.e(Defs.LOG_TAG, "Could not load rates from remote!", e);
			}
			return rates;
		}

		@Override
		protected void onPostExecute(Map<CurrencyLocales, List<CurrencyData>> result) {
			// notifyListeners(Notifications.UPDATE_RATES_DONE);
			setRefreshActionButtonState(false);
			if (updateOK && !result.isEmpty()) {
				DataSource source = null;
				try {
					source = new SQLiteDataSource();
					source.connect(activity);
					source.addRates(result);
				} catch (DataSourceException e) {
					Log.e(Defs.LOG_TAG, "Could not save currencies to database!", e);
					Toast.makeText(getActivity(), R.string.error_db_load_rates, Defs.TOAST_ERR_TIME).show();
				} finally {
					IOUtils.closeQuitely(source);
				}
				updateCurrenciesListView(result.get(getSelectedCurrenciesLocale()));
			} else {
				Toast.makeText(getActivity(), R.string.error_download_rates, Defs.TOAST_ERR_TIME).show();
			}
		}

	}

}