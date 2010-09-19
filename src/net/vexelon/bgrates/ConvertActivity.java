package net.vexelon.bgrates;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

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
	
	enum ConvertOptions {
		ConvertToBGN,
		ConvertFromBGN
	};
	
	private final static String TAG = Defs.LOG_TAG;
	private ExchangeRate _myRates = null;
	private String _selCodeFrom = "";
	private String _selCodeTo = "";
	private ConvertOptions _convertOption = ConvertOptions.ConvertToBGN;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.convert);
		
		// read rates from intent bundle
		_myRates = getIntent().getParcelableExtra(Defs.INT_EXCHANGERATES);
		
		// init dropdown convert options
		initSpinners(_convertOption);
		
		// set dropdown actions
		Spinner spinnerFrom = (Spinner)findViewById(R.id.SpinnerCurFrom);
		spinnerFrom.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long row) {
				
				_selCodeFrom = parent.getItemAtPosition(pos).toString();
				
				// calculate and refresh
				updateResult(_convertOption);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing
			}
		});
		
		// convert To
		Spinner spinnerTo = (Spinner)findViewById(R.id.SpinnerCurTo);
		spinnerTo.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long row) {

				_selCodeTo = parent.getItemAtPosition(pos).toString();
				
				// calculate and refresh
				updateResult(_convertOption);				
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// do nothing
			}
		});
		
		// switch convert button
		Button btnSwitch = (Button) findViewById(R.id.ButtonSwitch);
		btnSwitch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// toggle convert type
				
				switch(_convertOption) {
				case ConvertFromBGN:
					_convertOption = ConvertOptions.ConvertToBGN;
					break;
				case ConvertToBGN:
					_convertOption = ConvertOptions.ConvertFromBGN;
					break;
				}
				
				initSpinners(_convertOption);
			}
		});		

		// create buttons & functionality
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
		Button btnDel = (Button) findViewById(R.id.ButtonBack);
		btnDel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CharSequence value = getResText(R.id.EditTextFrom);
				if ( value.length() > 1 )
					setResText(R.id.EditTextFrom, value.subSequence(0, value.length() - 1));
				else
					setResText(R.id.EditTextFrom, "0");
				
				updateResult(_convertOption);
			}
		});
		
		// set defaults
		setResText(R.id.EditTextFrom, "0");
		setResText(R.id.EditTextTo, "0");
	}
	
	private void initSpinners(ConvertOptions convertOption) {
		
		String[] itemsFrom = null;
		String[] itemsTo = null;
		
		switch(convertOption) {
		case ConvertFromBGN:
			itemsFrom = new String[] { "BGN" };
			itemsTo = _myRates.currenciesToStringArray();
			break;
			
		case ConvertToBGN:
		default:
			itemsFrom = _myRates.currenciesToStringArray();
			itemsTo = new String[] { "BGN" };				
			break;
		}
		
		// convert From
		Spinner spinnerFrom = (Spinner)findViewById(R.id.SpinnerCurFrom);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, itemsFrom );
		spinnerFrom.setAdapter(adapter);

		// convert To
		Spinner spinnerTo = (Spinner)findViewById(R.id.SpinnerCurTo);
		adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, itemsTo );
		spinnerTo.setAdapter(adapter);
		
	}
	
	private void updateResult(ConvertOptions convertOption) {
		// do convert calculations and display
		
		if ( _selCodeTo.equals(_selCodeFrom) ) // invalid update
			return;
		
		CurrencyInfo currency = null;
		BigDecimal sum, rate, ratio, result = new BigDecimal(0.0);
		MathContext mc = new MathContext(3);		
		
		try {
			sum = new BigDecimal( getResText(R.id.EditTextFrom).toString() );
		}
		catch(NumberFormatException e) {
			Log.e(TAG, e.toString());
			return; // invalid number
		}
		
		try {
		
			switch(convertOption) {
			case ConvertFromBGN:
				currency = _myRates.getCurrencyByCode(_selCodeTo);
				rate = new BigDecimal(currency.getRate(), mc);
				ratio = new BigDecimal(currency.getRatio(), mc);
				//result = sum * ratio / rate;
				result = sum.multiply(ratio.divide(rate, mc), mc);
				break;
				
			case ConvertToBGN:
				currency = _myRates.getCurrencyByCode(_selCodeFrom);
				rate = new BigDecimal(currency.getRate(), mc);
				ratio = new BigDecimal(currency.getRatio(), mc);
				//result = rate / ratio * sum;
				result = rate.divide(ratio, mc).multiply(sum, mc);
				break;
			}
		
		} catch (NumberFormatException e) {
			Log.e(TAG, e.getMessage());
		}		
		
		setResText(R.id.EditTextTo, result.round(new MathContext(2, RoundingMode.UP)).toPlainString());	
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
				
				updateResult(_convertOption);
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
