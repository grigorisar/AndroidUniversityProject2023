package com.example.android2022.database;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.UserDictionary;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android2022.models.FenceModel;
import com.example.android2022.models.TraversalModel;

import java.util.ArrayList;
import java.util.HashMap;

public class LocationContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.example.android2022.provider";

    // defining content URI
    static final String URL_F = "content://" + PROVIDER_NAME + "/fences";
    static final String URL_T = "content://" + PROVIDER_NAME + "/traversals";

    // parsing the content URI
    static final Uri FENCE_URI = Uri.parse(URL_F);
    static final Uri TRAVERSAL_URI = Uri.parse(URL_T);

    static final String id = "id";
    static final String name = "name";
    static final int uriCodeFence = 1;
    static final int uriCodeTraversal = 2;
    static UriMatcher uriMatcher;
    final ContentResolver resolver = getContext().getContentResolver();
    private static HashMap<String, String> values;
    DbHelper helper = new DbHelper(getContext());

    static {
        // to match the content URI
        // every time user access table under content provider
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //TODO: customize this for two tables
        // to access whole table

        //access table fences
        uriMatcher.addURI(PROVIDER_NAME, DbLocation.TABLE_FENCE, uriCodeFence);

        //access table traversal
        uriMatcher.addURI(PROVIDER_NAME, DbLocation.TABLE_TRAVERSAL, uriCodeTraversal);

        // to access a particular row
        // of the table
//        uriMatcher.addURI(PROVIDER_NAME, "users/*", uriCode);
    }

//    @Nullable
//    @Override
//    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selectionClause, @Nullable String[] selectionArgs, @Nullable String groupBy, @Nullable String having, @Nullable String orderBy) {
//        SQLiteDatabase database = helper.getReadableDatabase();
//        Cursor cursor = null;
//        switch(uriMatcher.match(uri)){
//            //Select * from LOCATIONS
//            case 1:
//                cursor = database.query(DbLocation.TABLE_NAME,projection,selectionClause,selectionArgs,groupBy,having,orderBy);
//                break;
//        }
//        return cursor;
//    }

    public ArrayList getLastSessionFences (){

        //TODO: Implement this to get the geofences of the last session

        // select DbLocation.TIMESTAMP_COL from DbLocation.TABLE_FENCE WHERE DbLocation.TIMESTAMP_COL=getLastSession() ;
        ArrayList fences = new ArrayList();
        Cursor c = query(FENCE_URI,
                null,
                DbLocation.TIMESTAMP_COL + " = ?",
                new String[]{getLastSession()},
                null);

        while(c.moveToNext()){
//            FenceModel fence = new FenceModel(cursor.getString(
//                    getValue(cursor,DbLocation.TIMESTAMP_COL),
//                    Double.parseDouble(getValue(cursor,DbLocation.LAT_COL)),
//                    Double.parseDouble(getValue(cursor,DbLocation.LON_COL)));
            @SuppressLint("Range")
            FenceModel fence = new FenceModel(
                    c.getString(c.getColumnIndex(DbLocation.TIMESTAMP_COL)),
                    c.getDouble(c.getColumnIndex(DbLocation.LAT_COL)),
                    c.getDouble(c.getColumnIndex(DbLocation.LON_COL)));
            fences.add(fence);

        }
        return  fences;
    }


    public ArrayList<TraversalModel> getTraversals(String sessionId) {
        ArrayList<TraversalModel> travs = new ArrayList<>();
        Cursor c = query(
                TRAVERSAL_URI,
                null,
                DbLocation.TIMESTAMP_COL + " = ?",
                new String[]{sessionId},
                null);
        while(c.moveToNext()){
            @SuppressLint("Range")
            TraversalModel t = new TraversalModel(
                    sessionId,
                    c.getString(c.getColumnIndex(DbLocation.ACTION_COL)),
                    c.getString(c.getColumnIndex(DbLocation.TIMESTAMP_COL)),
                    c.getDouble(c.getColumnIndex((DbLocation.LAT_COL))),
                    c.getDouble(c.getColumnIndex((DbLocation.LON_COL)))
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
        Cursor cursor = query(
                FENCE_URI,
                new String[]{"max("+DbLocation.TIMESTAMP_COL+")"},
                null,
                null,
                null,
                null);
        if(cursor!= null){
            cursor.moveToFirst();
            return  cursor.getString(0);
        }
        return null;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
//        getContext().getContentResolver();
        return false;
    }

    @Nullable //TODO: maybe remove this and implement null handling in this function (probs not possible)
    @Override
    public Cursor query(Uri uri,
                        @Nullable String[] projection, // columns to return
                        @Nullable String selection,  // WHERE clause
                        @Nullable String[] selectionArgs, // WHERE clause value substitution
                        @Nullable String sortOrder) {
        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = null;
        switch(uriMatcher.match(uri)){
            //Select * from LOCATIONS
            case uriCodeFence:
//                cursor = database.query(uri.getPath(),projection,selection,selectionArgs,null,null,null);
//                sortOrder = DbLocation.FENCE_ID + " DESC";
                break; //Select * from LOCATIONS
            case uriCodeTraversal:
//                sortOrder = DbLocation.TIMESTAMP_COL + " DESC";
//                cursor = database.query(uri.getPath(),projection,selection,selectionArgs,null,null,null);
                break;
            default:
                Log.d(TAG, "UriMatcher:CODE NOT FOUND \nquery() -> returned: " + cursor);
        }
//        cursor = database.query(uri.getPath(),projection,selection,selectionArgs,null,null,sortOrder);
        //TODO: check if uri matches table
        cursor = database.query(uri.getPath(),projection,selection,selectionArgs,null,null,sortOrder);
        //TODO: Add null handling
        return cursor;
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
        int match = uriMatcher.match(uri);
        switch (match)
        {
            case uriCodeFence:
                return "vnd.android.cursor.dir/";
            case uriCodeTraversal:
                return "vnd.android.cursor.item/person";
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri insertedItemUri = resolver.insert(uri,values); //may be null
        return insertedItemUri;
//        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int rowsAffected = resolver.update(uri,values,selection,selectionArgs);
        return rowsAffected;
//        throw new UnsupportedOperationException("Not yet implemented");
    }

}