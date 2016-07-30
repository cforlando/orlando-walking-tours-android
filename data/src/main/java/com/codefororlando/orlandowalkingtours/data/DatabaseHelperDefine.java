package com.codefororlando.orlandowalkingtours.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.codefororlando.orlandowalkingtours.data.definition.LandmarkFtsTable;
import com.codefororlando.orlandowalkingtours.data.definition.LandmarkTable;
import com.codefororlando.orlandowalkingtours.data.definition.SqliteDefinition;
import com.codefororlando.orlandowalkingtours.data.definition.TourLandmarkTable;
import com.codefororlando.orlandowalkingtours.data.definition.TourTable;
import com.codefororlando.orlandowalkingtours.log.Logger;

public class DatabaseHelperDefine extends SQLiteOpenHelper {
    protected static final String NAME = "walkingTour.sqlite";
    private static final int VERSION = 2;

    protected final LandmarkTable landmarkTable = new LandmarkTable();
    protected final LandmarkFtsTable landmarkFtsTable = new LandmarkFtsTable();
    protected final TourTable tourTable = new TourTable();
    protected final TourLandmarkTable tourLandmarkTable = new TourLandmarkTable();

    private final SqliteDefinition[] definitions = new SqliteDefinition[]{
            landmarkTable,
            landmarkFtsTable,
            tourTable,
            tourLandmarkTable
    };

    protected final Logger logger;

    protected DatabaseHelperDefine(@NonNull Context context, @NonNull Logger logger) {
        super(context, NAME, null, VERSION);
        this.logger = logger;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        logger.debug("Database definition");

        for (SqliteDefinition definition : definitions) {
            String definitionStatement = definition.getCreateStatement();
            logger.debug(definitionStatement);
            sqLiteDatabase.execSQL(definitionStatement);
        }

        for (String triggerStatement : landmarkFtsTable.getTriggers()) {
            logger.debug(triggerStatement);
            sqLiteDatabase.execSQL(triggerStatement);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        for (SqliteDefinition definition : definitions) {
            definition.onUpdate(sqLiteDatabase, oldVersion, newVersion);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        // Enable foreign key constraints
        db.execSQL("PRAGMA foreign_keys=ON;");
    }
}
