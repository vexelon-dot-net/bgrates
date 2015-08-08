/*
 * The MIT License
 * 
 * Copyright (c) 2015 Petar Petrov
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
package net.vexelon.bgrates.ui.fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import net.vexelon.bgrates.R;

public class AboutFragment extends AbstractFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_about, container, false);
		init(rootView);
		return rootView;
	}

	private void init(View view) {
		ImageView icLogo = (ImageView) view.findViewById(R.id.about_logo);
		icLogo.setImageResource(R.drawable.about_icon);

		PackageInfo pinfo = null;
		try {
			pinfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),
					PackageManager.GET_GIDS);
		} catch (Exception e) {
			// Log.e(TAG, e.getMessage());
		}

		StringBuilder sb = new StringBuilder();
		sb.append(getResString(R.string.app_name));
		sb.append("\n");
		sb.append(getResString(R.string.about_tagline));
		sb.append("\n");
		if (pinfo != null) {
			sb.append(getResString(R.string.about_version));
			sb.append(pinfo.versionName);
			sb.append("\n");
		}
		this.setText(view, R.id.about_row1, sb.toString());
		Linkify.addLinks((TextView) view.findViewById(R.id.about_row1), Linkify.ALL);

		sb = new StringBuilder();
		sb.append(getResString(R.string.about_author));
		sb.append("\n");
		sb.append("https://github.com/petarov/bgrates");
		sb.append("\n");
		sb.append(getResString(R.string.about_logo_author));
		sb.append("\n");
		sb.append("http://stremena.com");
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
		sb.append("Copyright (c) 2013 Aha-Soft. http://www.aha-soft.com/free-icons/free-yellow-button-icons/");
		sb.append("\n");
		sb.append("Copyright (CC BY-ND 3.0) Visual Pharm. http://icons8.com");
		sb.append("\n");
		this.setText(view, R.id.about_row2, sb.toString());
		Linkify.addLinks((TextView) view.findViewById(R.id.about_row2), Linkify.ALL);
	}

	void setText(View view, int id, String text) {
		TextView tx = (TextView) view.findViewById(id);
		if (tx != null) {
			tx.setText(text);
		}
	}

	String getResString(int id) {
		return this.getResources().getString(id);
	}

}
