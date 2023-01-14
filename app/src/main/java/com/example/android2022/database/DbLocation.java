package com.example.android2022.database;

public class DbLocation {
    static public String DB_NAME="LOCATIONS_DB";
    static public String TABLE_FENCE="FENCES";
    static public String TABLE_TRAVERSAL="TRAVERSALS";

    static public int DB_VERSION = 1;

    //TRAVERSAL
    static public String TRAVERSAL_ID = "ID_T";
    static public String ACTION_COL = "ACTION";

    //FENCES
    static public String FENCE_ID = "ID_F";

    //COMMON
    static public String SESSION_ID = "SESSION_ID";
    static public String LAT_COL = "LAT";
    static public String LON_COL = "LON";
    static public String TIMESTAMP_COL = "TIMESTAMP";



    static public String AUTHORITY = "com.example.android2022";
    static public String PATH_TRAVERSAL = DbLocation.TABLE_TRAVERSAL;
    static public String PATH_FENCE = DbLocation.TABLE_FENCE;

    static public String CREATE_TABLE_FENCE = "" +
            "CREATE TABLE "+TABLE_FENCE+" " +
            "("
            + FENCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SESSION_ID +" TEXT,"
            + LAT_COL +" TEXT,"
            + LON_COL +" TEXT,);";

    static public String CREATE_TABLE_TRAVERSAL = "" +
            "CREATE TABLE "+TABLE_TRAVERSAL +" ("
            + TRAVERSAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SESSION_ID +" TEXT,"
            + LAT_COL +" TEXT,"
            + LON_COL +" TEXT,"
            + TIMESTAMP_COL +" TEXT,"
            + ACTION_COL +" TEXT,);";
}
