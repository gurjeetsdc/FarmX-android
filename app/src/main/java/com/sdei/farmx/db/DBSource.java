package com.sdei.farmx.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sdei.farmx.dataobject.State;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.helper.utils.AppUtils;

import java.io.IOException;
import java.util.ArrayList;

public class DBSource {

    private final Context context;

    private static DBSource sInstance;

    // Database fields
    private SQLiteDatabase database;
    private final DatabaseHelper dbHelper;

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DBSource(Context context) {
        this.context = context;
        dbHelper = DatabaseHelper.getInstance(context);
    }

    private void open() throws SQLException {

        if (database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }

    }

    public static DBSource getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new DBSource(context.getApplicationContext());
        }
        return sInstance;

    }

    private void createTable(String sqlQuery) {

        open();
        database.execSQL(sqlQuery);

    }

    private void dropTable(String tableName) {

        open();
        database.execSQL("DROP TABLE IF EXISTS " + tableName);

    }

    private void closeDb() {

        try {
            dbHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isTableExist(String tableName) {

        boolean status = false;

        Cursor cursor = null;

        try {

            cursor = database.rawQuery("select DISTINCT name from sqlite_master where type='table' AND name= '" + tableName + "'", null);

            if (cursor != null) {

                if (cursor.getCount() > 0) {

                    status = true;

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (cursor != null)
                cursor.close();

        }

        return status;

    }

    public void clearDatabase() {

        try {

            context.deleteDatabase(DatabaseHelper.DATABASE_NAME);
            closeDb();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String getJson(Object object) throws JsonProcessingException {
        ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return writer.writeValueAsString(object);
    }

    private void saveDataInTable(String tableName, byte[] data) {

        open();
        dropTable(tableName);
        createTable(DBConstants.query(tableName));

        ContentValues values = new ContentValues();
        values.put(DBConstants.OBJECT, data);
        database.insert(tableName, null, values);

    }

    public void saveUser(User user) {

        try {

            saveDataInTable(DBConstants.TABLE_USER, getJson(user).getBytes());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    private String retrieveDataFromTable(String tableName) {

        String json = null;

        if (isTableExist(tableName)) {

            Cursor cursor = null;

            try {

                cursor = database.rawQuery("select * from " + tableName, null);

                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    byte[] blob = cursor.getBlob(cursor.getColumnIndex(DBConstants.OBJECT));
                    json = new String(blob);
                }

                cursor.close();

            } catch (Exception e) {

                if (cursor != null) {
                    cursor.close();
                }

            }

        }

        return json;

    }

    public User getUser() {

        try {

            open();

            String json = retrieveDataFromTable(DBConstants.TABLE_USER);

            if (!TextUtils.isEmpty(json)) {

                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(json, User.class);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public void saveStates(ArrayList<State> list) {

        try {

            saveDataInTable(DBConstants.TABLE_STATE, getJson(list).getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<State> getStates() {

        ArrayList<State> dataList = new ArrayList<>();

        open();

        Cursor cursor = null;

        try {
            cursor = database.rawQuery("select * from " + DBConstants.TABLE_STATE, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                byte[] blob = cursor.getBlob(cursor.getColumnIndex(DBConstants.OBJECT));
                String json = new String(blob);
                dataList = AppUtils.getArrayListFromJson(json, State.class);
            }

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

            if (cursor != null) {
                cursor.close();
            }

        }

        return dataList;
    }

    public void saveUserFacebookData(User mUser) {

        try {

            saveDataInTable(DBConstants.TABLE_FACEBOOK_USER, getJson(mUser).getBytes());

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }

    public User getFacebookUser() {

        User object = null;
        open();
        String json = retrieveDataFromTable(DBConstants.TABLE_FACEBOOK_USER);

        if (!TextUtils.isEmpty(json)) {

            ObjectMapper mapper = new ObjectMapper();

            try {
                object = mapper.readValue(json, User.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return object;

    }

}
