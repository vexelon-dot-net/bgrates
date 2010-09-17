package net.vexelon.bgrates;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ConvertActivity extends Activity {
	
	private final static String TAG = Defs.LOG_TAG;
	private ConvertActivity _context = null;
	private ExchangeRate _myRates = null;
	private CurrencyInfo _selCurrencyFrom = null;
	private CurrencyInfo _selCurrencyTo = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		_context = this;
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.convert);
		
		// read rates from intent bundle
		_myRates = getIntent().getParcelableExtra(Defs.INT_EXCHANGERATES);

		// convert From
		Spinner spinnerFrom = (Spinner)findViewById(R.id.SpinnerCurFrom);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, _myRates.currenciesToStringArray());
		spinnerFrom.setAdapter(adapter);
		spinnerFrom.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long row) {
				
				try {
					String code = parent.getItemAtPosition(pos).toString();
					_selCurrencyFrom = _myRates.getCurrencyByCode(code);
				}
				catch(Exception e) {
					Log.e(TAG, "Error reading item from spinnerFrom!");
				}
				
				// calculate and refresh
				updateResult(); 
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing
			}
		});
		
		// convert To
		Spinner spinnerTo = (Spinner)findViewById(R.id.SpinnerCurTo);
		adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, _myRates.currenciesToStringArray() );
		spinnerTo.setAdapter(adapter);
		spinnerTo.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long row) {
				try {
					String code = parent.getItemAtPosition(pos).toString();
					_selCurrencyTo = _myRates.getCurrencyByCode(code);
				}
				catch(Exception e) {
					Log.e(TAG, "Error reading item from spinnerTo !");
				}
				
				// calculate and refresh
				updateResult(); 				
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing
			}
		});
		
		// buttons functionality
		createButton(R.id.Button00, "0");
		createButton(R.id.Button01, "1");
		createButton(R.id.Button02, "2");
		createButton(R.id.Button03, "3");
		createButton(R.id.Button04, "4");
		createButton(R.id.Button05, "5");
		createButton(R.id.Button06, "6");
		createButton(R.id.Button07, "7");
		createButton(R.id.Button08, "8");
		createButton(R.id.Button09, "9");
		createButton(R.id.ButtonDot, ".");
		
		// delete button
		Button btn = (Button) findViewById(R.id.ButtonBack);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CharSequence value = getResText(R.id.EditTextFrom);
				if ( value.length() > 1 )
					setResText(R.id.EditTextFrom, value.subSequence(0, value.length() - 1));
				else
					setResText(R.id.EditTextFrom, "0");
				
				updateResult();
			}
		});	
		
		// set defaults
		setResText(R.id.EditTextFrom, "0");
		setResText(R.id.EditTextTo, "0");
	}
	
	private void updateResult() {
		// do convert calculations and display

		try {
			Double rate = Double.parseDouble( _selCurrencyFrom.getRate() );
			Double ratio = Double.parseDouble( _selCurrencyFrom.getRatio() );
			Double sum = Double.parseDouble( getResText(R.id.EditTextFrom).toString() );
			
			Double result = rate / ratio * sum;
			setResText(R.id.EditTextTo, result.toString());	
			
		} catch (NumberFormatException e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	private void createButton(int id, final CharSequence value) {
		Button btn = (Button) findViewById(id);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String curValue = getResText(R.id.EditTextFrom).toString();
				if ( curValue.equalsIgnoreCase("0") )
					setResText(R.id.EditTextFrom, value);
				else
					appendResText(R.id.EditTextFrom, value);
				
				updateResult();
			}
		});		
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
