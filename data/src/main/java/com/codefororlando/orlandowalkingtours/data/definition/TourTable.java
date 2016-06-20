package com.codefororlando.orlandowalkingtours.data.definition;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.data.model.Tour;
import com.codefororlando.orlandowalkingtours.data.repository.LandmarkRepository;

import java.util.LinkedList;
import java.util.List;

public class TourTable implements SqliteDefinition {
    public static final String TABLE_NAME = "tour",
            ID = BaseColumns._ID,
            NAME = "name",
            LAST_MODIFY_MILLIS = "lastModifyMillis";

    @Override
    public String getCreateStatement() {
        return String.format(
                "create table if not exists %s (%s,%s,%s)",
                TABLE_NAME,
                String.format("%s integer primary key autoincrement", ID),
                String.format("%s text", NAME),
                String.format("%s integer default (%s)", LAST_MODIFY_MILLIS, NOW_MILLIS)
        );
    }

    @Override
    public void onUpdate(SQLiteDatabase database, int oldVersion, int newVersion) {
    }

    private final String selectTourIdName =
            String.format("select %s, %s from %s", ID, NAME, TABLE_NAME);

    // Does not load landmarks
    @TargetApi(19)
    public List<Tour> get(SQLiteDatabase database) {
        String sql = String.format("%s order by %s desc", selectTourIdName, LAST_MODIFY_MILLIS);
        try (Cursor cursor = database.rawQuery(sql, null)) {
            List<Tour> tours = new LinkedList<>();
            while (cursor.moveToNext()) {
                tours.add(toTour(cursor));
            }
            return tours;
        }
    }

    private Tour toTour(Cursor cursor) {
        return new Tour(
                cursor.getLong(0),
                cursor.getString(1)
        );
    }

    @TargetApi(19)
    public Tour get(SQLiteDatabase database,
                    long tourId,
                    TourLandmarkTable tourLandmarkTable,
                    LandmarkRepository landmarkRepository) {
        Tour tour = null;

        // Performing multiple queries in a transaction is much quicker than outside a transaction
        database.beginTransaction();

        try {
            String sql = String.format("%s where %s=?", selectTourIdName, ID);
            try (Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(tourId)})) {
                if (cursor.moveToNext()) {
                    tour = toTour(cursor);
                }
            }

            if (tour != null) {
                List<HistoricLandmark> stops =
                        tourLandmarkTable.getLandmarks(database, tour.id, landmarkRepository);
                tour.setTourStops(stops);
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        return tour;
    }

    public Tour save(SQLiteDatabase database, Tour tour) {
        // TODO Set name and add landmarks
        try {
            database.beginTransaction();

            // Existing
            if (tour.id > 0) {
                return updateTourTransact(database, tour);
            }

            // New
            return saveNewTourTransact(database, tour);

        } finally {
            database.endTransaction();
        }
    }

    private Tour updateTourTransact(SQLiteDatabase database, final Tour tour) {
        String updateSql = String.format(
                "update %s set %s=? where %s=?",
                TABLE_NAME,
                NAME,
                ID
        );
        SQLiteStatement statement = database.compileStatement(updateSql);
        statement.bindString(1, tour.name);
        statement.bindLong(2, tour.id);
        statement.executeUpdateDelete();

        database.setTransactionSuccessful();

        return tour;
    }

    private Tour saveNewTourTransact(SQLiteDatabase database, final Tour tour) {
        String saveSql = String.format(
                "insert into %s(%s) values(?)",
                TABLE_NAME,
                NAME
        );
        SQLiteStatement statement = database.compileStatement(saveSql);
        statement.bindString(1, tour.name);
        long id = statement.executeInsert();

        database.setTransactionSuccessful();

        return new Tour(id, tour);
    }
}
