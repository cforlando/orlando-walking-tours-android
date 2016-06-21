package com.codefororlando.orlandowalkingtours.data.definition;

abstract public class AutoIncrementIdTable implements SqliteDefinition {
    protected static final String AUTO_INCREMENT_ID_COLUMN =
            String.format("%s integer primary key autoincrement", _ID);
}
