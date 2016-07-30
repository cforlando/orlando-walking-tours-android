package com.codefororlando.orlandowalkingtours.present.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.codefororlando.orlandowalkingtours.R;
import com.codefororlando.orlandowalkingtours.service.LocationService;
import com.codefororlando.orlandowalkingtours.service.LocationServiceConnection;

import butterknife.OnClick;

// Requires layout to contain actions with IDs 'done' and 'cancel'
abstract public class DoneCancelBarLocationFragment extends ButterKnifeFragment {
    // Done+cancel bar

    @OnClick(R.id.done)
    protected void onDone() {
    }

    @OnClick(R.id.cancel)
    protected void onCancel() {
    }

    // Location

    private LocationServiceConnection mLocationServiceConnection;

    // Call in {@link #onStart}
    protected void bindLocationService() {
        // Create if NULL because fragment state doesn't keep when transactions are made
        if (mLocationServiceConnection == null) {
            mLocationServiceConnection = new LocationServiceConnection();
        }

        Activity activity = getActivity();
        activity.bindService(
                new Intent(activity, LocationService.class),
                mLocationServiceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    // Call in {@link #onStop}
    protected void unbindLocationService() {
        LocationServiceConnection serviceConnection = mLocationServiceConnection;
        // Do not access service after unbinding
        mLocationServiceConnection = null;
        getActivity().unbindService(serviceConnection);
    }

    protected void startLocationPublish() {
        if (mLocationServiceConnection != null) {
            mLocationServiceConnection.publishLocations();
        }
    }
}
