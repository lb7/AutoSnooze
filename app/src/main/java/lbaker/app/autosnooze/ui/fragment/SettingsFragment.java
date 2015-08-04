package lbaker.app.autosnooze.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import lbaker.app.autosnooze.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends PreferenceFragment {

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
