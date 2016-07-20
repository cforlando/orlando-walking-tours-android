package com.codefororlando.orlandowalkingtours.present.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.codefororlando.orlandowalkingtours.event.OnPermissionGrantEvent;
import com.codefororlando.orlandowalkingtours.present.base.BaseFragment;
import com.codefororlando.orlandowalkingtours.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Requests various permissions
 */
public class PermissionRequestFragment extends BaseFragment {
    // Called when fragment is no longer needed and should be removed
    public interface OnPermissionRequestCompleteListener {
        void onPermissionRequestComplete();
    }

    public static final String PERMISSION_KEY = "PERMISSION_KEY";

    public static PermissionRequestFragment newInstance(String... permissions) {
        PermissionRequestFragment fragment = new PermissionRequestFragment();
        Bundle arguments = new Bundle();
        arguments.putStringArray(PERMISSION_KEY, permissions);
        fragment.setArguments(arguments);
        return fragment;
    }

    protected final int permissionRequestCode = 9312;

    private OnPermissionRequestCompleteListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        for (Object o : new Object[]{getActivity(), getTargetFragment()}) {
            if (o instanceof OnPermissionRequestCompleteListener) {
                mListener = (OnPermissionRequestCompleteListener) o;
                break;
            }
        }
        if (mListener == null) {
            throw new IllegalArgumentException("Implement OnPermissionRequestCompleteListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        busSubscribe();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForPermission();
    }

    @Override
    public void onDestroy() {
        busUnsubscribe();
        super.onDestroy();
    }

    private void checkForPermission() {
        Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey(PERMISSION_KEY)) {
            mListener.onPermissionRequestComplete();
            return;
        }

        checkForPermission(arguments.getStringArray(PERMISSION_KEY));
    }

    private void checkForPermission(String[] permissions) {
        List<String> noPermission = new ArrayList<>();
        PermissionUtil permissionUtil = PermissionUtil.get();
        for (String permission : permissions) {
            if (!permissionUtil.hasPermission(permission)) {
                noPermission.add(permission);
            }
        }

        int size = noPermission.size();
        if (size > 0) {
            requestPermissions(noPermission.toArray(new String[size]), permissionRequestCode);
        } else {
            mListener.onPermissionRequestComplete();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == permissionRequestCode) {
            int i = 0;
            for (String permission : permissions) {
                boolean isGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                onPermissionGrant(permission, isGranted);
                i++;
            }

            mListener.onPermissionRequestComplete();

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void onPermissionGrant(String permission, boolean isGranted) {
        if (isGranted) {
            bus.publish(new OnPermissionGrantEvent(permission));
        }
    }
}
