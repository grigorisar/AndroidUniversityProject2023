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

    static DbHelper helper;

    public TraversalModel(String sessionId, String action, String timestamp, double latitude, double longitude,int fenceId) {
        this.fenceId = fenceId;
        this.sessionId = sessionId;
        this.action = action;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public long persist() throws Exception{
        ContentValues values = new ContentValues();
        values.put(DbLocation.FENCE_ID, this.fenceId);
        values.put(DbLocation.SESSION_ID, this.sessionId);
        values.put(DbLocation.LAT_COL, this.latitude);
        values.put(DbLocation.LON_COL, this.longitude);
        values.put(DbLocation.ACTION_COL, this.action);
        values.put(DbLocation.TIMESTAMP_COL, this.timestamp);
        SQLiteDatabase db = helper.getWritableDatabase();
        long result = db.insert(DbLocation.TABLE_TRAVERSAL, null, values);
        db.close();
        if (result == -1) {
            throw new Exception("Insert failed!");
        }
        return result;
    }

//    public static ArrayList<TraversalModel> getLastTraversals() throws Exception{
//        ArrayList<TraversalModel> latestTraversals = new ArrayList<>();
//
//        String table = DbLocation.TABLE_TRAVERSAL;
//        String[] columns = {
//                DbLocation.SESSION_ID,
//                DbLocation.LAT_COL,
//                DbLocation.LON_COL,
//                DbLocation.TIMESTAMP_COL,
//                DbLocation.ACTION_COL,
//        };
////        String[] selectionArgs = {sessionId};
////        String sessionId = getLastSessionId();
//
//        // If a last session is found then select it, otherwise return null
//        String sessionId = fetchLastSessionId();
//        if (sessionId!= null){ //if found
//            SQLiteDatabase db = helper.getReadableDatabase();
//            String[] selectionArgs = new String[]{sessionId};
//            String selection = DbLocation.SESSION_ID+"=?";
//
//            Cursor results = db.query(table, columns, selection, selectionArgs, null, null, null, null);
//
//            if (results.moveToFirst()) {
//                do {
//                    TraversalModel traversal= new TraversalModel(
//                            results.getString(1),
//                            results.getString(2),
//                            results.getString(3),
//                            results.getDouble(4),
//                            results.getDouble(5));
//                    latestTraversals.add(traversal);
//                } while (results.moveToNext());
//            }
//            db.close();
//            return latestTraversals;
//        }
//        return null;
//    }

    @Nullable
    private static String fetchLastSessionId() throws Exception{
        //TODO: use  'SELECT timestamp FROM DbLocation.TABLE_TRAVERSAL ORDER BY timestamp DESC LIMIT 1;'

        // Custom query for the last timestamp
        SQLiteDatabase db = helper.getReadableDatabase();

        String query1 = "SELECT " + DbLocation.TIMESTAMP_COL+","+DbLocation.SESSION_ID + " FROM " +DbLocation.TABLE_TRAVERSAL + " ORDER BY " +DbLocation.TIMESTAMP_COL+ " DESC LIMIT 1;";
        Cursor  cursor = db.rawQuery(query1,null);
        int colIndex = cursor.getColumnIndex(DbLocation.SESSION_ID);
        if (cursor != null && colIndex >= 0) {
            cursor.moveToFirst();
            String sessionId =  cursor.getString(colIndex);
            db.close();
            return sessionId;
        }
        db.close();
        return null;
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

