package com.codefororlando.orlandowalkingtours.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

// Schema defined by https://brigades.opendatanetwork.com/resource/aq56-mwpv.json
public class RemoteLandmark {
    public static final String DATA_URL =
            "https://brigades.opendatanetwork.com/resource/aq56-mwpv.json";

    public int id;
    public Location location;
    @SerializedName("local")
    public Date localDate;
    @SerializedName("address")
    public String streetAddress;
    @SerializedName("location_city")
    public String city;
    @SerializedName("location_state")
    public String state;
    public String name;
    public String type;
    @SerializedName("downtown_walking_tour")
    public String description;

    public static class Location {
        public String type;
        public double[] coordinates;

    }
}
