package com.codefororlando.orlandowalkingtours.service;

import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.codefororlando.orlandowalkingtours.event.OnLocationChangeEvent;
import com.codefororlando.orlandowalkingtours.present.base.BaseService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Broadcasts location when possible and on change in location.
 * <p/>
 * Never start this service, bind only for simpler lifecycle management.
 */
public class LocationService extends BaseService
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private final IBinder binder = new LocalBinder();

    private GoogleApiClient mGoogleApiClient;

    private boolean mIsReceivingLocation;

    private Location mLastLocation;

    public class LocalBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        busSubscribe();
        setApiClient();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Apply bound service pattern and avoid managing lifecycle.
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        busUnsubscribe();
        super.onDestroy();
    }

    // Private

    private void setApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    private void onConnectionLost() {
        mIsReceivingLocation = false;
        stopLocationUpdates();
    }

    // GoogleApiClient.ConnectionCallbacks

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        onConnectionLost();
    }

    // GoogleApiClient.OnConnectionFailedListener

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        onConnectionLost();
    }

    // LocationListener

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        mIsReceivingLocation = true;

        publishLocation(location);
    }

    private void publishLocation(Location location) {
        bus.publish(new OnLocationChangeEvent(location));
    }

    private void startLocationUpdates() {
        if (!isLocationEnabled()) {
            setApiClient();
            return;
        }

        if (mIsReceivingLocation) {
            // A client wanting location updates would be interested in the last location
            if (mLastLocation != null) {
                publishLocation(mLastLocation);
            }
            return;
        }

        final GoogleApiClient client = mGoogleApiClient;
        if (isLocationEnabled(client)) {
            LocationRequest locationRequest = new LocationRequest()
                    .setInterval(30000)
                    .setFastestInterval(1000)
                    .setSmallestDisplacement(3)
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            try {
                FusedLocationProviderApi locationApi = LocationServices.FusedLocationApi;
                PendingResult<Status> pendingStatus =
                        locationApi.requestLocationUpdates(client, locationRequest, this);
                // Log info on unsuccessful
                pendingStatus.setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (!status.isSuccess()) {
                            String message = status.getStatusMessage();
                            logI(String.format("Location request fail %s", message));
                        }
                    }
                });
            } catch (SecurityException e) {
                onLocationSecurityException(e);
            }
        }
    }

    private void onLocationSecurityException(SecurityException e) {
        mIsReceivingLocation = false;
        logE("Location fail", e);
    }

    private void stopLocationUpdates() {
        mIsReceivingLocation = false;

        GoogleApiClient client = mGoogleApiClient;
        if (isLocationEnabled(client)) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }
    }

    private boolean isLocationEnabled() {
        return isLocationEnabled(mGoogleApiClient);
    }

    private boolean isLocationEnabled(GoogleApiClient client) {
        return client != null &&
                client.isConnected() &&
                client.hasConnectedApi(LocationServices.API);
    }

    // API

    public void publishLocations() {
        startLocationUpdates();
    }
}
