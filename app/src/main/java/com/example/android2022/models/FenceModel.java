package com.example.android2022.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android2022.database.DbHelper;
import com.example.android2022.database.DbLocation;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class FenceModel {
    // radius of 100 but we don't need to save that because we don't use it
    private int id;
    private String sessionId;
    private double latitude,longitude;
    static DbHelper helper;

    public FenceModel(int id, String sessionId, double latitude, double longitude) {
        this.id = id;
        this.sessionId = sessionId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
