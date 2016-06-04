package com.codefororlando.orlandowalkingtours;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.codefororlando.orlandowalkingtours.models.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.utilities.DevelopmentUtilities;
import com.codefororlando.orlandowalkingtours.walkingtours.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<HistoricLandmark> historicLocations;
    private Context ctx;

    private HashMap<Marker, HistoricLandmark> historicMarkesMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historicLocations = (ArrayList<HistoricLandmark>) getIntent().getSerializableExtra("HLOCATIONS");
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ctx = this;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        //TODO - set users currenct location
        LatLng myLoc = new LatLng(28.544735, -81.3871897);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc,12.5f));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(ctx, LocationDetailActivity.class);

                HistoricLandmark lan = historicMarkesMap.get(marker);

                DevelopmentUtilities.logV("Sending landmark -> " + lan.getName());
                intent.putExtra("HLOCATION", lan);
                startActivity(intent);
            }
        });
        // Add a marker in Sydney and move the camera
        for(HistoricLandmark lan : historicLocations){
            LatLng temp = new LatLng(lan.getLocation().getLatitude(), lan.getLocation().getLongitude());
            Marker mark = mMap.addMarker(new MarkerOptions()
                    .position(temp)
                    .title(lan.getName())
                    .snippet(lan.getAddress())
            );

            historicMarkesMap.put(mark, lan);

        }


    }
}
