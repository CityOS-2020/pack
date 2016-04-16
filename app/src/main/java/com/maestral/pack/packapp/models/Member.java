package com.maestral.pack.packapp.models;

import android.location.Location;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by irfanka on 4/16/16.
 */
public class Member {
    public Member(String userName, String firstName, String lastName, Boolean isLeader, double[] geoLocation){
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isGroupLeader = isLeader;
        this.geoLocation = geoLocation;
    }
    public double [] geoLocation;
    @SerializedName("username")
    public String userName;
    @SerializedName("FirstName")
    public String  firstName;
    @SerializedName("LastName")
    public String lastName;
    public Boolean isGroupLeader;

    @Override
    public String toString(){
        return this.userName + " " + this.firstName + " " + this.lastName + " " + String.valueOf(this.isGroupLeader) + " " + String.valueOf(this.geoLocation);
    }
}
