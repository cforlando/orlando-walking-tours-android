package com.codefororlando.orlandowalkingtours.data.definition;

import android.database.sqlite.SQLiteDatabase;

public interface SqliteDefinition {
    String NOW_MILLIS = "(julianday('now') - 2440587.5) * 86400000";

    String getCreateStatement();

    void onUpdate(SQLiteDatabase database, int oldVersion, int newVersion);

}
