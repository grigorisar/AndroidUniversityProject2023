package com.example.android2022.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(@Nullable Context context){
        super(context, DbLocation.DB_NAME,null, DbLocation.DB_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbLocation.CREATE_TABLE_FENCE);
        db.execSQL(DbLocation.CREATE_TABLE_TRAVERSAL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + DbLocation.TABLE_FENCE);
        db.execSQL("DROP TABLE IF EXISTS " + DbLocation.TABLE_TRAVERSAL);
        onCreate(db);
    }
}
