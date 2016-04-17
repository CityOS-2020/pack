package com.maestral.pack.packapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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
            setEventListener_AddEditUsername();
            setEventListener_AddEditFirstName();
            setEventListener_AddEditLastname();
        }

        void setEventListener_AddEditLastname(){
            EditTextPreference userLastName = (EditTextPreference)findPreference("userLastName");

            if(userLastName != null) {
                userLastName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if(mainScreen!=null) {
                            if(newValue != null){
                                Self.getInstance().member.lastName = newValue.toString();
                            }
                        }
                        Log.d("test1", "User last name: " + newValue.toString());
                        //startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI),REQUEST_CODE_PICK_CONTACTS);
                        return true;
                    }
                });
            }
        }

        void setEventListener_AddEditUsername(){
            EditTextPreference username = (EditTextPreference)findPreference("username");

            if(username != null) {
                username.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if(mainScreen!=null) {
                            if(newValue != null){
                                Self.getInstance().member.userName = newValue.toString();
                            }
                        }
                        Log.d("test1", "Username: " + newValue.toString());
                        //startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI),REQUEST_CODE_PICK_CONTACTS);
                        return true;
                    }
                });
            }
        }

        void setEventListener_AddEditFirstName(){
            EditTextPreference userFirstName = (EditTextPreference)findPreference("userFirstName");

            if(userFirstName != null) {
                userFirstName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if(mainScreen!=null) {
                            if(newValue != null){
                                Self.getInstance().member.firstName = newValue.toString();
                            }
                        }
                        Log.d("test1", "User first name: " + newValue.toString());
                        //startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI),REQUEST_CODE_PICK_CONTACTS);
                        return true;
                    }
                });
            }
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
