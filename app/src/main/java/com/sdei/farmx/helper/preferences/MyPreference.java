package com.sdei.farmx.helper.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This class is used to save the user data locally.
 */
public class MyPreference {

    private static SharedPreferences myPref;
    private static SharedPreferences.Editor editor;

    /**
     * Save user string data in the preferences
     */
    public static void saveString(Context context, String key, String value) {

        if (value != null) {
            myPref = context.getSharedPreferences(PreferenceConstant.APP_PREFERENCE, 0);
            editor = myPref.edit();
            editor.putString(key, value);
            editor.apply();
        }

    }

    /**
     * Save user boolean data in the preferences
     */
    public static void saveBoolean(Context context, String key, boolean value) {

        try {

            myPref = context.getSharedPreferences(PreferenceConstant.APP_PREFERENCE, 0);
            editor = myPref.edit();
            editor.putBoolean(key, value);
            editor.apply();

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Use to retrieve user boolean data from the preferences
     */
    public static boolean getBoolean(Context context, String key) {

        boolean status = false;

        try {

            myPref = context.getSharedPreferences(PreferenceConstant.APP_PREFERENCE, 0);
            status = myPref.getBoolean(key, false);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;

    }

    /**
     * Use to retrieve user string data from the preferences
     */
    public static String getString(Context context, String key) {
        try {

            myPref = context.getSharedPreferences(PreferenceConstant.APP_PREFERENCE, 0);
            return myPref.getString(key, "");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    /**
     * Use to retrieve user string data from the preferences
     */
    public static int getInteger(Context context, String key) {
        myPref = context.getSharedPreferences(PreferenceConstant.APP_PREFERENCE, 0);
        return myPref.getInt(key, 0);
    }

    /**
     * Save user string data in the preferences
     */
    public static void saveInteger(Context context, String key, int value) {
        myPref = context.getSharedPreferences(PreferenceConstant.APP_PREFERENCE, 0);
        editor = myPref.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Use to clear the all preference data
     */
    public static void clearPreferenceData(Context context) {
        myPref = context.getSharedPreferences(PreferenceConstant.APP_PREFERENCE, 0);
        editor = myPref.edit();
        editor.clear();
        editor.apply();
    }

}
