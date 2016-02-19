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
import java.util.Map;

import com.google.common.collect.Maps;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
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
	private String lastUpdateLastValue;

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
	public void onStart() {
		super.onStart();
		final AppSettings appSettings = new AppSettings(getActivity());
		if (appSettings.getSupportNotification() == AppSettings.NOSUPPORT_NOT_ACK) {
			final TextView tvMessage = new TextView(getActivity());
			tvMessage.setText(Html.fromHtml(getString(R.string.pref_nosupport_message)));
			tvMessage.setMovementMethod(LinkMovementMethod.getInstance());
			tvMessage.setPadding(24, 24, 24, 12);

			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.pref_nosupport_title)
					.setPositiveButton(R.string.text_close, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).setCancelable(false).setView(tvMessage).show();
			appSettings.setSupportNotification(AppSettings.NOSUPPORT_ACK);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_refresh:
			reloadRates(true);
			lastUpdateLastValue = tvLastUpdate.getText().toString();
			tvLastUpdate.setText(R.string.last_update_updating_text);
			setRefreshActionButtonState(true);
			return true;
		case R.id.action_sort:
			newSortMenu().show();
			return true;
		case R.id.action_filter:
			newFilterMenu().show();
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

	private AlertDialog newFilterMenu() {
		final AppSettings appSettings = new AppSettings(getActivity());
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.action_filter_title).setSingleChoiceItems(R.array.action_filter_values,
				appSettings.getCurrenciesFilterSelection(), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						appSettings.setCurrenciesFilterSelection(which);
						filterCurrenciesListView(which);
						// notify user
						switch (appSettings.getCurrenciesFilterSelection()) {
						case AppSettings.FILTERBY_ALL:
							Toast.makeText(getActivity(), R.string.action_filter_all, Toast.LENGTH_SHORT).show();
							break;
						case AppSettings.FILTERBY_NONFIXED:
							Toast.makeText(getActivity(), R.string.action_filter_nonfixed, Toast.LENGTH_SHORT).show();
							break;
						case AppSettings.FILTERBY_FIXED:
							Toast.makeText(getActivity(), R.string.action_filter_fixed, Toast.LENGTH_SHORT).show();
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
		AppSettings appSettings = new AppSettings(activity);

		CurrencyListAdapter adapter = new CurrencyListAdapter(activity, R.layout.currency_row_layout, currenciesList,
				appSettings.getCurrenciesPrecision());
		lvCurrencies.setAdapter(adapter);

		// sortCurrenciesListView(appSettings.getCurrenciesSortSelection());
		filterCurrenciesListView(appSettings.getCurrenciesFilterSelection());

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
		adapter.sortBy(new AppSettings(getActivity()).getCurrenciesSortSelection(), sortByAscending);
		adapter.notifyDataSetChanged();
	}

	/**
	 * Filter currencies by rate type
	 *
	 * @param filterBy
	 */
	private void filterCurrenciesListView(final int filterBy) {
		final CurrencyListAdapter adapter = (CurrencyListAdapter) lvCurrencies.getAdapter();
		adapter.getFilter().filter(Integer.toString(filterBy), new Filter.FilterListener() {
			@Override
			public void onFilterComplete(int count) {
				if (count > 0) {
					adapter.sortBy(new AppSettings(getActivity()).getCurrenciesSortSelection(), sortByAscending);
					adapter.notifyDataSetChanged();
				} else {
					adapter.notifyDataSetInvalidated();
				}
			}
		});
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
				ratesList.addAll(source.getLastFixedRates(getSelectedCurrenciesLocale()));
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
		private boolean downloadFixed = false;

		public UpdateRatesTask() {
			activity = CurrenciesFragment.this.getActivity();
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Map<CurrencyLocales, List<CurrencyData>> doInBackground(Void... params) {
			Map<CurrencyLocales, List<CurrencyData>> rates = Maps.newHashMap();
			Date currentYear = DateTimeUtils.getCurrentYear();
			try {
				DataSource dataSource = null;
				try {
					dataSource = new SQLiteDataSource();
					dataSource.connect(activity);
					downloadFixed = dataSource.getFixedRates(getSelectedCurrenciesLocale(), currentYear).isEmpty();
				} catch (DataSourceException e) {
					Log.e(Defs.LOG_TAG, "Could not read fixed currencies from database!", e);
				} finally {
					IOUtils.closeQuitely(dataSource);
				}
				Log.v(Defs.LOG_TAG, "Loading rates from remote source..., downloadFixed=" + downloadFixed);
				Source source = new BNBSource();
				rates = source.downloadRates(downloadFixed);
				updateOK = true;
			} catch (SourceException e) {
				Log.e(Defs.LOG_TAG, "Could not load rates from remote!", e);
			}
			return rates;
		}

		@Override
		protected void onPostExecute(Map<CurrencyLocales, List<CurrencyData>> result) {
			setRefreshActionButtonState(false);
			CurrencyLocales selectedCurrenciesLocale = getSelectedCurrenciesLocale();
			if (updateOK && !result.isEmpty()) {
				DataSource source = null;
				try {
					source = new SQLiteDataSource();
					source.connect(activity);
					source.addRates(result);
					if (!downloadFixed) {
						/**
						 * We have downloaded only the non-fixed currencies, so
						 * we need to fetch
						 * the list of last downloaded fixed currencies and
						 * update the view with all
						 * entries.
						 */
						List<CurrencyData> currenciesList = result.get(selectedCurrenciesLocale);
						currenciesList.addAll(source.getLastFixedRates(selectedCurrenciesLocale));
						updateCurrenciesListView(currenciesList);
						return;
					}
				} catch (DataSourceException e) {
					Log.e(Defs.LOG_TAG, "Could not save currencies to database!", e);
					Toast.makeText(getActivity(), R.string.error_db_load_rates, Defs.TOAST_ERR_TIME).show();
				} finally {
					IOUtils.closeQuitely(source);
				}
				updateCurrenciesListView(result.get(selectedCurrenciesLocale));
			} else {
				tvLastUpdate.setText(lastUpdateLastValue);
				Toast.makeText(getActivity(), R.string.error_download_rates, Defs.TOAST_ERR_TIME).show();
			}
		}

	}

}