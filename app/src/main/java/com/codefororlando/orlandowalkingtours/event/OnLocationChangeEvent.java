package com.codefororlando.orlandowalkingtours.event;

import android.location.Location;

public class OnLocationChangeEvent {
    public final Location location;

    public OnLocationChangeEvent(Location location) {
        this.location = location;
    }
}
