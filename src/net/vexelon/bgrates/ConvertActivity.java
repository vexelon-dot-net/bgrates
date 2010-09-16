package net.vexelon.bgrates;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class ConvertActivity extends Activity {
	
	private final static String TAG = Defs.LOG_TAG;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.convert);

		// currencies to work with
		setText(R.id.TextViewConvert,"Select currency to convert from:");
		Spinner spinner = (Spinner)findViewById(R.id.SpinnerCurrencies);
		String[] list = {"USD","EUR","ESP"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, ExchangeRate. );
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
