package com.codefororlando.orlandowalkingtours.log;

import android.util.Log;

public class ClassTagLogger implements Logger {
    private final String tag;
    private final boolean isDebug;

    public ClassTagLogger(Object o, boolean isDebug) {
        tag = o.getClass().getSimpleName();
        this.isDebug = isDebug;
    }

    @Override
    public void debug(String message) {
        if (isDebug) {
            Log.d(tag, message);
        }
    }

    /**
     * Objects must conform the the format string (or exceptions will occur)
     */
    @Override
    public void debug(String format, Object... objects) {
        if (isDebug) {
            debug(String.format(format, objects));
        }
    }

    @Override
    public void info(String s) {
        Log.i(tag, s);
    }

    @Override
    public void error(String message, Throwable throwable) {
        Log.e(tag, message, throwable);
    }
}
