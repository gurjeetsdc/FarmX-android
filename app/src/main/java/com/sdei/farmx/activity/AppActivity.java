package com.sdei.farmx.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.sdei.farmx.R;
import com.sdei.farmx.helper.ConfigurationWrapper;
import com.sdei.farmx.helper.preferences.MyPreference;
import com.sdei.farmx.helper.preferences.PreferenceConstant;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.Locale;

abstract class AppActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * This is where we do all of our normal static set up: create views,
     * bind data to lists, etc.
     * @param savedInstanceState containing the activity's previously frozen state, if there was one.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    /**
     * Method to open an activity
     * @param activity current class reference
     * @param mainActivityClass opening activity class reference
     */
    public void openActivity(Activity activity,
                             Class<? extends AppActivity> mainActivityClass) {

        Intent intent = new Intent(activity, mainActivityClass);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    /**
     * To check the network availability
     * @param context reference to the Activity class
     * @param showAlert flag to show the Alert if there is any network problem
     * @param action to perform on Alert button click
     * @return true if network is available
     */
    public boolean isNetworkAvailable(Context context,
                                      boolean showAlert,
                                      int action) {
        return AppUtils.isNetworkAvailable(context, showAlert, action);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        String language = getAppLanguage(newBase);
        Locale locale = new Locale(language);
        super.attachBaseContext(ConfigurationWrapper.wrapLocale(newBase, locale));
    }

    /**
     * To get the current language of the app
     * @param context reference to the Activity class
     * @return current language of the app
     */
    public String getAppLanguage(Context context){
        return MyPreference.getString(context, PreferenceConstant.APP_LANGUAGE);
    }

}
