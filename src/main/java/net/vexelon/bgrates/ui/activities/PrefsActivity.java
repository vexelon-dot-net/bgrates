package net.vexelon.bgrates.ui.activities;

import android.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import net.vexelon.bgrates.ui.fragments.PrefsFragment;

public class PrefsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getFragmentManager().beginTransaction().replace(R.id.content, new PrefsFragment()).commit();
	}

}
