package net.vexelon.bgrates;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class AboutActivity extends Activity {
	
	private final static String TAG = Defs.LOG_TAG;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.v(TAG, "@onCreate()");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
	}

}
