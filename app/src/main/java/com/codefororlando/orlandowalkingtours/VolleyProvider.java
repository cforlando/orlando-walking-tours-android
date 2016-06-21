package com.codefororlando.orlandowalkingtours;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

// Do not use IoC framework, keep simple for future contributors
public class VolleyProvider {
    private static RequestQueue sRequestQueue;

    public static void initialize(Context context) {
        sRequestQueue = Volley.newRequestQueue(context);
    }

    public static RequestQueue getRequestQueue() {
        return sRequestQueue;
    }
}
