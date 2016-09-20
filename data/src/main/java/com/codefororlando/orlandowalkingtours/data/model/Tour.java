package com.codefororlando.orlandowalkingtours.data.model;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Tour {
    public final long id;
    public final String name;
    private final AtomicReference<List<Long>> tourStopIdsAr = new AtomicReference<>();

    public Tour(long id, Tour tour) {
        this(id, tour.name, tour.tourStopIdsAr.get());
    }

    @SuppressWarnings("unchecked")
    public Tour(long id, String name) {
        this(id, name, Collections.EMPTY_LIST);
    }

    public Tour(long id, String name, List<Long> stops) {
        this.id = id;
        this.name = name;
        setTourStops(stops);
    }

    public void setTourStops(List<Long> stops) {
        synchronized (tourStopIdsAr) {
            tourStopIdsAr.set(stops == null ? Collections.<Long>emptyList() : stops);
        }
    }

    public List<Long> getTourStopIds() {
        return tourStopIdsAr.get();
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", name, tourStopIdsAr.get().size());
    }
}
