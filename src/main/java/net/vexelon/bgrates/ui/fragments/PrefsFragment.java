package net.vexelon.bgrates.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import net.vexelon.bgrates.R;

public class PrefsFragment extends PreferenceFragment implements OnPreferenceClickListener {

	public static final String TAG = "preferences";
	public static final String KEY_SCREEN_APP_PREFS = "app_prefs";
	public static final String KEY_PREF_RATEUS = "rateus";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		addPreferencesFromResource(R.xml.preferences);
		findPreference(KEY_PREF_RATEUS).setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.getKey().equals(KEY_PREF_RATEUS)) {
			final String appPackageName = getActivity().getPackageName();
			try {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
			} catch (android.content.ActivityNotFoundException e) {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
			}
		}
		return false;
	}
}
