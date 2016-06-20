package com.codefororlando.orlandowalkingtours;

import com.android.volley.RequestQueue;
import com.codefororlando.orlandowalkingtours.data.repository.LandmarkRepository;
import com.codefororlando.orlandowalkingtours.data.repository.LandmarkRepositoryImpl;
import com.codefororlando.orlandowalkingtours.data.repository.TourRepository;
import com.codefororlando.orlandowalkingtours.data.repository.TourRepositoryImpl;
import com.codefororlando.orlandowalkingtours.data.DatabaseHelper;
import com.codefororlando.orlandowalkingtours.event.RxBus;
import com.codefororlando.orlandowalkingtours.log.ClassTagLogger;

// Do not use IoC framework, keep simple for future contributors
public class RepositoryProvider {
    private static LandmarkRepository sLandmarkRepository;
    private static TourRepository sTourRepository;

    public static void initialize(DatabaseHelper databaseHelper,
                                  RequestQueue requestQueue,
                                  RxBus bus,
                                  boolean isDebug) {
        ClassTagLogger landmarkRepositoryLogger =
                new ClassTagLogger(LandmarkRepository.class, isDebug);
        sLandmarkRepository = new LandmarkRepositoryImpl(
                databaseHelper,
                requestQueue,
                bus,
                landmarkRepositoryLogger
        );

        sTourRepository = new TourRepositoryImpl(databaseHelper);
    }

    public static LandmarkRepository getLandmark() {
        return sLandmarkRepository;
    }

    public static TourRepository getTour() {
        return sTourRepository;
    }
}
