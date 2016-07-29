package com.codefororlando.orlandowalkingtours.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class LocationServiceConnection implements ServiceConnection {
    private LocationService mService;

    public void publishLocations() {
        if (mService == null) {
            return;
        }

        mService.publishLocations();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mService = ((LocationService.LocalBinder) iBinder).getService();
        publishLocations();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        // Rarely happens, log is sufficient
        String message = String.format("%s has disconnected", componentName.toString());
        Log.e(getClass().getSimpleName(), message);
    }
}
