package net.vexelon.bgrates;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ConvertActivity extends Activity {
	
	private final static String TAG = Defs.LOG_TAG;
	private ExchangeRate _myRates;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.convert);
		
		_myRates = getIntent().getParcelableExtra(Defs.INT_EXCHANGERATES);

		// convert From
		Spinner spinner = (Spinner)findViewById(R.id.SpinnerCurrencies);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, _myRates.currenciesToStringArray());
		spinner.setAdapter(adapter);
		
		// convert To
		Spinner spinnerHow = (Spinner)findViewById(R.id.SpinnerConvertHow);
		adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, _myRates.currenciesToStringArray() );
		spinnerHow.setAdapter(adapter);
		
	}

	void setText(int id, String text) {
		TextView tx = (TextView)findViewById(id);
		if ( tx != null )
			tx.setText(text);
	}	
}
