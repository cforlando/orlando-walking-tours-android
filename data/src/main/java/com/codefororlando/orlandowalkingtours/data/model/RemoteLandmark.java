package com.codefororlando.orlandowalkingtours.data.model;

import com.google.gson.annotations.SerializedName;

// Schema defined by https://brigades.opendatanetwork.com/resource/aq56-mwpv.json
public class RemoteLandmark {
    public static final String DATA_URL =
            "https://orlando-walking-tours.firebaseio.com/historic-locations.json";

    public String id;
    public Location location;
    @SerializedName("address")
    public String streetAddress;
    public String name;
    public String type;
    public String description;

    public static class Location {
        public double latitude;
        public double longitude;
    }
}
