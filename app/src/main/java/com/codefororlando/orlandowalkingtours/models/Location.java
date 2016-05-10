package com.codefororlando.orlandowalkingtours.models;

/**
 * Created by MarkoPhillipMarkovic on 5/9/2016.
 */
//This class holds location data. Use it for mapping data from the datasource.
public class Location {
    private String mType;
    private double mLatitude;
    private double mLongitude;

    public Location(String type, double latitude, double longitude){
        mType = type;
        mLatitude = latitude;
        mLongitude = longitude;
    }


    public String getType() {
        return mType;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }
}
