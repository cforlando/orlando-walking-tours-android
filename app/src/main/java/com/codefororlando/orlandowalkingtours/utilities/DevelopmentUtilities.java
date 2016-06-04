package com.codefororlando.orlandowalkingtours.utilities;

import android.util.Log;

/**
 * Created by MarkoPhillipMarkovic on 5/11/2016.
 */
/* This class is intended to create the development of the app easier. Instead of writing down the same TAG every time you need to use logcat, use this class */
public class DevelopmentUtilities {
    public static String TAG = "ORLANDOWALKINGTOURS";

    public static void logE(String textToPrint)
    {
        Log.e(TAG, textToPrint);
    }
    public static void logV(String textToPrint)
    {
        Log.v(TAG, textToPrint);
    }
    public static void logI(String textToPrint)
    {
        Log.i(TAG, textToPrint);
    }
    public static void logD(String textToPrint)
    {
        Log.d(TAG, textToPrint);
    }
}
