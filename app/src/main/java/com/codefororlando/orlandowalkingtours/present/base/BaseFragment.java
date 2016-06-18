package com.codefororlando.orlandowalkingtours.present.base;

import android.support.v4.app.Fragment;
import android.support.v7.appcompat.BuildConfig;

import com.codefororlando.orlandowalkingtours.log.ClassTagLogger;
import com.codefororlando.orlandowalkingtours.log.Logger;

public class BaseFragment extends Fragment {

    // Logging

    private final Logger logger = newLogger();

    // Allows for overriding, injection not likely possible
    protected com.codefororlando.orlandowalkingtours.log.Logger newLogger() {
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
