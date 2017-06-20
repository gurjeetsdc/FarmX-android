package com.sdei.farmx.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.sdei.farmx.R;
import com.sdei.farmx.apimanager.ApiManager;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.databinding.ActivitySplashBinding;
import com.sdei.farmx.dataobject.SingleSelectionItem;
import com.sdei.farmx.dialog.SingleSelectionDialog;
import com.sdei.farmx.helper.preferences.MyPreference;
import com.sdei.farmx.helper.preferences.PreferenceConstant;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppLogger;
import com.sdei.farmx.helper.utils.AppUtils;

import io.fabric.sdk.android.Fabric;

import java.security.MessageDigest;
import java.util.ArrayList;

/**
 * This is the loading screen for the app. This screen will open up for 3 seconds
 * and after that user will redirect to Home screen of the app.
 */
public class SplashActivity extends AppActivity implements ApiServiceCallback {

    private SingleSelectionDialog langDialog = null;
    private boolean selectLang = false;

    /**
     * Called when the activity is first created.
     * This is where we do all of our normal static set up: create views,
     * bind data to lists, etc.
     *
     * @param savedInstanceState containing the activity's previously frozen state, if there was one.
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        ActivitySplashBinding binding
                = DataBindingUtil.setContentView(SplashActivity.this, R.layout.activity_splash);
        selectLang = TextUtils.isEmpty(getAppLanguage(SplashActivity.this));
        binding.setButtonVisibility(getAppLanguage(SplashActivity.this));
        printKeyHash();
        AppUtils.setStatusBarTint(this, R.color.amber);

        if(!selectLang) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openActivity(SplashActivity.this, MainActivity.class);
                    finishActivity(SplashActivity.this);
                }
            }, 2000);

        }
//        else if(isNetworkAvailable(SplashActivity.this, true, 101)){
//
//            ApiManager.callService(
//                    SplashActivity.this,
//                    AppConstants.API_INDEX.LANGUAGE,
//                    SplashActivity.this,
//                    false,
//                    null);
//
//        }

    }

    /**
     * Handle click listeners for the screen.
     *
     * @param view clicked on and perform action accordingly by using view id.
     */
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.select_lang_btn:

                openLanguagesDialog();

                break;

        }

    }

    /**
     * Open up the languages dialog to select the language
     */
    private void openLanguagesDialog() {

        final ArrayList<SingleSelectionItem> languages = new ArrayList<>();
        String lang[] = getResources().getStringArray(R.array.languages);
        String langKeys[] = getResources().getStringArray(R.array.language_keys);

        for (int i = 0; i < lang.length; i++) {

            SingleSelectionItem object = AppUtils.getSingleSelectionItem(i,
                    langKeys[i], lang[i], 0);
            languages.add(object);

        }

        langDialog = new SingleSelectionDialog(SplashActivity.this,
                languages,
                new RecyclerCallback() {
                    @Override
                    public void onItemClick(int position) {

                        MyPreference.saveString(SplashActivity.this,
                                PreferenceConstant.APP_LANGUAGE,
                                languages.get(position).getKey());
                        langDialog.dismiss();
                        langDialog = null;

                        openActivity(SplashActivity.this, MainActivity.class);
                        finishActivity(SplashActivity.this);

                    }

                    @Override
                    public void onChildItemClick(int parentIndex, int childIndex) {

                    }
                });

        langDialog.show();

    }

    public void printKeyHash() {

        PackageInfo packageInfo;

        try {

            //Retrieve package info
            packageInfo = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : packageInfo.signatures) {

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String key = new String(Base64.encode(md.digest(), 0));

                AppLogger.log("SplashActivity", "Key Hash= " + key);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

//        AppLogger.log("SplashActivity", response.getData().toString());

    }

    @Override
    public void onException(int apiIndex, Throwable t) {

    }

    @Override
    public void onError(int apiIndex, String message) {

    }

}
