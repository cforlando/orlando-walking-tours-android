package com.codefororlando.orlandowalkingtours.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class PermissionUtil {
    private static PermissionUtil sInstance;

    public static void initialize(Context context) {
        sInstance = new PermissionUtil(context);
    }

    public static PermissionUtil get() {
        return sInstance;
    }

    private final Context context;

    public PermissionUtil(Context context) {
        this.context = context;
    }

    public boolean hasPermission(String permission) {
        int permissionStatus = ContextCompat.checkSelfPermission(context, permission);
        return permissionStatus == PackageManager.PERMISSION_GRANTED;
    }

    private final String[] locationPermissions = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public boolean hasLocationPermission() {
        for (String permission : locationPermissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }
}
