package com.codefororlando.orlandowalkingtours.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HistoricLandmark implements Parcelable {
    public final long id;
    public final String name;
    public final String streetAddress;
    public final String description;
    public final double latitude;
    public final double longitude;

    // TODO Thumbnail URL

    public HistoricLandmark(long id,
                            String name,
                            String description,
                            String streetAddress,
                            double latitude,
                            double longitude) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.streetAddress = streetAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return name;
    }

    // Parcelable

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeString(streetAddress);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    public static final Parcelable.Creator<HistoricLandmark> CREATOR
            = new Parcelable.Creator<HistoricLandmark>() {
        public HistoricLandmark createFromParcel(Parcel in) {
            return new HistoricLandmark(in);
        }

        public HistoricLandmark[] newArray(int size) {
            return new HistoricLandmark[size];
        }
    };

    private HistoricLandmark(Parcel in) {
        this(
                in.readLong(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readDouble(),
                in.readDouble()
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
