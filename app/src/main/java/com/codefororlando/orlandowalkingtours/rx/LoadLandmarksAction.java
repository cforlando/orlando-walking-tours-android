package com.codefororlando.orlandowalkingtours.rx;

import android.location.Location;

import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmarkDistance;
import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmarkDistanceSelect;
import com.codefororlando.orlandowalkingtours.data.repository.LandmarkRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class LoadLandmarksAction
        implements Observable.OnSubscribe<List<HistoricLandmarkDistanceSelect>> {
    private final LandmarkRepository landmarkRepository;
    private final Location location;
    private final Comparator<HistoricLandmarkDistance> comparator;

    public LoadLandmarksAction(LandmarkRepository landmarkRepository,
                               Location location,
                               Comparator<HistoricLandmarkDistance> comparator) {
        this.landmarkRepository = landmarkRepository;
        this.location = location;
        this.comparator = comparator;
    }

    @Override
    public void call(final Subscriber<? super List<HistoricLandmarkDistanceSelect>> subscriber) {
        Observable.from(landmarkRepository.getLandmarks())
                .map(new Func1<HistoricLandmark, HistoricLandmarkDistanceSelect>() {
                    @Override
                    public HistoricLandmarkDistanceSelect call(HistoricLandmark landmark) {
                        HistoricLandmarkDistanceSelect landmarkDistanceSelect =
                                new HistoricLandmarkDistanceSelect(landmark);
                        landmarkDistanceSelect.setLocation(location);
                        return landmarkDistanceSelect;
                    }
                })
                .toList()
                .subscribe(new Action1<List<HistoricLandmarkDistanceSelect>>() {
                    @Override
                    public void call(List<HistoricLandmarkDistanceSelect> landmarkSelects) {
                        Collections.sort(landmarkSelects, comparator);
                        subscriber.onNext(landmarkSelects);
                        subscriber.onCompleted();
                    }
                });
    }
}
