package com.example.unipitouristapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PoiLogDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "PoiLogDB.db";
    public static final int DATABASE_VERSION = 1;
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_POILOG_TABLE =
            "CREATE TABLE " + PoiLogDbContract.PoiLogEntry.TABLE_NAME + " (" +
                    PoiLogDbContract.PoiLogEntry._ID + " INTEGER PRIMARY KEY," +
                    PoiLogDbContract.PoiLogEntry.COLUMN_NAME_POITITLE + TEXT_TYPE + COMMA_SEP +
                    PoiLogDbContract.PoiLogEntry.COLUMN_NAME_POICATEGORY + TEXT_TYPE + COMMA_SEP +
                    PoiLogDbContract.PoiLogEntry.COLUMN_NAME_LATITUDE + TEXT_TYPE + COMMA_SEP +
                    PoiLogDbContract.PoiLogEntry.COLUMN_NAME_LONGITUDE + TEXT_TYPE + COMMA_SEP +
                    PoiLogDbContract.PoiLogEntry.COLUMN_NAME_TIMESTAMP + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PoiLogDbContract.PoiLogEntry.TABLE_NAME;

    public PoiLogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_POILOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
