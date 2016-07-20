package com.codefororlando.orlandowalkingtours;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.codefororlando.orlandowalkingtours.data.DatabaseHelper;
import com.codefororlando.orlandowalkingtours.log.ClassTagLogger;
import com.codefororlando.orlandowalkingtours.util.PermissionUtil;

public class App extends Application {
    @Override
    public void onCreate() {
        setStrict();

        super.onCreate();

        initializeSingleton();
    }

    // Require best practices
    private void setStrict() {
        StrictMode.setThreadPolicy(
                new StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .penaltyDeathOnNetwork()
                        .penaltyFlashScreen()
                        .build()
        );
        StrictMode.setVmPolicy(
                new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectLeakedClosableObjects()
                        .penaltyLog()
                        .penaltyDeath()
                        .build()
        );
    }

    private void initializeSingleton() {
        // StrictMode$StrictModeDiskReadViolation
        final Context context = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                VolleyProvider.initialize(context);
            }
        }).start();

        BusProvider.initialize();

        boolean isDebug = BuildConfig.DEBUG;
        DatabaseHelper.initialize(this, new ClassTagLogger(DatabaseHelper.class, isDebug));

        RepositoryProvider.initialize(
                DatabaseHelper.get(), VolleyProvider.getRequestQueue(), BusProvider.get(), isDebug);
        RepositoryProvider.getLandmark().queryLandmarks();

        PermissionUtil.initialize(this);
    }
}
