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

import com.maestral.pack.packapp.API.PackApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        PackApi mAPI;
        PreferenceScreen mainScreen;
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
                else if(eventTriggered == 2){
                    EditTextPreference joinGroupTxt = (EditTextPreference) findPreference("joinGroup");
                    if (joinGroupTxt != null) {
                        joinGroupTxt.setText("");
                    }
                }
            }

            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
            leaderGroupName = SP.getString("createGroup", "");
            joinGroupName = SP.getString("joinGroup", "");

            if(leaderGroupName != "") {
                Intent settingsLeaderScreen = new Intent(MyApplication.getAppContext(), SettingsLeaderActivity.class);
                startActivity(settingsLeaderScreen);
            }
            else if(joinGroupName != ""){
                Intent settingsLeaderScreen = new Intent(MyApplication.getAppContext(), SettingsMemberActivity.class);
                startActivity(settingsLeaderScreen);
            }

            mAPI = PackApi.retrofit.create(PackApi.class);

            setEventListener_CreateGroup();
            setEventListener_JoinGroup();
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

        void setEventListener_CreateGroup(){

            EditTextPreference createGroup = (EditTextPreference)findPreference("createGroup");

            if(createGroup != null) {
                createGroup.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if(mainScreen!=null) {
                            if(newValue != null){
                                leaderGroupName = newValue.toString();
                                Call<String> AddGroupCall = mAPI.AddGroup(Self.getInstance().member, leaderGroupName);

                                AddGroupCall.enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        Log.i("SettingsActivity", "Success calling CreateGroup API.");
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        Log.e("SettingsActiviy", "Error when calling CreateGroup API");
                                    }
                                });
                            }

                            Intent settingsLeaderScreen = new Intent(MyApplication.getAppContext(), SettingsLeaderActivity.class);
                            startActivity(settingsLeaderScreen);
                        }
                        Log.d("SettingsActivity", "CreateGroup" + newValue.toString());
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
                            if(newValue != null){
                                joinGroupName = newValue.toString();
                            }
                            Intent settingsLeaderScreen = new Intent(MyApplication.getAppContext(), SettingsMemberActivity.class);
                            startActivity(settingsLeaderScreen);
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
