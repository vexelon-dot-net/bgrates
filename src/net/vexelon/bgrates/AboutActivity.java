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

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.util.Linkify;
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

		StringBuilder sb = new StringBuilder(500);
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
		sb.append("Copyright (c) 2013 Go Squared Ltd. http://www.gosquared.com/");
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
