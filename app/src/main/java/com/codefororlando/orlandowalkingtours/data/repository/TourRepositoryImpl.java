package com.codefororlando.orlandowalkingtours.data.repository;

import com.codefororlando.orlandowalkingtours.RepositoryProvider;
import com.codefororlando.orlandowalkingtours.data.DatabaseHelper;
import com.codefororlando.orlandowalkingtours.data.model.Tour;

import java.util.List;

public class TourRepositoryImpl implements TourRepository {
    private final DatabaseHelper databaseHelper;

    public TourRepositoryImpl(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public List<Tour> getTours() {
        return databaseHelper.getTours();
    }

    @Override
    public Tour getTour(long tourId) {
        return databaseHelper.getTour(tourId, RepositoryProvider.getLandmark());
    }

    @Override
    public Tour saveTour(Tour tour) {
        return databaseHelper.saveTour(tour);
    }
}
