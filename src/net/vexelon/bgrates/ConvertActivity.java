package net.vexelon.bgrates;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ConvertActivity extends Activity {
	
	private final static String TAG = Defs.LOG_TAG;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.convert);
		
		//Bundle bundle = getIntent().getP
		HeaderInfo headerInfo = (HeaderInfo) getIntent().getParcelableExtra(Defs.INT_HEADERINFO);
		Parcelable[] results = getIntent().getParcelableArrayExtra(Defs.INT_CURRENCIES);
		CurrencyInfo[] currencies = null;
		if ( results != null ) {
			currencies = new CurrencyInfo[results.length];
			int i = 0;
			for (Parcelable parcelable : results) {
				currencies[i++] = (CurrencyInfo) parcelable;
			}
		}
		ExchangeRate myRates = new ExchangeRate(headerInfo, currencies);

		// currencies to work with
		setText(R.id.TextViewConvert,"Select currency to convert from:");
		Spinner spinner = (Spinner)findViewById(R.id.SpinnerCurrencies);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, myRates.currenciesToStringArray());
		spinner.setAdapter(adapter);
		
		// convert method
		setText(R.id.TextViewToFrom,"Convert method:");
		Spinner spinnerHow = (Spinner)findViewById(R.id.SpinnerConvertHow);
		adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, new String[] {"From", "To"} );
		spinnerHow.setAdapter(adapter);
		
		// sum nfo
		
		// result box
		setText(R.id.TextViewResult,"Result:");
		
		
	}

	void setText(int id, String text) {
		TextView tx = (TextView)findViewById(id);
		if ( tx != null )
			tx.setText(text);
	}	
}
