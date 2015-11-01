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

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.R;
import net.vexelon.bgrates.db.DataSource;
import net.vexelon.bgrates.db.DataSourceException;
import net.vexelon.bgrates.db.SQLiteDataSource;
import net.vexelon.bgrates.db.models.CurrencyData;
import net.vexelon.bgrates.ui.components.ConvertSourceListAdapter;
import net.vexelon.bgrates.ui.components.ConvertTargetListAdapter;
import net.vexelon.bgrates.utils.IOUtils;

public class ConvertFragment extends AbstractFragment {

	public static final int MAX_CONVERT_ROWS = 4;
	private ArrayList<ConvertRow> _rows = null;
	private int[] _visibleCurrencies = null;
	private int _curSelectedRowId = -1;
	//////////

	private Spinner spinnerSourceCurrency;
	private EditText etSourceValue;
	private ListView lvTargetCurrencies;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_convert, container, false);
		init(rootView);
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// add refresh currencies menu option
		inflater.inflate(R.menu.convert, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_addcurrency:
			showAddCurrencyMenu();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void init(View view) {
		final Context context = getActivity();
		// setup source currencies
		spinnerSourceCurrency = (Spinner) view.findViewById(R.id.source_currency);
		DataSource source = null;
		List<CurrencyData> currenciesList = Lists.newArrayList();
		try {
			source = new SQLiteDataSource();
			source.connect(getActivity());
			currenciesList = source.getLastRates(getSelectedCurrenciesLocale());
			if (!currenciesList.isEmpty()) {
				ConvertSourceListAdapter adapter = new ConvertSourceListAdapter(getActivity(),
						android.R.layout.simple_spinner_item, currenciesList);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerSourceCurrency.setAdapter(adapter);
			}
		} catch (DataSourceException e) {
			// TODO: Add UI error msg
			Log.e(Defs.LOG_TAG, "Could not load currencies from database!", e);
		} finally {
			IOUtils.closeQuitely(source);
		}
		// setup source value
		etSourceValue = (EditText) view.findViewById(R.id.text_source_value);
		etSourceValue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				etSourceValue.setSelection(etSourceValue.getText().length());
			}
		});
		// setup target currencies list
		lvTargetCurrencies = (ListView) view.findViewById(R.id.list_target_currencies);
		ConvertTargetListAdapter adapter = new ConvertTargetListAdapter(context, R.layout.convert_target_row_layout,
				currenciesList);
		lvTargetCurrencies.setAdapter(adapter);
	}

	private void showAddCurrencyMenu() {
		// TODO
	}
	/**
	 * Read previously selected currencies for convert
	 */
	// private void loadSettings() {
	// SharedPreferences prefs =
	// getActivity().getSharedPreferences(Defs.CONV_PREFS_NAME, 0);
	//
	// int count = prefs.getInt(Defs.CONV_PREFS_KEY_CONVITEMS_COUNT,
	// MAX_CONVERT_ROWS);
	// _visibleCurrencies = new int[count];
	//
	// for (int i = 0; i < count; i++) {
	// String key = Defs.CONV_PREFS_KEY_ITEM + i;
	// _visibleCurrencies[i] = prefs.getInt(key, 0);
	// }
	// }

	/**
	 * Write currently selected currencies for convert
	 */
	// private void saveSettings() {
	// SharedPreferences prefs =
	// getActivity().getSharedPreferences(Defs.CONV_PREFS_NAME, 0);
	// SharedPreferences.Editor editor = prefs.edit();
	//
	// editor.putInt(Defs.CONV_PREFS_KEY_CONVITEMS_COUNT, MAX_CONVERT_ROWS);
	// int i = 0;
	// for (ConvertRow row : _rows) {
	// Spinner spinner = (Spinner)
	// getActivity().findViewById(row.getSpinnerId());
	// String key = Defs.CONV_PREFS_KEY_ITEM + i++;
	// editor.putInt(key, spinner.getSelectedItemPosition());
	// }
	//
	// editor.commit();
	// }

	/**
	 * Creates and inits UI elements (spinner & edittext) from logical row
	 * 
	 * @param ConvertRow
	 *            - initialized ConvertRow object
	 */
	private void initRow(ConvertRow row) {

		String[] items = null;

		switch (row.getRowType()) {
		case RowBGN:
			items = new String[] { "BGN" };

			// init the edit text box
			setResText(row.getEditTextId(), "1.00"); // default

			break;

		case RowOthers:
		default:

			// init the edit text box
			setResText(row.getEditTextId(), "0.00");

			break;
		}

		// init the spinner
		final int thisRowId = row.getRowId();
		// Log.d(TAG, "Creating row " + thisRowId);

		Spinner spinner = (Spinner) getActivity().findViewById(row.getSpinnerId());
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(
		// this, android.R.layout.simple_spinner_item, items );

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long listRow) {
				// calculate and refresh
				// updateResult(_curSelectedRowId);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing
			}
		});

		// init edit text
		EditText editText = (EditText) getActivity().findViewById(row.getEditTextId());
		editText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				_curSelectedRowId = thisRowId;
				// updateResult(_curSelectedRowId);
				return false;
			}

		});
		editText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_curSelectedRowId = thisRowId;
				// updateResult(_curSelectedRowId);
			}
		});
	}

	/**
	 * Do convert calculations and display results
	 * 
	 * @param rowId
	 *            - the logical row which activated the update
	 */
	/*
	 * private void updateResult(int rowId) {
	 * // Log.d(TAG, "Update Result, caller row is " + rowId);
	 * 
	 * // find caller row
	 * ConvertRow callerRow = null;
	 * 
	 * for (ConvertRow row : _rows) {
	 * if (row.getRowId() == rowId) {
	 * callerRow = row;
	 * break;
	 * }
	 * }
	 * 
	 * if (callerRow == null) { // something went wrong !
	 * // Log.e(TAG, "Failed to find caller row !");
	 * return;
	 * }
	 * 
	 * Spinner callerRowSpinner = (Spinner)
	 * getActivity().findViewById(callerRow.getSpinnerId());
	 * String callerRowCode = (String)
	 * callerRowSpinner.getItemAtPosition(callerRowSpinner.
	 * getSelectedItemPosition());
	 * // Log.d(TAG, "Caller Row Code is " + callerRowCode);
	 * 
	 * BigDecimal sum;
	 * MathContext mc = new MathContext(Defs.SCALE_CALCULATIONS);
	 * 
	 * try {
	 * sum = new BigDecimal(getResText(callerRow.getEditTextId()).toString(),
	 * mc);
	 * // Log.d(TAG, "Sum is " + sum.toPlainString());
	 * } catch (NumberFormatException e) {
	 * Log.e(TAG, "Failed to convert value from String!", e);
	 * return; // invalid number
	 * }
	 * 
	 * BigDecimal rate, ratio, result;
	 * CurrencyInfo currency = null;
	 * 
	 * try {
	 * 
	 * // update all rows (except the caller row)
	 * for (ConvertRow row : _rows) {
	 * if (row.getRowId() != callerRow.getRowId()) {
	 * 
	 * switch (row.getRowType()) {
	 * case RowBGN:
	 * // Log.d(TAG, "Updating RowBGN !");
	 * 
	 * // convert from caller row currency to BGN currency
	 * currency = _myRates.getCurrencyByCode(callerRowCode);
	 * rate = new BigDecimal(currency.getRate(), mc);
	 * ratio = new BigDecimal(currency.getRatio(), mc);
	 * try {
	 * result = rate.divide(ratio, mc).multiply(sum, mc); // result
	 * // =
	 * // rate
	 * // /
	 * // ratio
	 * // *
	 * // sum;
	 * } catch (Exception e) {
	 * // Log.d(TAG, e.toString());
	 * result = BigDecimal.ZERO;
	 * }
	 * // Log.d(TAG,
	 * // String.format("rate: %1$s, ratio: %2$s, result: %3$s,
	 * // sum: %4$s",
	 * // rate.toPlainString(), ratio.toPlainString(),
	 * // result.toPlainString(), sum.toPlainString()));
	 * setResText(row.getEditTextId(), NumberUtils.scaleNumber(result,
	 * Defs.SCALE_SHOW_SHORT));
	 * break;
	 * 
	 * case RowOthers:
	 * 
	 * // get selected currency from row's spinner
	 * Spinner spinner = (Spinner)
	 * getActivity().findViewById(row.getSpinnerId());
	 * String code = (String)
	 * spinner.getItemAtPosition(spinner.getSelectedItemPosition());
	 * // Log.d(TAG, "Updating RowOthers - " + code);
	 * 
	 * if (callerRow.getRowType() == RowType.RowBGN) { // simply
	 * // convert
	 * // from
	 * // BGN
	 * // currency
	 * // to
	 * // selected
	 * // row
	 * // currency
	 * currency = _myRates.getCurrencyByCode(code);
	 * rate = new BigDecimal(currency.getRate(), mc);
	 * ratio = new BigDecimal(currency.getRatio(), mc);
	 * try {
	 * result = sum.divide(rate, mc).multiply(ratio, mc);
	 * } catch (Exception e) {
	 * // Log.e(TAG, e.toString());
	 * result = BigDecimal.ZERO;
	 * }
	 * setResText(row.getEditTextId(), NumberUtils.scaleNumber(result,
	 * Defs.SCALE_SHOW_SHORT));
	 * } else { // more complex calculations
	 * 
	 * // Step 1 - Convert from caller row currency to BGN
	 * // currency
	 * currency = _myRates.getCurrencyByCode(callerRowCode);
	 * rate = new BigDecimal(currency.getRate(), mc);
	 * ratio = new BigDecimal(currency.getRatio(), mc);
	 * try {
	 * result = rate.divide(ratio, mc).multiply(sum, mc);
	 * } catch (Exception e) {
	 * // Log.e(TAG, e.toString());
	 * result = BigDecimal.ZERO;
	 * }
	 * // sum = result;
	 * 
	 * // Step 2 - Convert from BGN currency to selected
	 * // row currency
	 * currency = _myRates.getCurrencyByCode(code);
	 * rate = new BigDecimal(currency.getRate(), mc);
	 * ratio = new BigDecimal(currency.getRatio(), mc);
	 * try {
	 * result = result.multiply(ratio.divide(rate, mc), mc); // result
	 * // =
	 * // sum
	 * // *
	 * // ratio
	 * // /
	 * // rate;
	 * } catch (Exception e) {
	 * // Log.e(TAG, e.toString());
	 * result = BigDecimal.ZERO;
	 * }
	 * 
	 * // Step 3 - Display results
	 * setResText(row.getEditTextId(), NumberUtils.scaleNumber(result,
	 * Defs.SCALE_SHOW_SHORT));
	 * }
	 * break;
	 * } // end switch
	 * } // end if
	 * } // end for
	 * } catch (NumberFormatException e) {
	 * Log.e(TAG, "", e);
	 * }
	 * }
	 */
	private void setResText(int id, CharSequence text) {
		TextView tx = (TextView) getActivity().findViewById(id);
		if (tx != null)
			tx.setText(text);
	}

	private CharSequence getResText(int id) {
		TextView tx = (TextView) getActivity().findViewById(id);
		if (tx != null)
			return tx.getText();

		return null;
	}

	private void appendResText(int id, CharSequence text) {
		TextView tx = (TextView) getActivity().findViewById(id);
		if (tx != null)
			tx.setText(tx.getText().toString() + text);
	}

}
