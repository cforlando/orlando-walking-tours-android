package com.codefororlando.orlandowalkingtours.rx;

import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmarkDistance;
import com.codefororlando.orlandowalkingtours.data.model.Tour;
import com.codefororlando.orlandowalkingtours.data.repository.TourRepository;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class SaveTourAction implements Observable.OnSubscribe<Tour> {
    private final Tour tour;
    private final List<HistoricLandmarkDistance> stops;
    private final TourRepository tourRepository;

    public SaveTourAction(Tour tour,
                          List<HistoricLandmarkDistance> stops,
                          TourRepository tourRepository) {
        this.tour = tour;
        this.stops = stops;
        this.tourRepository = tourRepository;
    }

    @Override
    public void call(final Subscriber<? super Tour> subscriber) {
        Observable.from(stops)
                .map(new Func1<HistoricLandmarkDistance, Long>() {
                    @Override
                    public Long call(HistoricLandmarkDistance landmarkDistance) {
                        return landmarkDistance.landmark.id;
                    }
                })
                .toList()
                .subscribe(new Action1<List<Long>>() {
                    @Override
                    public void call(List<Long> ids) {
                        tour.setTourStops(ids);
                        Tour savedTour = tourRepository.save(tour);
                        subscriber.onNext(savedTour);
                        subscriber.onCompleted();
                    }
                });
    }
}
