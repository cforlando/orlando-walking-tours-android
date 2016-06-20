package com.codefororlando.orlandowalkingtours.present.base;

import android.support.v7.app.AppCompatActivity;

import com.codefororlando.orlandowalkingtours.BuildConfig;
import com.codefororlando.orlandowalkingtours.log.ClassTagLogger;
import com.codefororlando.orlandowalkingtours.log.Logger;

public class BaseActivity extends AppCompatActivity {

    // Logging

    private final Logger logger = newLogger();

    // Allows for overriding, injection not likely possible
    protected Logger newLogger() {
        return new ClassTagLogger(this, BuildConfig.DEBUG);
    }

    protected void logD(String s) {
        logger.debug(s);
    }

    protected void logD(String format, Object... objects) {
        logger.debug(format, objects);
    }

    protected void logI(String s) {
        logger.info(s);
    }

    protected void logE(String s, Throwable throwable) {
        logger.error(s, throwable);
    }
}
