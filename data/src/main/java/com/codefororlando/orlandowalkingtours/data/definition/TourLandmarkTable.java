package com.codefororlando.orlandowalkingtours.data.definition;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.data.repository.LandmarkRepository;

import java.util.LinkedList;
import java.util.List;

public class TourLandmarkTable implements SqliteDefinition {
    public static final String TABLE_NAME = "tourLandmark",
            TOUR_ID = "tourId",
            LANDMARK_ID = "landmarkId",
            TOUR_STOP = "tourStop";

    @Override
    public String getCreateStatement() {
        String idColumn = BaseColumns._ID;
        return String.format(
                "create table if not exists %s (%s,%s,%s,%s,%s,%s)",
                TABLE_NAME,
                String.format("%s integer primary key", BaseColumns._ID),
                String.format("%s integer", TOUR_ID),
                String.format("%s integer", LANDMARK_ID),
                String.format("%s integer", TOUR_STOP),
                String.format("foreign key(%s) references %s(%s)", TOUR_ID, TourTable.TABLE_NAME, idColumn),
                String.format("foreign key(%s) references %s(%s)", LANDMARK_ID, LandmarkTable.TABLE_NAME, idColumn)
        );
    }

    @Override
    public void onUpdate(SQLiteDatabase database, int oldVersion, int newVersion) {
    }

    @TargetApi(19)
    public List<HistoricLandmark> getLandmarks(SQLiteDatabase database,
                                               long tourId,
                                               LandmarkRepository landmarkRepository) {
        String sql = String.format(
                "select %s from %s where %s=? order by %s asc",
                TourLandmarkTable.LANDMARK_ID,
                TourLandmarkTable.TABLE_NAME,
                TourLandmarkTable.TOUR_ID,
                TourLandmarkTable.TOUR_STOP
        );
        try (Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(tourId)})) {
            List<HistoricLandmark> tourStops = new LinkedList<>();
            while (cursor.moveToNext()) {
                HistoricLandmark landmark = landmarkRepository.getLandmark(cursor.getLong(0));
                // This should never be null but future changes may break something
                tourStops.add(landmark);
            }
            return tourStops;
        }
    }
}
