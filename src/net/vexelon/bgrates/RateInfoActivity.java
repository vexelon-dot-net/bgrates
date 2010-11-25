package net.vexelon.bgrates;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;


public class RateInfoActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.rate_info);
		
		Intent intent = getIntent();
		if (intent != null) {
			// country ID icon
			ImageView icon = (ImageView) findViewById(R.id.IconRateInfo);
			int imgId = intent.getIntExtra(Defs.INTENT_FLAG_ID, -1);
			if ( imgId != -1 ) {
				icon.setImageResource(imgId);	
			}
				
			TextView tvRateOld = (TextView) findViewById(R.id.TextRatioOld);
			tvRateOld.setText(intent.getStringExtra(Defs.INTENT_OLD_RATEINFO));
			
			TextView tvRateNew = (TextView) findViewById(R.id.TextRatioNew);
			tvRateNew.setText(intent.getStringExtra(Defs.INTENT_NEW_RATEINFO));			
		}
		
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		finish();
		return super.onTouchEvent(event);
	}
	
}
