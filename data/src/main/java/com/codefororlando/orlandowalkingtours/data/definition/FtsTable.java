package com.codefororlando.orlandowalkingtours.data.definition;

abstract public class FtsTable implements SqliteDefinition {
    abstract protected String getContentTableName();

    abstract protected String getTableName();

    abstract protected String[] getFtsColumnNames();

    public String[] getTriggers() {
        return FtsTrigger.getTriggers(getContentTableName(), getTableName(), getFtsColumnNames());
    }
}
