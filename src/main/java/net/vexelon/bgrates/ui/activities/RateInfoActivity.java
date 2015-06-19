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
package net.vexelon.bgrates.ui.activities;
import net.vexelon.bgrates.Defs;
import net.vexelon.bgrates.R;
import net.vexelon.bgrates.R.id;
import net.vexelon.bgrates.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
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
			if ( icon != null && imgId != -1 ) {
				icon.setImageResource(imgId);
			}

			// tendency
			icon = (ImageView) findViewById(R.id.IconRateInfoTendency);
			int tendencyIconId = intent.getIntExtra(Defs.INTENT_NEW_RATEINFO_TENDENCY_ICONID, -1);
			if (icon != null && tendencyIconId != -1)
				icon.setImageResource(tendencyIconId);

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
