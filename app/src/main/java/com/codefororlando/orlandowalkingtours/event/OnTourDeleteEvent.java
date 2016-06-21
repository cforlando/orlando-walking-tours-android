package com.codefororlando.orlandowalkingtours.event;

public class OnTourDeleteEvent {
    public final long tourId;

    public OnTourDeleteEvent(long tourId) {
        this.tourId = tourId;
    }
}
