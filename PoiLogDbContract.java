package com.example.unipitouristapp;

import android.provider.BaseColumns;

public class PoiLogDbContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private PoiLogDbContract() {}
    // Inner class that defines the table contents
    public static class PoiLogEntry implements BaseColumns {
        public static final String TABLE_NAME = "poiLogDetails";
        public static final String COLUMN_NAME_POITITLE = "PoiTitle";
        public static final String COLUMN_NAME_POICATEGORY = "PoiCategory";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}
