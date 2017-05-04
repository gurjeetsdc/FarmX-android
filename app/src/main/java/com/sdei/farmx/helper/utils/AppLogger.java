package com.sdei.farmx.helper.utils;

import android.support.compat.BuildConfig;
import android.util.Log;

public class AppLogger {

    public static void log(String screen, String message) {

        if (BuildConfig.DEBUG) {
            Log.e(screen, message);
        }

    }

}
