package com.example.unipitouristapp;

public class PoiLog { //This object is used to create a new entry in the local db.

    private int poiLog_id;
    private String poiLog_title;
    private String poiLog_category;
    private String poiLog_latitude;
    private String poiLog_longitude;
    private String poiLog_timestamp;

    public PoiLog(int poiLog_id, String poiLog_title, String poiLog_category, String poiLog_latitude, String poiLog_longitude, String poiLog_timestamp) {
        this.poiLog_id = poiLog_id;
        this.poiLog_title = poiLog_title;
        this.poiLog_category =  poiLog_category;
        this.poiLog_latitude = poiLog_latitude;
        this.poiLog_longitude = poiLog_longitude;
        this.poiLog_timestamp = poiLog_timestamp;
    }

    public PoiLog(String poiLog_title, String poiLog_category, String poiLog_latitude, String poiLog_longitude, String poiLog_timestamp) {
        this.poiLog_title = poiLog_title;
        this.poiLog_category =  poiLog_category;
        this.poiLog_latitude = poiLog_latitude;
        this.poiLog_longitude = poiLog_longitude;
        this.poiLog_timestamp = poiLog_timestamp;
    }

    public int getPoiLog_id() {
        return poiLog_id;
    }

    public String getPoiLog_latitude() {
        return poiLog_latitude;
    }

    public String getPoiLog_longitude() {
        return poiLog_longitude;
    }

    public String getPoiLog_title() {
        return poiLog_title;
    }

    public String getPoiLog_timestamp() {
        return poiLog_timestamp;
    }

    public String getPoiLog_category() {
        return poiLog_category;
    }

    public void setPoiLog_category(String poiLog_category) {
        this.poiLog_category = poiLog_category;
    }
}
