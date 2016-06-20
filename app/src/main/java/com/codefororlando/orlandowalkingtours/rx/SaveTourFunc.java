package com.codefororlando.orlandowalkingtours.rx;

import com.codefororlando.orlandowalkingtours.data.model.Tour;
import com.codefororlando.orlandowalkingtours.data.repository.TourRepository;

import rx.functions.Func1;

public class SaveTourFunc implements Func1<Tour, Tour> {
    private final TourRepository tourRepository;

    public SaveTourFunc(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    @Override
    public Tour call(Tour tour) {
        return tourRepository.saveTour(tour);
    }
}
