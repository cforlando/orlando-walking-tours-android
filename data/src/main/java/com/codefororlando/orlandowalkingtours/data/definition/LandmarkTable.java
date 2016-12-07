package com.codefororlando.orlandowalkingtours.data.definition;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.codefororlando.orlandowalkingtours.data.model.HistoricLandmark;
import com.codefororlando.orlandowalkingtours.data.model.RemoteLandmark;

import java.util.ArrayList;
import java.util.List;

public class LandmarkTable extends AutoIncrementIdTable {
    public static final String TABLE_NAME = "landmark",
            REMOTE_ID = "remoteId",
            NAME = "name",
            TYPE = "type",
            DESCRIPTION = "description",
            STREET_ADDRESS = "streetAddress",
            LATITUDE = "latitude",
            LONGITUDE = "longitude",
            THUMBNAIL_URL = "thumbnailUrl";

    @Override
    public String getCreateStatement() {
        return String.format(
                "create table if not exists %s (%s,%s,%s,%s,%s,%s,%s,%s,%s)",
                TABLE_NAME,
                AUTO_INCREMENT_ID_COLUMN,
                String.format("%s text unique", REMOTE_ID),
                String.format("%s text", NAME),
                String.format("%s text", TYPE),
                String.format("%s text", DESCRIPTION),
                String.format("%s text", STREET_ADDRESS),
                String.format("%s double", LATITUDE),
                String.format("%s double", LONGITUDE),
                String.format("%s text", THUMBNAIL_URL)
        );
    }

    @Override
    public void onUpdate(SQLiteDatabase database, int oldVersion, int newVersion) {
        // TODO Alter schema properly as necessary
    }

    @TargetApi(19)
    public List<HistoricLandmark> get(SQLiteDatabase sqLiteDatabase, String query) {
        String sql = String.format(
                "select %s, c.%s, c.%s, c.%s, c.%s, %s, %s from %s c",
                _ID,
                NAME,
                TYPE,
                DESCRIPTION,
                STREET_ADDRESS,
                LATITUDE,
                LONGITUDE,
                TABLE_NAME
        );

        String[] selectionArgs = null;

        if (!TextUtils.isEmpty(query)) {
            // TODO Match only columns displayed (name and address)
            sql += String.format(
                    " inner join %1$s f on c.%2$s=f.docid where %1$s match ?",
                    LandmarkFtsTable.TABLE_NAME, _ID);
            selectionArgs = new String[]{query};
        }

        try (Cursor cursor = sqLiteDatabase.rawQuery(sql, selectionArgs)) {
            List<HistoricLandmark> landmarks = new ArrayList<>();
            while (cursor.moveToNext()) {
                landmarks.add(new HistoricLandmark(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getDouble(5),
                        cursor.getDouble(6)
                ));
            }
            return landmarks;
        }
    }

    @TargetApi(19)
    public List<HistoricLandmark> save(SQLiteDatabase database, List<RemoteLandmark> landmarks) {
        // TODO Insert or update since save signal could originate from force refresh
        String insertSql = String.format(
                "insert or ignore into %s(%s,%s,%s,%s,%s,%s,%s) values(?,?,?,?,?,?,?)",
                TABLE_NAME,
                REMOTE_ID,
                NAME,
                TYPE,
                DESCRIPTION,
                STREET_ADDRESS,
                LATITUDE,
                LONGITUDE
        );

        database.beginTransaction();

        try (SQLiteStatement sqLiteStatement = database.compileStatement(insertSql)) {
            List<HistoricLandmark> historicLandmarks = new ArrayList<>(landmarks.size());

            for (RemoteLandmark landmark : landmarks) {
                if (landmark.location == null) {
                    continue;
                }

                String name = landmark.name;
                String type = landmark.type;
                String description = landmark.description;
                String streetAddress = landmark.streetAddress;
                double latitude = landmark.location.latitude;
                double longitude = landmark.location.longitude;
                sqLiteStatement.clearBindings();
                sqLiteStatement.bindString(1, landmark.id);
                sqLiteStatement.bindString(2, name);
                sqLiteStatement.bindString(3, type);
                sqLiteStatement.bindString(4, description);
                sqLiteStatement.bindString(5, streetAddress);
                sqLiteStatement.bindDouble(6, latitude);
                sqLiteStatement.bindDouble(7, longitude);

                long localId = sqLiteStatement.executeInsert();
                if (localId > 0) {
                    HistoricLandmark historicLandmark = new HistoricLandmark(
                            localId, name, type, description, streetAddress, latitude, longitude
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
