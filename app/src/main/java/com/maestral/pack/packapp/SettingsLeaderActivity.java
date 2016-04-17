package com.maestral.pack.packapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.os.Bundle;
import android.util.Log;

import com.maestral.pack.packapp.API.PackApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsLeaderActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new LeaderScreenFragment()).commit();
    }

    public static class LeaderScreenFragment extends PreferenceFragment {

        PackApi mAPI;
        PreferenceScreen mainScreen;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.leaderscreen);

            mAPI = PackApi.retrofit.create(PackApi.class);

            mainScreen = (PreferenceScreen) findPreference("leaderPrefScreen");

            Preference groupNamePref = (Preference) findPreference("groupName");

            if(groupNamePref!=null)
            {
                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
                groupNamePref.setTitle("You are leader of " + SP.getString("createGroup", "") + " group.");
            }

            setEventListener_CloseGroup();
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

        void setEventListener_CloseGroup(){

            Preference leaveGroup = findPreference("leaveGroup");

            if(leaveGroup != null) {
                leaveGroup.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                    @Override
                    public boolean onPreferenceClick(Preference preference) {

                        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());

                        Call<String> RemoveGroupCall = mAPI.RemoveGroup(SP.getString("createGroup", ""));

                        RemoveGroupCall.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                Log.i("SettingsLeaderActivity", "Success calling CloseGroup API.");

                                Intent settingsActivity = new Intent(MyApplication.getAppContext(), SettingsActivity.class);
                                settingsActivity.putExtra("EventTriggered", 3);
                                startActivity(settingsActivity);
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Log.e("SettingsLeaderActivity", "Error when calling CloseGroup API");
                            }
                        });

                        //Log.e("EditTextPreference","In onPreferenceClick, groupname: " + strUserGroup);
                        return true;
                    }
                });
            }
        }
    }
}
