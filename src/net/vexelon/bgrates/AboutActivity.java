package net.vexelon.bgrates;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends Activity {
	
	private final static String TAG = Defs.LOG_TAG;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Log.v(TAG, "@onCreate()");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		init();
	}
	
	private void init() {

		ImageView icLogo = (ImageView)findViewById(R.id.about_logo);
		icLogo.setImageResource(R.drawable.about_icon);
		
		PackageInfo pinfo = null;
		try {
			pinfo = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_GIDS);
		}
		catch(Exception e) {
			//Log.e(TAG, e.getMessage());
		}
		
		StringBuffer sb = new StringBuffer(500);
		sb.append(getResString(R.string.app_name));
		sb.append("\n");
		sb.append(getResString(R.string.about_tagline));
		sb.append("\n");
		if ( pinfo != null ) {
			sb.append(getResString(R.string.about_version));
			sb.append(" ");
			sb.append(pinfo.versionName);
			sb.append("\n");
		}
		sb.append(getResString(R.string.about_author));
		sb.append("\n");
		sb.append("http://code.google.com/p/bgrates/");
		sb.append("\n");
		sb.append("\n");
		sb.append(getResString(R.string.about_bnb_info));
		sb.append("\n");
		sb.append("http://www.bnb.bg");
		sb.append("\n");
		sb.append("\n");
		sb.append(getResString(R.string.about_bnb_info_2));
		sb.append("\n");
		sb.append("\n");
		sb.append(getResString(R.string.about_flag_icons_info));
		sb.append("\n");
		sb.append("http://www.famfamfam.com");
		sb.append("\n");
		
		this.setText(R.id.about_apptitle, sb.toString());
		Linkify.addLinks((TextView)findViewById(R.id.about_apptitle), Linkify.ALL);
	}
	
	void setText(int id, String text) {
		TextView tx = (TextView)findViewById(id);
		if ( tx != null )
			tx.setText(text);
	}
	
	String getResString(int id) {
		return this.getResources().getString(id);
	}
}
