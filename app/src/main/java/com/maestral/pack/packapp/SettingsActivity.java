package com.maestral.pack.packapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        PreferenceScreen mainScreen;
        PreferenceScreen leaderPrefScreen;
        int eventTriggered = 0;
        String leaderGroupName = "";
        String joinGroupName = "";
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

            mainScreen = (PreferenceScreen)findPreference("mainScreen");
            Bundle intentBundle = getActivity().getIntent().getExtras();

            if(intentBundle != null) {
                eventTriggered = intentBundle.getInt("EventTriggered");

                if (eventTriggered == 3) {
                    EditTextPreference createGroupTxt = (EditTextPreference) findPreference("createGroup");
                    if (createGroupTxt != null) {
                        createGroupTxt.setText("");
                    }
                }
            }

            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
            leaderGroupName = SP.getString("createGroup", "");

            if(leaderGroupName != "") {
                Intent settingsLeaderScreen = new Intent(MyApplication.getAppContext(), SettingsLeaderActivity.class);
                startActivity(settingsLeaderScreen);
            }
            else if(joinGroupName != ""){
                /*addPreferencesFromResource(R.xml.memberscreen);

                Preference groupNamePref = (Preference) findPreference("groupName");
                if(groupNamePref!=null)
                {
                    groupNamePref.setTitle("You are part of " + leaderGroupName + " group.");
                }*/
            }


            setEventListener_CreateGroup();
            setEventListener_JoinGroup();

            Preference userGroupPref = (Preference) findPreference("userGroup");
            if(userGroupPref != null) {
                userGroupPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                        String strUserGroup = SP.getString("userGroup", "");

                        Log.e("EditTextPreference", "In onPreferenceClick, groupname: " + strUserGroup);
                        return true;
                    }
                });
            }
        }

        void setEventListener_CreateGroup(){

            EditTextPreference createGroup = (EditTextPreference)findPreference("createGroup");

            if(createGroup != null) {
                createGroup.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if(mainScreen!=null) {
                            if(newValue != null){
                                leaderGroupName = newValue.toString();
                            }

                            Intent settingsLeaderScreen = new Intent(MyApplication.getAppContext(), SettingsLeaderActivity.class);
                            startActivity(settingsLeaderScreen);
                        }
                        Log.d("test1", "test1" + newValue.toString());
                        //startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI),REQUEST_CODE_PICK_CONTACTS);
                        return true;
                    }
                });
            }
        }

        void setEventListener_JoinGroup(){
            Preference joinGroup = findPreference("joinGroup");

            if(joinGroup != null) {
                joinGroup.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if(mainScreen!=null) {
                            mainScreen.removeAll();
                            addPreferencesFromResource(R.xml.memberscreen);
                        }
                        Log.d("test1", "memberScreen");
                        //startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI),REQUEST_CODE_PICK_CONTACTS);
                        return true;
                    }
                });
            }
        }

    }

}
