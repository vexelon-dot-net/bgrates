package net.vexelon.bgrates.ui.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import net.vexelon.bgrates.R;

public class PrefsFragment extends PreferenceFragment {

	public static final String TAG = "preferences";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}
