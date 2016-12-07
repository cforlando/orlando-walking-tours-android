package com.codefororlando.orlandowalkingtours.data.definition;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.codefororlando.orlandowalkingtours.data.model.Tour;

import java.util.LinkedList;
import java.util.List;

public class TourLandmarkTable extends AutoIncrementIdTable {
    public static final String TABLE_NAME = "tourLandmark",
            TOUR_ID = "tourId",
            LANDMARK_ID = "landmarkId",
            TOUR_STOP = "tourStop";

    @Override
    public String getCreateStatement() {
        String idColumn = _ID;
        return String.format(
                "create table if not exists %s (%s,%s,%s,%s)",
                TABLE_NAME,
                AUTO_INCREMENT_ID_COLUMN,
                String.format("%s integer references %s(%s) on delete cascade", TOUR_ID, TourTable.TABLE_NAME, idColumn),
                String.format("%s integer references %s(%s)", LANDMARK_ID, LandmarkTable.TABLE_NAME, idColumn),
                String.format("%s integer", TOUR_STOP)
        );
    }

    @Override
    public void onUpdate(SQLiteDatabase database, int oldVersion, int newVersion) {
        // TODO Alter schema properly as necessary
    }

    @TargetApi(19)
    public List<Long> getLandmarkIds(SQLiteDatabase database, long tourId) {
        String sql = String.format(
                "select %s from %s where %s=? order by %s asc",
                TourLandmarkTable.LANDMARK_ID,
                TourLandmarkTable.TABLE_NAME,
                TourLandmarkTable.TOUR_ID,
                TourLandmarkTable.TOUR_STOP
        );
        try (Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(tourId)})) {
            List<Long> tourStopIds = new LinkedList<>();
            while (cursor.moveToNext()) {
                tourStopIds.add(cursor.getLong(0));
            }
            return tourStopIds;
        }
    }

    public int deleteTourStops(SQLiteDatabase database, long tourId) {
        String sql = String.format("delete from %s where %s=?", TABLE_NAME, TOUR_ID);
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindLong(1, tourId);
        return statement.executeUpdateDelete();
    }

    public List<Long> saveTourStops(SQLiteDatabase database, Tour tour) {
        String sql = String.format(
                "insert into %s(%s,%s) values(?,?)",
                TABLE_NAME, TOUR_ID, LANDMARK_ID
        );
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindLong(1, tour.id);
        List<Long> stopIds = new LinkedList<>();
        for (long landmarkId : tour.getTourStopIds()) {
            statement.bindLong(2, landmarkId);
            statement.executeInsert();
            stopIds.add(landmarkId);
        }
        return stopIds;
    }
}
