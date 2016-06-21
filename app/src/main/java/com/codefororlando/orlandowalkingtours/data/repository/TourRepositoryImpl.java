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
    public Tour get(long tourId) {
        return databaseHelper.getTour(tourId, RepositoryProvider.getLandmark());
    }

    @Override
    public Tour save(Tour tour) {
        return databaseHelper.saveTour(tour);
    }

    @Override
    public long delete(long tourId) {
        return databaseHelper.deleteTour(tourId);
    }
}
