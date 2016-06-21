package com.codefororlando.orlandowalkingtours.data.definition;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.data.model.RemoteLandmark;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class LandmarkTable extends AutoIncrementIdTable {
    public static final String TABLE_NAME = "landmark",
            REMOTE_ID = "remoteId",
            NAME = "name",
            DESCRIPTION = "description",
            STREET_ADDRESS = "streetAddress",
            CITY = "city",
            STATE = "state",
            LATITUDE = "latitude",
            LONGITUDE = "longitude",
            ORIGINAL_JSON = "originalJson",
            THUMBNAIL_URL = "thumbnailUrl";

    @Override
    public String getCreateStatement() {
        return String.format(
                "create table if not exists %s (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)",
                TABLE_NAME,
                AUTO_INCREMENT_ID_COLUMN,
                String.format("%s integer unique", REMOTE_ID),
                String.format("%s text", NAME),
                String.format("%s text", DESCRIPTION),
                String.format("%s text", STREET_ADDRESS),
                String.format("%s text", CITY),
                String.format("%s text", STATE),
                String.format("%s double", LATITUDE),
                String.format("%s double", LONGITUDE),
                String.format("%s text", ORIGINAL_JSON),
                String.format("%s text", THUMBNAIL_URL)
        );
    }

    @Override
    public void onUpdate(SQLiteDatabase database, int oldVersion, int newVersion) {
    }

    @TargetApi(19)
    public List<HistoricLandmark> get(SQLiteDatabase sqLiteDatabase) {
        String sql = String.format(
                "select %s, %s, %s, %s, %s, %s from %s",
                _ID,
                NAME,
                DESCRIPTION,
                STREET_ADDRESS,
                LATITUDE,
                LONGITUDE,
                TABLE_NAME
        );
        try (Cursor cursor = sqLiteDatabase.rawQuery(sql, null)) {
            List<HistoricLandmark> landmarks = new ArrayList<>();
            while (cursor.moveToNext()) {
                landmarks.add(new HistoricLandmark(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getDouble(4),
                        cursor.getDouble(5)
                ));
            }
            return landmarks;
        }
    }

    @TargetApi(19)
    public List<HistoricLandmark> save(SQLiteDatabase database, List<RemoteLandmark> landmarks) {
        // TODO Insert or update since save signal could originate from force refresh
        String insertSql = String.format(
                "insert or ignore into %s(%s,%s,%s,%s,%s,%s,%s,%s,%s) values(?,?,?,?,?,?,?,?,?)",
                TABLE_NAME,
                REMOTE_ID,
                NAME,
                DESCRIPTION,
                STREET_ADDRESS,
                CITY,
                STATE,
                LATITUDE,
                LONGITUDE,
                ORIGINAL_JSON
        );

        database.beginTransaction();

        Gson gson = new Gson();
        try (SQLiteStatement sqLiteStatement = database.compileStatement(insertSql)) {
            List<HistoricLandmark> historicLandmarks = new ArrayList<>(landmarks.size());

            for (RemoteLandmark landmark : landmarks) {
                if (landmark.location == null) {
                    continue;
                }

                String name = landmark.name;
                String description = landmark.description;
                String streetAddress = landmark.streetAddress;
                double[] coordinates = landmark.location.coordinates;
                double latitude = coordinates[0];
                double longitude = coordinates[1];
                sqLiteStatement.clearBindings();
                sqLiteStatement.bindLong(1, landmark.id);
                sqLiteStatement.bindString(2, name);
                sqLiteStatement.bindString(3, description);
                sqLiteStatement.bindString(4, streetAddress);
                sqLiteStatement.bindString(5, landmark.city);
                sqLiteStatement.bindString(6, landmark.state);
                sqLiteStatement.bindDouble(7, latitude);
                sqLiteStatement.bindDouble(8, longitude);
                sqLiteStatement.bindString(9, gson.toJson(landmark));

                long localId = sqLiteStatement.executeInsert();
                if (localId > 0) {
                    HistoricLandmark historicLandmark = new HistoricLandmark(
                            localId, name, description, streetAddress, latitude, longitude
                    );
                    historicLandmarks.add(historicLandmark);
                }
            }

            database.setTransactionSuccessful();

            return historicLandmarks;
        } finally {
            database.endTransaction();
        }
    }
}
