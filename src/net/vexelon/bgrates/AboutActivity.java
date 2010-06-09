package net.vexelon.bgrates;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class AboutActivity extends Activity {
	
	private final static String TAG = Defs.LOG_TAG;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		Log.v(TAG, "@onCreate()");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		init();
		
	}
	
	private void init() {
		
		ImageView icLogo = (ImageView)findViewById(R.id.about_logo);
		icLogo.setImageResource(R.drawable.icon);
		
	}

}
