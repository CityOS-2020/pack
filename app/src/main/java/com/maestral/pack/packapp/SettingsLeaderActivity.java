package com.maestral.pack.packapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.os.Bundle;

public class SettingsLeaderActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new LeaderScreenFragment()).commit();
    }

    public static class LeaderScreenFragment extends PreferenceFragment {

        PreferenceScreen mainScreen;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.leaderscreen);

            mainScreen = (PreferenceScreen) findPreference("leaderPrefScreen");

            Preference groupNamePref = (Preference) findPreference("groupName");

            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());

            if(groupNamePref!=null)
            {
                groupNamePref.setTitle("You are leader of " + SP.getString("createGroup", "") + " group.");
            }

            setEventListener_CloseGroup();
        }

        void setEventListener_CloseGroup(){

            Preference leaveGroup = findPreference("leaveGroup");

            if(leaveGroup != null) {
                leaveGroup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent settingsActivity = new Intent(MyApplication.getAppContext(), SettingsActivity.class);
                        settingsActivity.putExtra("EventTriggered", 3);
                        startActivity(settingsActivity);

                        //Log.e("EditTextPreference","In onPreferenceClick, groupname: " + strUserGroup);
                        return true;
                    }
                });
            }
        }
    }
}
