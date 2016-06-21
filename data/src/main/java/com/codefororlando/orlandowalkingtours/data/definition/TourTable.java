package com.codefororlando.orlandowalkingtours.data.definition;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.codefororlando.orlandowalkingtours.data.model.Tour;

import java.util.LinkedList;
import java.util.List;

public class TourTable extends AutoIncrementIdTable {
    public static final String TABLE_NAME = "tour",
            NAME = "name",
            LAST_MODIFY_MILLIS = "lastModifyMillis";

    @Override
    public String getCreateStatement() {
        return String.format(
                "create table if not exists %s (%s,%s,%s)",
                TABLE_NAME,
                AUTO_INCREMENT_ID_COLUMN,
                String.format("%s text", NAME),
                String.format("%s integer default (%s)", LAST_MODIFY_MILLIS, NOW_MILLIS)
        );
    }

    @Override
    public void onUpdate(SQLiteDatabase database, int oldVersion, int newVersion) {
    }

    private final String selectTourIdName =
            String.format("select %s, %s from %s", _ID, NAME, TABLE_NAME);

    @TargetApi(19)
    public List<Tour> get(SQLiteDatabase database, TourLandmarkTable xrTable) {
        String sql = String.format("%s order by %s desc", selectTourIdName, LAST_MODIFY_MILLIS);
        // Wrap multiple queries in transaction for performance
        database.beginTransaction();
        try {
            try (Cursor cursor = database.rawQuery(sql, null)) {
                List<Tour> tours = new LinkedList<>();
                while (cursor.moveToNext()) {
                    Tour tour = toTour(cursor);
                    tour.setTourStops(xrTable.getLandmarkIds(database, tour.id));
                    tours.add(tour);
                }
                database.setTransactionSuccessful();
                return tours;
            }
        } finally {
            database.endTransaction();
        }
    }

    private Tour toTour(Cursor cursor) {
        return new Tour(
                cursor.getLong(0),
                cursor.getString(1)
        );
    }

    @TargetApi(19)
    public Tour get(SQLiteDatabase database, long tourId, TourLandmarkTable tourLandmarkTable) {
        Tour tour = null;

        // Performing multiple queries in a transaction is much quicker than outside a transaction
        database.beginTransaction();

        try {
            String sql = String.format("%s where %s=?", selectTourIdName, _ID);
            try (Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(tourId)})) {
                if (cursor.moveToNext()) {
                    tour = toTour(cursor);
                }
            }

            if (tour != null) {
                List<Long> stopIds = tourLandmarkTable.getLandmarkIds(database, tour.id);
                tour.setTourStops(stopIds);
            }

            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }

        return tour;
    }

    public Tour save(SQLiteDatabase database, Tour tour, TourLandmarkTable xrTable) {
        try {
            database.beginTransaction();

            // Existing
            if (tour.id > 0) {
                return updateTourTransact(database, tour, xrTable);
            }

            // New
            return saveNewTourTransact(database, tour, xrTable);

        } finally {
            database.endTransaction();
        }
    }

    private Tour updateTourTransact(SQLiteDatabase database,
                                    Tour tour,
                                    TourLandmarkTable xrTable) {
        String updateSql = String.format(
                "update %s set %s=? where %s=?",
                TABLE_NAME,
                NAME,
                _ID
        );
        SQLiteStatement statement = database.compileStatement(updateSql);
        statement.bindString(1, tour.name);
        statement.bindLong(2, tour.id);
        int updateCount = statement.executeUpdateDelete();

        xrTable.deleteTourStops(database, tour.id);
        xrTable.saveTourStops(database, tour);

        database.setTransactionSuccessful();

        return tour;
    }

    private Tour saveNewTourTransact(SQLiteDatabase database,
                                     Tour tour,
                                     TourLandmarkTable xrTable) {
        String saveSql = String.format(
                "insert into %s(%s) values(?)",
                TABLE_NAME,
                NAME
        );
        SQLiteStatement statement = database.compileStatement(saveSql);
        statement.bindString(1, tour.name);
        long id = statement.executeInsert();

        tour = new Tour(id, tour);
        List<Long> stopIds = xrTable.saveTourStops(database, tour);
        tour.setTourStops(stopIds);

        database.setTransactionSuccessful();

        return tour;
    }

    public long delete(SQLiteDatabase database, long tourId) {
        String deleteSql = String.format("delete from %s where %s=?", TABLE_NAME, _ID);
        SQLiteStatement statement = database.compileStatement(deleteSql);
        statement.bindLong(1, tourId);
        long deleteCount = statement.executeUpdateDelete();
        return deleteCount > 0 ? tourId : 0;
    }
}
