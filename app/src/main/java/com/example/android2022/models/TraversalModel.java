package com.example.android2022.models;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.android2022.database.DbHelper;
import com.example.android2022.database.DbLocation;

import java.util.ArrayList;

public class TraversalModel {

    public static final String ENTER = "ENTER";
    public static final String EXIT = "EXIT";
    private int fenceId;
    private String sessionId, action, timestamp;
    private double latitude, longitude;


    public TraversalModel(String sessionId, String action, String timestamp, double latitude, double longitude,int fenceId) {
        this.fenceId = fenceId;
        this.sessionId = sessionId;
        this.action = action;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getFenceId() {
        return fenceId;
    }

    public void setFenceId(int fenceId) {
        this.fenceId = fenceId;
    }
}

