package com.sdei.farmx.db;

public class DBConstants {

    private static final String COLUMN_ID = "_id";
    final static String OBJECT = "object";

    static final String TABLE_USER = "user";
    static final String TABLE_STATE = "states";
    static final String TABLE_FACEBOOK_USER = "facebook_user";

    static String query(String tableName) {

        return "create table if not exists "
                + tableName + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + OBJECT + " blob);";

    }

}
