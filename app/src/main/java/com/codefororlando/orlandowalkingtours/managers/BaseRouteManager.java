package com.codefororlando.orlandowalkingtours.managers;

import android.content.Context;

import com.google.gson.Gson;

/**
 * Created by MarkoPhillipMarkovic on 5/10/2016.
 */
public class BaseRouteManager {
    //Holds application context
    protected Context mContext;

    //Current state and flags for data fetch.
    public final static int IDLE = 0;
    public final static int PULLING = 1;
    public final static int FINISHED = 2;
    protected int mState;

    //Gets the url for the API. Override it in your child class
    public String getUrl(){
        return "";
    }

    public void switchState(int newState) {

    }
}
