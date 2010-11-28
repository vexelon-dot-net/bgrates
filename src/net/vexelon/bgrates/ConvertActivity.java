/*
 * The MIT License
 * 
 * Copyright (c) 2010 Petar Petrov
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
package net.vexelon.bgrates;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

import net.vexelon.bgrates.ConvertRow.RowType;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ConvertActivity extends Activity {
	
	private final static String TAG = Defs.LOG_TAG;
	private ExchangeRate _myRates = null;
	private ArrayList<ConvertRow> _rows = null;
	private int[] _visibleCurrencies = null;
	private int _curSelectedRowId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.convert);
		
		// read rates from intent bundle
		_myRates = getIntent().getParcelableExtra(Defs.INT_EXCHANGERATES);
		
		_rows = new ArrayList<ConvertRow>(Defs.MAX_CONVERT_ROWS);
		_rows.add(new ConvertRow(R.id.Spinner4, R.id.EditText4, RowType.RowOthers));
		_rows.add(new ConvertRow(R.id.Spinner3, R.id.EditText3, RowType.RowOthers));
		_rows.add(new ConvertRow(R.id.Spinner2, R.id.EditText2, RowType.RowOthers));
		_rows.add(new ConvertRow(R.id.Spinner1, R.id.EditText1, RowType.RowBGN));
		
		// init dropdown convert options
		for (ConvertRow row : _rows) {
			_curSelectedRowId = row.getRowId(); // should select the Top one
			initRow(row);
		}
		
		// load last saved settings and adjust
		loadSettings();
		
		if ( _visibleCurrencies != null ) {
//			this.runOnUiThread(new Runnable() {
//				@Override
//				public void run() {
					int i = 0;
					for (ConvertRow row : _rows) {
						final Spinner spinner = (Spinner)findViewById(row.getSpinnerId());
						spinner.setSelection(_visibleCurrencies[i++]);
					}
//				}
//			}); // runOnUiThread			
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		saveSettings();
	}
	
	/**
	 * Read previously selected currencies for convert
	 */
	private void loadSettings() {
		SharedPreferences prefs = this.getSharedPreferences(Defs.CONV_PREFS_NAME, 0);
		
		int count = prefs.getInt(Defs.CONV_PREFS_KEY_CONVITEMS_COUNT, Defs.MAX_CONVERT_ROWS);
		_visibleCurrencies = new int[count];
		
		for( int i = 0; i < count; i++ ) {
			String key = Defs.CONV_PREFS_KEY_ITEM + i;
			_visibleCurrencies[i] = prefs.getInt(key, 0);
		}
	}
	
	/**
	 * Write currently selected currencies for convert
	 */
	private void saveSettings() {
		SharedPreferences prefs = this.getSharedPreferences(Defs.CONV_PREFS_NAME, 0);
		SharedPreferences.Editor editor = prefs.edit();
		
		editor.putInt(Defs.CONV_PREFS_KEY_CONVITEMS_COUNT, Defs.MAX_CONVERT_ROWS);
		int i = 0;
		for (ConvertRow row : _rows) {
			Spinner spinner = (Spinner)findViewById(row.getSpinnerId());
			String key = Defs.CONV_PREFS_KEY_ITEM + i++;
			editor.putInt(key, spinner.getSelectedItemPosition());
		}
		
		editor.commit();
	}
	
	/**
	 * Creates and inits UI elements (spinner & edittext) from logical row
	 * @param ConvertRow - initialized ConvertRow object
	 */
	private void initRow(ConvertRow row) {
		
		String[] items = null;
		
		switch(row.getRowType()) {
		case RowBGN:
			items = new String[] { "BGN" };
			
			// init the edit text box
			setResText(row.getEditTextId(), "1.00"); // default
			
			break;
			
		case RowOthers:
		default:
			items = _myRates.currenciesToStringArray();
			
			// init the edit text box
			setResText(row.getEditTextId(), "0.00");
			
			break;
		}
		
		// init the spinner
		final int thisRowId = row.getRowId();
		//Log.d(TAG, "Creating row " + thisRowId);
		
		Spinner spinner = (Spinner)findViewById(row.getSpinnerId());
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
//				this, android.R.layout.simple_spinner_item, items );
		ConvertCurrencyAdapter adapter = new ConvertCurrencyAdapter(this, android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long listRow) {
				// calculate and refresh
				updateResult(_curSelectedRowId);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing
			}
		});	
		
		// init edit text
		EditText editText = (EditText)findViewById(row.getEditTextId());
		editText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				_curSelectedRowId = thisRowId;
				updateResult(_curSelectedRowId);
				return false;
			}
			
		});
		editText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				_curSelectedRowId = thisRowId;
				updateResult(_curSelectedRowId);
			}
		});
	}
	
	/**
	 * Do convert calculations and display results
	 * @param rowId - the logical row which activated the update
	 */
	private void updateResult(int rowId) {
		//Log.d(TAG, "Update Result, caller row is " + rowId);
		
		// find caller row
		ConvertRow callerRow = null;
		
		for (ConvertRow row : _rows) {
			if ( row.getRowId() == rowId ) {
				callerRow = row;
				break;
			}
		}
		
		if ( callerRow == null ) { // something went wrong !
			//Log.e(TAG, "Failed to find caller row !");
			return;
		}
		
		Spinner callerRowSpinner = (Spinner)findViewById(callerRow.getSpinnerId());
		String callerRowCode = (String) callerRowSpinner.getItemAtPosition(callerRowSpinner.getSelectedItemPosition());
		//Log.d(TAG, "Caller Row Code is " + callerRowCode);
		
		BigDecimal sum;
		MathContext mc = new MathContext(Defs.SCALE_CALCULATIONS);

		try {
			sum = new BigDecimal( getResText(callerRow.getEditTextId()).toString(), mc );
			//Log.d(TAG, "Sum is " + sum.toPlainString());
		}
		catch(NumberFormatException e) {
			//Log.e(TAG, e.toString());
			return; // invalid number
		}
		
		BigDecimal rate, ratio, result;
		CurrencyInfo currency = null;
		
		try {
			
			// update all rows (except the caller row)
			for (ConvertRow row : _rows) {
				if ( row.getRowId() != callerRow.getRowId() ) {
					
					switch(row.getRowType()) {
					case RowBGN:
						//Log.d(TAG, "Updating RowBGN !");
						
						// convert from caller row currency to BGN currency
						currency = _myRates.getCurrencyByCode(callerRowCode);
						rate = new BigDecimal(currency.getRate(), mc);
						ratio = new BigDecimal(currency.getRatio(), mc);
						try {
							result = rate.divide(ratio, mc).multiply(sum, mc); //result = rate / ratio * sum;
						}
						catch(Exception e) {
							Log.d(TAG, e.toString());
							result = BigDecimal.ZERO;
						}
						//Log.d(TAG, String.format("rate: %1$s, ratio: %2$s, result: %3$s, sum: %4$s", rate.toPlainString(), ratio.toPlainString(), result.toPlainString(), sum.toPlainString()));
						setResText(row.getEditTextId(), Utils.scaleNumber(result, Defs.SCALE_SHOW_SHORT));
						break;
						
					case RowOthers:
						
						// get selected currency from row's spinner
						Spinner spinner = (Spinner)findViewById(row.getSpinnerId());
						String code = (String) spinner.getItemAtPosition(spinner.getSelectedItemPosition());
						//Log.d(TAG, "Updating RowOthers - " + code);
						
						if ( callerRow.getRowType() == RowType.RowBGN ) { // simply convert from BGN currency to selected row currency
							currency = _myRates.getCurrencyByCode(code);
							rate = new BigDecimal(currency.getRate(), mc);
							ratio = new BigDecimal(currency.getRatio(), mc);
							try {
								result = sum.divide(rate, mc).multiply(ratio, mc);
							}
							catch(Exception e) {
								Log.e(TAG, e.toString());
								result = BigDecimal.ZERO;
							}
							setResText(row.getEditTextId(), Utils.scaleNumber(result, Defs.SCALE_SHOW_SHORT));
						}
						else { // more complex calculations
							
							// Step 1 - Convert from caller row currency to BGN currency
							currency = _myRates.getCurrencyByCode(callerRowCode);
							rate = new BigDecimal(currency.getRate(), mc);
							ratio = new BigDecimal(currency.getRatio(), mc);
							try {
								result = rate.divide(ratio, mc).multiply(sum, mc);
							}
							catch(Exception e) {
								Log.e(TAG, e.toString());
								result = BigDecimal.ZERO;
							}
							//sum = result;
							
							// Step 2 - Convert from BGN currency to selected row currency
							currency = _myRates.getCurrencyByCode(code);
							rate = new BigDecimal(currency.getRate(), mc);
							ratio = new BigDecimal(currency.getRatio(), mc);
							result = result.multiply(ratio.divide(rate, mc), mc); //result = sum * ratio / rate;
							
							// Step 3 - Display results
							setResText(row.getEditTextId(), Utils.scaleNumber(result, Defs.SCALE_SHOW_SHORT));							
						}
						break;
					} // end switch
				} // end if
			} // end for			
		} catch (NumberFormatException e) {
			//Log.e(TAG, e.getMessage());
		}		
	}
	

	private void setResText(int id, CharSequence text) {
		TextView tx = (TextView)findViewById(id);
		if ( tx != null )
			tx.setText(text);
	}
	
	private CharSequence getResText(int id) {
		TextView tx = (TextView)findViewById(id);
		if ( tx != null )
			return tx.getText();
		
		return null;
	}
	
	private void appendResText(int id, CharSequence text) {
		TextView tx = (TextView)findViewById(id);
		if ( tx != null )
			tx.setText(tx.getText().toString() + text);
	}	
}
