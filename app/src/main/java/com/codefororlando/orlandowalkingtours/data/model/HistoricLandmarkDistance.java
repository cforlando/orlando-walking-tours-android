package com.codefororlando.orlandowalkingtours.data.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.text.Collator;
import java.util.Comparator;

public class HistoricLandmarkDistance {
    private static final Collator COLLATOR = Collator.getInstance();
    public static final Comparator<HistoricLandmarkDistance> NAME_COMPARATOR =
            new Comparator<HistoricLandmarkDistance>() {
                @Override
                public int compare(HistoricLandmarkDistance f, HistoricLandmarkDistance g) {
                    HistoricLandmark h = f.landmark,
                            i = g.landmark;
                    return COLLATOR.compare(h.name, i.name);
                }
            };

    public static class SquareDistanceComparator implements Comparator<HistoricLandmarkDistance> {
        private final double latitude,
                longitude;

        public SquareDistanceComparator(Location location) {
            this(location.getLatitude(), location.getLongitude());
        }

        public SquareDistanceComparator(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public int compare(HistoricLandmarkDistance f, HistoricLandmarkDistance g) {
            HistoricLandmark h = f.landmark,
                    i = g.landmark;
            double latHDelta = Math.abs(latitude - h.latitude),
                    lngHDelta = Math.abs(longitude - h.longitude),
                    latIDelta = Math.abs(latitude - i.latitude),
                    lngIDelta = Math.abs(longitude - i.longitude);
            if (latHDelta < latIDelta && lngHDelta < lngIDelta) {
                return -1;
            } else if (latHDelta > latIDelta && lngHDelta > lngIDelta) {
                return 1;
            }
            double hypH = Math.hypot(latHDelta, lngHDelta),
                    hypI = Math.hypot(latIDelta, lngIDelta);
            return hypH < hypI ? -1 : 1;
        }
    }

    public final HistoricLandmark landmark;

    // For quick reference rather than having to access through landmark
    private final LatLng coordinates;
    private Location mLastLocation;

    // Distance as computed externally
    private String mDistanceText;

    public HistoricLandmarkDistance(HistoricLandmark historicLandmark) {
        landmark = historicLandmark;
        coordinates = new LatLng(historicLandmark.latitude, historicLandmark.longitude);
    }

    public String getDistanceText() {
        return mDistanceText;
    }

    public void setLocation(Location location) {
        if (mLastLocation != null && mLastLocation.equals(location)) {
            return;
        }

        mLastLocation = location;
        if (location == null) {
            mDistanceText = "";
        } else {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            /*
             * TODO Convert to proper units and format, use string res, mi/km
             *      Will need to actively update if units can be toggled
             */
            double distanceMeter = SphericalUtil.computeDistanceBetween(latLng, coordinates);
            double distanceMile = distanceMeter / 1609.34;
            mDistanceText = String.format("(%.1f %s)", distanceMile, "mi");
        }
    }
}
