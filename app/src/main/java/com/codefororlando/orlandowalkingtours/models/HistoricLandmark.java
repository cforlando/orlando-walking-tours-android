package com.codefororlando.orlandowalkingtours.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by MarkoPhillipMarkovic on 5/9/2016.
 */
//This class represents Historic landmark. Use it for mapping data from the datasource
public class HistoricLandmark implements Serializable {
    private String mAddress;
    private Date mLocal;
    private Location mLocation;
    private String mLocation_city;
    private String mLocation_state;
    private String mLocation_location;
    private String mName;
    private String mType;

    public HistoricLandmark(String address, Date local, Location location, String location_location, String location_city, String location_state, String name, String type){
        mAddress = address;
        mLocal = local;
        mLocation = location;
        mLocation_city = location_city;
        mLocation_location = location_location;
        mLocation_state = location_state;
        mName = name;
        mType = type;
    }

    public String getAddress() {
        return mAddress;
    }

    public Date getLocal() {
        return mLocal;
    }

    public Location getLocation() {
        return mLocation;
    }

    public String getLocation_location() {
        return mLocation_location;
    }

    public String getLocation_city() {
        return mLocation_city;
    }

    public String getLocation_state() {
        return mLocation_state;
    }

    public String getName() {
        return mName;
    }

    public String getType() {
        return mType;
    }
}
