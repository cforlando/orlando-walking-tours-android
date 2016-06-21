package com.codefororlando.orlandowalkingtours.data.repository;

import com.codefororlando.orlandowalkingtours.data.model.Tour;

import java.util.List;

public interface TourRepository {
    List<Tour> getTours();

    Tour get(long tourId);

    Tour save(Tour tour);

    long delete(long tourId);
}
