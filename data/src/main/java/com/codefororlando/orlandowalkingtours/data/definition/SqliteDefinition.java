package com.codefororlando.orlandowalkingtours.data.definition;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public interface SqliteDefinition extends BaseColumns {
    String NOW_MILLIS = "(julianday('now') - 2440587.5) * 86400000";

    String getCreateStatement();

    void onUpdate(SQLiteDatabase database, int oldVersion, int newVersion);

}
