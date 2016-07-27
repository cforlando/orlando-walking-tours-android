package com.codefororlando.orlandowalkingtours.data.model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.text.Collator;
import java.util.Comparator;

// TODO Rename if contains other UI properties
public class HistoricLandmarkSelect {
    private static final Collator COLLATOR = Collator.getInstance();
    public static final Comparator<HistoricLandmarkSelect> NAME_COMPARATOR =
            new Comparator<HistoricLandmarkSelect>() {
                @Override
                public int compare(HistoricLandmarkSelect f, HistoricLandmarkSelect g) {
                    HistoricLandmark h = f.landmark,
                            i = g.landmark;
                    return COLLATOR.compare(h.name, i.name);
                }
            };

    public static class SquareDistanceComparator implements Comparator<HistoricLandmarkSelect> {
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
        public int compare(HistoricLandmarkSelect f, HistoricLandmarkSelect g) {
            HistoricLandmark h = f.landmark,
                    i = g.landmark;
            double latHDelta = Math.abs(latitude - h.latitude),
                    lngHDelta = Math.abs(longitude - h.longitude),
                    latIDelta = Math.abs(latitude - i.latitude),
                    lngIDelta = Math.abs(longitude - i.longitude);
            if (latHDelta < latIDelta && lngHDelta < lngIDelta) {
                return -1;
            } else if (latIDelta < latHDelta && lngIDelta < lngHDelta) {
                return 1;
            }
            double hypH = latHDelta * latHDelta + lngHDelta * lngHDelta,
                    hypI = latIDelta * latIDelta + lngIDelta * lngIDelta;
            return hypH < hypI ? -1 : 1;
        }
    }

    public boolean isSelected;
    public final HistoricLandmark landmark;

    private final LatLng coordinates;

    private String mDistanceText;

    public HistoricLandmarkSelect(HistoricLandmark historicLandmark) {
        landmark = historicLandmark;
        coordinates = new LatLng(historicLandmark.latitude, historicLandmark.longitude);
    }

    public String getDistanceText() {
        return mDistanceText;
    }

    public void setDistanceText(LatLng latLng) {
        // TODO Convert to proper units and format, use string res, mi/km
        double distance = SphericalUtil.computeDistanceBetween(latLng, coordinates);
        mDistanceText = String.format("(%.1f %s)", distance, "mi");
    }
}
