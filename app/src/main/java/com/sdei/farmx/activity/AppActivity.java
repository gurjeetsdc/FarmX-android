package com.sdei.farmx.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.sdei.farmx.R;
import com.sdei.farmx.apimanager.ApiManager;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.callback.DialogCallback;
import com.sdei.farmx.dataobject.AppHeaderObject;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.Equipment;
import com.sdei.farmx.dataobject.NavigationHeader;
import com.sdei.farmx.fragment.CropAddFragment;
import com.sdei.farmx.fragment.CropDashboardFragment;
import com.sdei.farmx.fragment.CropMyCropsFragment;
import com.sdei.farmx.fragment.EquipmentAddFragment;
import com.sdei.farmx.fragment.EquipmentMyEquipmentsFragment;
import com.sdei.farmx.fragment.InputDashboardFragment;
import com.sdei.farmx.fragment.CropDetailFragment;
import com.sdei.farmx.fragment.CropFragment;
import com.sdei.farmx.fragment.CropMoreItemsFragment;
import com.sdei.farmx.fragment.EquipmentFragment;
import com.sdei.farmx.fragment.InputDetailFragment;
import com.sdei.farmx.fragment.HomeFragment;
import com.sdei.farmx.fragment.InputMoreItemsFragment;
import com.sdei.farmx.fragment.CropMyBidsFragment;
import com.sdei.farmx.helper.ConfigurationWrapper;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.Locale;

public abstract class AppActivity extends AppCompatActivity {

    public AppHeaderObject headerObject;
    public NavigationHeader navHeader;

    public String equipmentActionType;

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

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        View view = getCurrentFocus();

        if (view != null
                && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE)
                && view instanceof EditText
                && !view.getClass().getName().startsWith("android.webkit.")) {

            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];

            if (x < view.getLeft()
                    || x > view.getRight()
                    || y < view.getTop()
                    || y > view.getBottom()) {
                AppUtils.hideKeyboard(AppActivity.this);
            }

        }

        return super.dispatchTouchEvent(ev);

    }

    /**
     * Method to open an activity
     *
     * @param activity          current class reference
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
     *
     * @param context   reference to the Activity class
     * @param showAlert flag to show the Alert if there is any network problem
     * @param action    to perform on Alert button click
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
     *
     * @param context reference to the Activity class
     * @return current language of the app
     */
    public String getAppLanguage(Context context) {
        return AppUtils.getAppLanguage(context);
    }

    /**
     * Finishes the activity with custom transition
     *
     * @param activity instance to finish
     */
    public void finishActivity(Activity activity) {
        activity.finish();
        overridePendingTransition(0, 0);
    }

    public boolean isUserLoggedIn() {
        return AppUtils.isUserLoggedIn(AppActivity.this);
    }

    public void initUserObject() {

        AppUtils.initUserObject(AppActivity.this);

    }

    /**
     * Change the app header depending upon current fragment instance.
     *
     * @param fragment instance to change the App header accordingly
     */
    public void changeAppHeader(Fragment fragment, String title) {

        if (fragment instanceof HomeFragment) {

            headerObject.setBackgroundDrawable(R.drawable.dashboard_amber_gradient);
            headerObject.setBackgroundColor(0);
            headerObject.setHasCenteredImage(true);
            headerObject.setShowSearch(true);
            headerObject.setShowMarquee(true);

            AppUtils.setStatusBarTint(this, R.color.amber);
            navHeader.setBackgroundColor(R.color.amber);

        } else {

            int color = 0;

            if (fragment instanceof CropFragment) {

                color = R.color.crop_green;

            } else if (fragment instanceof InputDashboardFragment
                    || fragment instanceof InputMoreItemsFragment
                    || fragment instanceof InputDetailFragment) {

                color = R.color.input;

            } else if (fragment instanceof EquipmentFragment) {

                color = R.color.equipment;

            }

            headerObject.setShowMarquee(fragment instanceof CropDetailFragment);
            headerObject.setBackgroundDrawable(0);
            headerObject.setBackgroundColor(color);
            headerObject.setShowSearch(false);
            headerObject.setHasCenteredImage(false);
            headerObject.setTitle(title);

            AppUtils.setStatusBarTint(this, color);
            navHeader.setBackgroundColor(color);

        }

    }

    public String getTitleName(Fragment fragment) {

        if (fragment instanceof CropAddFragment) {
            return getString(R.string.add_crop);
        } else if (fragment instanceof CropDashboardFragment
                || fragment instanceof CropMoreItemsFragment) {
            return getString(R.string.crops);
        } else if (fragment instanceof CropMyCropsFragment) {
            return getString(R.string.my_crops);
        } else if (fragment instanceof InputDashboardFragment) {
            return getString(R.string.farm_inputs);
        } else if (fragment instanceof CropMyBidsFragment) {
            return getString(R.string.my_bids);
        }

        return null;

    }

    public void finishFragment() {
        AppUtils.finishFragment(getSupportFragmentManager(),
                AppConstants.FragmentConstant.fragment);
        changeAppHeader(AppConstants.FragmentConstant.fragment,
                getTitleName(AppConstants.FragmentConstant.fragment));
    }

    public void openLoginActivity(Context context,
                                  String action,
                                  int pendingTask) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("action", action);
        startActivityForResult(intent, pendingTask);
    }

    public void openFragment(Fragment fragment) {

        AppUtils.openFragmentWithoutPopBack(getSupportFragmentManager(), fragment);

    }

    public void openAddCropPage(final Context context,
                                boolean editCrop,
                                Crop object) {

        if (isUserLoggedIn()) {

            CropAddFragment fragment = new CropAddFragment();

            Bundle bundle = new Bundle();
            bundle.putBoolean("editCrop", editCrop);

            if (editCrop)
                bundle.putSerializable("object", object);

            fragment.setArguments(bundle);
            openFragment(fragment);
            changeAppHeader(fragment, getString(R.string.add_crop));

        } else if (!editCrop) {

            showLoginToContinueAlert(context, AppConstants.PendingTask.ADD_CROP);

        }

    }

    public void openAddEquipmentFragment(final Context context,
                                String actionType,
                                Equipment object) {

        if (isUserLoggedIn()) {

            EquipmentAddFragment fragment = new EquipmentAddFragment();

            Bundle bundle = new Bundle();
            bundle.putString("actionType", actionType);

            if (actionType.equals("edit"))
                bundle.putSerializable("object", object);

            fragment.setArguments(bundle);
            openFragment(fragment);
            changeAppHeader(fragment, getString(R.string.add_equipment));

        } else {

            equipmentActionType = actionType;
            showLoginToContinueAlert(context, AppConstants.PendingTask.ADD_EQUIPMENT);

        }

    }

    public void showLoginToContinueAlert(final Context context, final int action) {

        AppUtils.openPNAlertDialog(
                context,
                getString(R.string.alert_login_to_continue),
                getString(R.string.ok),
                new DialogCallback() {
                    @Override
                    public void onSuccess() {

                        openLoginActivity(context, "login", action);

                    }

                    @Override
                    public void onFailure() {

                    }
                });

    }

    public void openMyCropsPage(final Context context) {

        if (isUserLoggedIn()) {

            openFragment(new CropMyCropsFragment());

        } else {

            showLoginToContinueAlert(context, AppConstants.PendingTask.MY_CROPS);

        }

    }

    public void openMyEquipmentsFragment(final Context context) {

        if (isUserLoggedIn()) {

            openFragment(new EquipmentMyEquipmentsFragment());

        } else {

            showLoginToContinueAlert(context, AppConstants.PendingTask.MY_EQUIPMENTS);

        }

    }

    public void getStateListing(Context context, ApiServiceCallback callback) {

        ApiManager.callService(
                context,
                AppConstants.API_INDEX.STATE,
                callback,
                true,
                null,
                null);

    }

    public boolean showErrorToast(Context context,
                                  final Fragment fragment,
                                  String errorMessage) {

        if (!TextUtils.isEmpty(errorMessage)
                && errorMessage.contains("Failed to connect to")) {

            AppUtils.openPAlertDialogWithTitle(
                    context,
                    getString(R.string.warning),
                    getString(R.string.unable_to_connect),
                    getString(R.string.ok),
                    new DialogCallback() {
                        @Override
                        public void onSuccess() {

                            if (fragment != null) {
                                AppUtils.finishFragment(getSupportFragmentManager(), fragment);
                                changeAppHeader(AppConstants.FragmentConstant.fragment,
                                        getTitleName(AppConstants.FragmentConstant.fragment));
                            }

                        }

                        @Override
                        public void onFailure() {

                        }
                    });

            return false;

        }

        return true;

    }

}
