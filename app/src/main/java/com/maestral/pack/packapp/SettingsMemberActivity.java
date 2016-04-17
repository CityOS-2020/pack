package com.maestral.pack.packapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsMemberActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MemberScreenFragment()).commit();
    }

    public static class MemberScreenFragment extends PreferenceFragment {
        PreferenceScreen mainScreen;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.memberscreen);

            mainScreen = (PreferenceScreen) findPreference("memberPrefScreen");

            Preference groupNamePref = (Preference) findPreference("groupName");

            if(groupNamePref!=null)
            {
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
                groupNamePref.setTitle("You are part of " + SP.getString("joinGroup", "") + " group.");
            }

            setEventListener_LeaveGroup();
        }

        void setEventListener_LeaveGroup(){
            Preference leaveGroup = findPreference("leaveGroup");

            if(leaveGroup != null) {
                leaveGroup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent settingsActivity = new Intent(MyApplication.getAppContext(), SettingsActivity.class);
                        settingsActivity.putExtra("EventTriggered", 2);
                        startActivity(settingsActivity);

                        //Log.e("EditTextPreference","In onPreferenceClick, groupname: " + strUserGroup);
                        return true;
                    }
                });
            }
        }
    }
}
