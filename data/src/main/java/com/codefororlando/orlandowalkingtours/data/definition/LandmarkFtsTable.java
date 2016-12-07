package com.codefororlando.orlandowalkingtours.data.definition;

import android.database.sqlite.SQLiteDatabase;

public class LandmarkFtsTable extends FtsTable {
    public static final String TABLE_NAME = "landmarkFts";

    @Override
    public String getCreateStatement() {
        return String.format(
                "CREATE VIRTUAL TABLE %s USING fts4(content='%s', %s, %s, %s, %s);",
                TABLE_NAME,
                LandmarkTable.TABLE_NAME,
                LandmarkTable.NAME,
                LandmarkTable.TYPE,
                LandmarkTable.DESCRIPTION,
                LandmarkTable.STREET_ADDRESS
        );
    }

    @Override
    protected String getContentTableName() {
        return LandmarkTable.TABLE_NAME;
    }

    @Override
    protected String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected String[] getFtsColumnNames() {
        return new String[]{
                LandmarkTable.NAME,
                LandmarkTable.TYPE,
                LandmarkTable.DESCRIPTION,
                LandmarkTable.STREET_ADDRESS
        };
    }

    @Override
    public void onUpdate(SQLiteDatabase database, int oldVersion, int newVersion) {
    }
}
