package com.codefororlando.orlandowalkingtours;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.atomic.AtomicReference;

// Do not use IoC framework, keep simple for future contributors
public class VolleyProvider {
    private static final AtomicReference<RequestQueue> REQUEST_QUEUE_ATOMIC_REFERENCE =
            new AtomicReference<>();

    public static void initialize(Context context) {
        synchronized (REQUEST_QUEUE_ATOMIC_REFERENCE) {
            REQUEST_QUEUE_ATOMIC_REFERENCE.set(Volley.newRequestQueue(context));
        }
    }

    public static RequestQueue getRequestQueue() {
        synchronized (REQUEST_QUEUE_ATOMIC_REFERENCE) {
            return REQUEST_QUEUE_ATOMIC_REFERENCE.get();
        }
    }
}
