package com.codefororlando.orlandowalkingtours.data;

import android.content.Context;

import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.data.model.RemoteLandmark;
import com.codefororlando.orlandowalkingtours.data.model.Tour;
import com.codefororlando.orlandowalkingtours.data.repository.LandmarkRepository;
import com.codefororlando.orlandowalkingtours.log.Logger;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

// Do not use IoC framework, keep simple for future contributors
public class DatabaseHelper extends DatabaseHelperDefine {
    private static final AtomicReference<DatabaseHelper> INSTANCE = new AtomicReference<>();

    public static void initialize(Context context, Logger logger) {
        synchronized (INSTANCE) {
            DatabaseHelper databaseHelper = INSTANCE.get();
            if (databaseHelper == null) {
                databaseHelper = new DatabaseHelper(context, logger);
                INSTANCE.set(databaseHelper);
            }
        }
    }

    public static DatabaseHelper get() {
        return INSTANCE.get();
    }

    protected DatabaseHelper(Context context, Logger logger) {
        super(context, logger);
    }

    public List<HistoricLandmark> getLandmarks() {
        return landmarkTable.get(getReadableDatabase());
    }

    public List<HistoricLandmark> saveLandmarks(List<RemoteLandmark> landmarks) {
        return landmarkTable.save(getWritableDatabase(), landmarks);
    }

    public List<Tour> getTours() {
        return tourTable.get(getReadableDatabase(), tourLandmarkTable);
    }

    public Tour getTour(long tourId, LandmarkRepository landmarkRepository) {
        return tourTable.get(getReadableDatabase(), tourId, tourLandmarkTable);
    }

    public Tour saveTour(Tour tour) {
        return tourTable.save(getWritableDatabase(), tour, tourLandmarkTable);
    }

    public long deleteTour(long tourId) {
        return tourTable.delete(getWritableDatabase(), tourId);
    }
}
