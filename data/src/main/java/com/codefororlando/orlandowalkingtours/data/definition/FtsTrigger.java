package com.codefororlando.orlandowalkingtours.data.definition;

public class FtsTrigger {
    // https://www.sqlite.org/fts3.html#section_6_2_2
    public static String[] getTriggers(String contentTableName, String ftsTableName, String... ftsColumns) {
        String deleteFtsDoc = String.format("DELETE FROM %s WHERE docid = old.rowid;", ftsTableName);

        String newDot = "new.",
                separator = ",";
        StringBuilder columnsSb = new StringBuilder(),
                newDotColumnsSb = new StringBuilder();
        for (String s : ftsColumns) {
            if (columnsSb.length() > 0) {
                columnsSb.append(separator);
                newDotColumnsSb.append(separator);
            }
            columnsSb.append(s);
            newDotColumnsSb.append(String.format("%s%s", newDot, s));
        }
        String insertFtsDoc = String.format(
                "INSERT INTO %s(docid, %s) VALUES(new.rowid, %s);",
                ftsTableName, columnsSb.toString(), newDotColumnsSb.toString()
        );

        return new String[]{
                String.format(
                        "CREATE TRIGGER %1$s_bu BEFORE UPDATE ON %1$s BEGIN %2$s END;",
                        contentTableName, deleteFtsDoc
                ),
                String.format(
                        "CREATE TRIGGER %1$s_bd BEFORE DELETE ON %1$s BEGIN %2$s END;",
                        contentTableName, deleteFtsDoc
                ),
                String.format(
                        "CREATE TRIGGER %1$s_au AFTER UPDATE ON %1$s BEGIN %2$s END;",
                        contentTableName, insertFtsDoc
                ),
                String.format(
                        "CREATE TRIGGER %1$s_ai AFTER INSERT ON %1$s BEGIN %2$s END;",
                        contentTableName, insertFtsDoc
                )
        };
    }
}
