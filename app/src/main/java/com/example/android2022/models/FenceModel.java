package com.example.android2022.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android2022.database.DbHelper;
import com.example.android2022.database.DbLocation;

import java.util.ArrayList;

public class FenceModel {
    // radius of 100 but we don't need to save that because we don't use it
    private String sessionId;
    private double latitude,longitude;
    static DbHelper helper;

    public FenceModel(String sessionId, double latitude, double longitude) {
        this.sessionId = sessionId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long persist() throws Exception{
        ContentValues values = new ContentValues();
        values.put(DbLocation.SESSION_ID, this.sessionId);
        values.put(DbLocation.LAT_COL, this.latitude);
        values.put(DbLocation.LON_COL, this.longitude);
        SQLiteDatabase db = helper.getWritableDatabase();
        long result = db.insert(DbLocation.TABLE_FENCE, null, values);
        db.close();
        if (result == -1) {
            throw new Exception("Insert failed!");
        }
        return result;

    }

    public static ArrayList<FenceModel> getCurrentSessionFences(String sessionId){
        ArrayList<FenceModel> fences = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();

        String table = DbLocation.TABLE_FENCE;
        String[] columns = {
                DbLocation.SESSION_ID,
                DbLocation.LAT_COL,
                DbLocation.LON_COL,
        };
        String selection = DbLocation.SESSION_ID+"=?";
        String[] selectionArgs = {sessionId};

        Cursor results = db.query(table, columns, selection, selectionArgs, null, null, null, null);
        if (results.moveToFirst()) {
            do {
                FenceModel fence= new FenceModel(results.getString(1), results.getDouble(2), results.getDouble(3));
                fences.add(fence);
            } while (results.moveToNext());
        }
        db.close();
        return fences;
    }

    //TODO: Retrieve function
//    public static ArrayList<FenceModel> getSessionFences(){
//        return ;
//    }

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
}
