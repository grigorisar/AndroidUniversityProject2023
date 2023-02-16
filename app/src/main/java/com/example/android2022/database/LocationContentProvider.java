package com.example.android2022.database;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.android2022.models.FenceModel;
import com.example.android2022.models.TraversalModel;

import java.util.ArrayList;
import java.util.HashMap;

public class LocationContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.example.android2022.provider";

    static final String id = "id";
    static final String name = "name";
    static final int uriCodeFence = 1;
    static final int uriCodeTraversal = 2;
    static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static ContentResolver resolver;
    private static HashMap<String, String> values;
    static DbHelper helper;
    static SQLiteDatabase database;

    static {
        // to match the content URI
        // every time user access table under content provider

        //TODO: customize this for two tables
        // to access whole table

        //access table fences
        uriMatcher.addURI(PROVIDER_NAME, DbLocation.TABLE_FENCE.toLowerCase(), uriCodeFence);
        //access table traversal
        uriMatcher.addURI(PROVIDER_NAME, DbLocation.TABLE_TRAVERSAL.toLowerCase(), uriCodeTraversal);

        // to access a particular row
        // of the table
//        uriMatcher.addURI(PROVIDER_NAME, "users/*", uriCode);
    }

    public ArrayList getLastSessionFences(){

        //TODO: Implement this to get the geofences of the last session

        String sessionId = getLastSession();
        if (sessionId == null) { return null;}
        // select DbLocation.TIMESTAMP_COL from DbLocation.TABLE_FENCE WHERE DbLocation.TIMESTAMP_COL=getLastSession() ;
        ArrayList fences = new ArrayList();
        Cursor c = query(DbLocation.FENCE_URI,
                null,
                DbLocation.SESSION_ID + " = ?",
                new String[]{sessionId},
                null);

        while(c.moveToNext()){
//            FenceModel fence = new FenceModel(cursor.getString(
//                    getValue(cursor,DbLocation.TIMESTAMP_COL),
//                    Double.parseDouble(getValue(cursor,DbLocation.LAT_COL)),
//                    Double.parseDouble(getValue(cursor,DbLocation.LON_COL)));
            @SuppressLint("Range")
            FenceModel fence = new FenceModel(
                    c.getInt(c.getColumnIndex(DbLocation.FENCE_ID)),
                    c.getString(c.getColumnIndex(DbLocation.SESSION_ID)),
                    c.getDouble(c.getColumnIndex(DbLocation.LAT_COL)),
                    c.getDouble(c.getColumnIndex(DbLocation.LON_COL)));
            fences.add(fence);

        }
        return  fences;
    }


    public ArrayList<TraversalModel> getLastSessionTraversals() {
        ArrayList<TraversalModel> travs = new ArrayList<>();
        String sessionId = getLastSession();
        if (sessionId == null) {
            return null;
        }
        Cursor c = query(
                DbLocation.TRAVERSAL_URI,
                null,
                DbLocation.SESSION_ID + " = ?",
                new String[]{sessionId},
                null);
        while(c.moveToNext()){
            @SuppressLint("Range")
            TraversalModel t = new TraversalModel(
                    sessionId,
                    c.getString(c.getColumnIndex(DbLocation.ACTION_COL)),
                    c.getString(c.getColumnIndex(DbLocation.TIMESTAMP_COL)),
                    c.getDouble(c.getColumnIndex((DbLocation.LAT_COL))),
                    c.getDouble(c.getColumnIndex((DbLocation.LON_COL))),
                    c.getInt(c.getColumnIndex((DbLocation.FENCE_ID)))
            );
            travs.add(t);
        }

        return travs;
    }

    private String getLastSession(){
//        SELECT max(DbLocation.TIMESTAMP_COL) FROM DbLocation.TABLE_FENCE
//        selectionClause = UserDictionary.Words.WORD + " = ?";
//
//        // Moves the user's input string to the selection arguments.
//        selectionArgs[0] = searchString;
//
        try {
            Cursor cursor = query(
                    DbLocation.FENCE_URI,
                    new String[]{"max("+DbLocation.SESSION_ID+")"},
                    null,
                    null,
                    null,
                    null);
            if(cursor!= null){
                cursor.moveToFirst();
                String session = cursor.getString(0);
                Log.d("SessionID", "getLastSession() returned: " + session);
                return session;
            }
        }catch (Error e){
            return null;
        }
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
//        getContext().getContentResolver();
        helper = new DbHelper(getContext());
        resolver = getContext().getContentResolver();
        database = helper.getReadableDatabase();

        return false;
    }

    @Nullable //TODO: maybe remove this and implement null handling in this function (probs not possible)
    @Override
    public Cursor query(Uri uri,
                        @Nullable String[] projection, // columns to return
                        @Nullable String selection,  // WHERE clause
                        @Nullable String[] selectionArgs, // WHERE clause value substitution
                        @Nullable String sortOrder) {
        //TODO: check if uri matches table
        try{
           Cursor cursor = database.query(getType(uri),projection,selection,selectionArgs,null,null,sortOrder);
           return cursor;
        }catch (Error e){
            return null;
        }
    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        resolver.delete(uri,selection,selectionArgs);
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri))
        {
            case uriCodeFence:
                return DbLocation.TABLE_FENCE;
            case uriCodeTraversal:
                return DbLocation.TABLE_TRAVERSAL;
            default:
                throw new UnsupportedOperationException("No table found for URI :" + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long id = database.insert(getType(uri), null, values);
        if (id > 0) {
            Uri _uri = ContentUris.withAppendedId(uri, id);
            resolver.notifyChange(_uri, null);
            return _uri;
        }
//        throw new UnsupportedOperationException("Insertion Failed for URI :" + uri);
        return uri;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
//        int rowsAffected = resolver.update(uri,values,selection,selectionArgs);
//        return rowsAffected;
        throw new UnsupportedOperationException("Not yet implemented");
    }

}