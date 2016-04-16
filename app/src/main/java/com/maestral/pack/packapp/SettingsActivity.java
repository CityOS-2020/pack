package com.maestral.pack.packapp;

import android.content.SharedPreferences;
import android.nfc.Tag;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference parentPref = (Preference) findPreference("preferencesParent");
            parentPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    Preference userGroupPref = (Preference) findPreference("userCategory");

                    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

                    String strUserGroup = SP.getString("userGroup", "");
                    Log.e("EditTextPreference","In onPreferenceClick 2nd, groupname: " + strUserGroup);
                    if(strUserGroup != "") {
                        userGroupPref.setEnabled(false);
                    }

                    return true;
                }
            });

            Preference userGroupPref = (Preference) findPreference("userGroup");
            userGroupPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {

                    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                    String strUserGroup = SP.getString("userGroup", "");

                    Log.e("EditTextPreference","In onPreferenceClick, groupname: " + strUserGroup);
                    return true;
                }
            });
        }

    }

}
