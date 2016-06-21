package com.codefororlando.orlandowalkingtours.event;

import com.codefororlando.orlandowalkingtours.data.model.Tour;

public class OnTourSaveEvent {
    public final Tour tour;

    public OnTourSaveEvent(Tour tour) {
        this.tour = tour;
    }
}
