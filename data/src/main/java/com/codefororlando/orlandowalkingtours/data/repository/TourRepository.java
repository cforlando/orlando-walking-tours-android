package com.codefororlando.orlandowalkingtours.data.repository;

import com.codefororlando.orlandowalkingtours.data.model.Tour;

import java.util.List;

public interface TourRepository {
    List<Tour> getTours();

    Tour getTour(long tourId);

    Tour saveTour(Tour tour);
}
