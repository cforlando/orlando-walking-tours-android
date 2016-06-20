package com.codefororlando.orlandowalkingtours.data.model;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Tour {
    public final long id;
    public final String name;
    private final AtomicReference<List<HistoricLandmark>> tourStopsAr = new AtomicReference<>();

    public Tour(long id, Tour tour) {
        this(id, tour.name, tour.tourStopsAr.get());
    }

    @SuppressWarnings("unchecked")
    public Tour(long id, String name) {
        this(id, name, Collections.EMPTY_LIST);
    }

    public Tour(long id, String name, List<HistoricLandmark> stops) {
        this.id = id;
        this.name = name;
        setTourStops(stops);
    }

    @SuppressWarnings("unchecked")
    public void setTourStops(List<HistoricLandmark> stops) {
        synchronized (tourStopsAr) {
            tourStopsAr.set(stops == null ? Collections.EMPTY_LIST : stops);
        }
    }

    public List<HistoricLandmark> getTourStops() {
        return tourStopsAr.get();
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", name, tourStopsAr.get().size());
    }
}
