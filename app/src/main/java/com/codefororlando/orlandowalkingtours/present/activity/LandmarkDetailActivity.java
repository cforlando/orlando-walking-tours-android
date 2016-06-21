package com.codefororlando.orlandowalkingtours.present.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.RepositoryProvider;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.present.base.BaseActivity;
import com.codefororlando.orlandowalkingtours.present.fragment.LandmarkDetailFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LandmarkDetailActivity extends BaseActivity implements OnMapReadyCallback {
    private static final String LANDMARK_ID_KEY = "LANDMARK_ID_KEY";

    public static Intent getIntent(Context context, long landmarkId) {
        return new Intent(context, LandmarkDetailActivity.class)
                .putExtra(LANDMARK_ID_KEY, landmarkId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landmark_detail_activity);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if (fragment instanceof SupportMapFragment) {
            ((SupportMapFragment) fragment).getMapAsync(this);
        } else if (fragment instanceof LandmarkDetailFragment) {
            ((LandmarkDetailFragment) fragment).setLandmark(getLandmark());
        }
    }

    // OnMapReadyCallback

    @Override
    public void onMapReady(GoogleMap googleMap) {
        HistoricLandmark landmark = getLandmark();
        double latitude = landmark.latitude,
                longitude = landmark.longitude;
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(landmark.name));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    // Data

    private HistoricLandmark getLandmark() {
        long landmarkId = getIntent().getLongExtra(LANDMARK_ID_KEY, 0);
        return RepositoryProvider.getLandmark().getLandmark(landmarkId);
    }
}
