package com.maestral.pack.packapp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.maestral.pack.packapp.models.Member;

/**
 * Created by irfanka on 4/17/16.
 */
public class Self {
    private static Self ourInstance = new Self();

    public static Self getInstance() {
        return ourInstance;
    }


    private Self() {


        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        String username = SP.getString("username", "");
        String firstName = SP.getString("userFirstName", "");
        String lastName = SP.getString("userLastName", "");

        member = new Member(username, firstName, lastName, false, null);

    }


    public Member member;

}
