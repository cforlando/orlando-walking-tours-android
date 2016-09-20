package com.codefororlando.orlandowalkingtours.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;

import com.codefororlando.orlandowalkingtours.present.fragment.PermissionRequestFragment;

public class PermissionUtil {
    private static PermissionUtil sInstance;

    public static void initialize(Context context) {
        sInstance = new PermissionUtil(context);
    }

    public static PermissionUtil get() {
        return sInstance;
    }

    private final Context context;

    private final String permissionRequestFragmentTag = "requestLocationPermission";

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

    public boolean isLocationPermission(String permission) {
        for (String s : locationPermissions) {
            if (s.equals(permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasLocationPermission() {
        for (String permission : locationPermissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    // Request location

    public boolean hasDeniedPermission(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * @return TRUE if location permission has been previously denied or FALSE otherwise
     */
    public boolean hasDeniedLocationPermissionRequest(Activity activity) {
        boolean hasDenied = false;
        for (String permission : locationPermissions) {
            hasDenied |= hasDeniedPermission(activity, permission);
        }
        return hasDenied;
    }

    // Request view convenience

    public boolean showLocationPermissionFragment(FragmentManager fragmentManager) {
        return showLocationPermissionFragment(fragmentManager, null);
    }

    /**
     * @return TRUE if the request location fragment is shown or FALSE if no action is taken
     * @see #removeRequestLocationPermissionFragment(FragmentManager)
     */
    public boolean showLocationPermissionFragment(FragmentManager fragmentManager,
                                                  Fragment targetFragment) {
        boolean hasLocationPermission = PermissionUtil.get().hasLocationPermission();
        if (!hasLocationPermission) {
            String tag = permissionRequestFragmentTag;
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment == null) {
                String locationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
                fragment = PermissionRequestFragment.newInstance(locationPermission);
                if (targetFragment != null) {
                    fragment.setTargetFragment(targetFragment, 0);
                }
                fragmentManager.beginTransaction()
                        .add(fragment, tag)
                        .commit();
            }

            return true;
        }

        return false;
    }

    /**
     * @see #showLocationPermissionFragment(FragmentManager)
     * @see #showLocationPermissionFragment(FragmentManager, Fragment)
     */
    public void removeRequestLocationPermissionFragment(FragmentManager fragmentManager) {
        Fragment fragment = fragmentManager.findFragmentByTag(permissionRequestFragmentTag);
        if (fragment != null) {
            fragmentManager.beginTransaction()
                    .remove(fragment)
                    // Stateless fragment can be removed at any time
                    .commitAllowingStateLoss();
        }
    }
}
