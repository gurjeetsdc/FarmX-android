package com.sdei.farmx.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.sdei.farmx.R;
import com.sdei.farmx.activity.MainActivity;
import com.sdei.farmx.apimanager.ApiManager;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.callback.DialogCallback;
import com.sdei.farmx.callback.NetworkErrorCallback;
import com.sdei.farmx.dataobject.AddToCart;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.Equipment;
import com.sdei.farmx.helper.AppInstance;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

abstract class AppFragment extends Fragment {

    public Context activityContext;

    public void openFragment(Fragment fragment) {

        AppUtils.openFragmentWithoutPopBack(getFragmentManager(), fragment);

    }

    public void changeAppHeader(Fragment fragment, String title) {

        if (activityContext != null && activityContext instanceof MainActivity) {
            MainActivity activity = (MainActivity) activityContext;
            activity.changeAppHeader(fragment, title);
        }

    }

    public void showLoginAlertMessage(final int pendingTask) {

        MainActivity activity = (MainActivity) activityContext;
        activity.showLoginToContinueAlert(activityContext, pendingTask);

    }

    public void getStateListing(ApiServiceCallback callback) {

        MainActivity activity = (MainActivity) activityContext;
        activity.getStateListing(activityContext, callback);

    }

    public void getCategoryListing(String type, Fragment fragment) {

        if (AppUtils.isNetworkAvailable(activityContext, (NetworkErrorCallback) fragment)) {

            ApiManager.callService(activityContext,
                    AppConstants.API_INDEX.CATEGORIES,
                    (ApiServiceCallback) fragment,
                    true,
                    type,
                    getUserAccessToken());

        }

    }

    public void checkPermissions(Context context,
                                 int action,
                                 String... permissions) {

        MainActivity activity = (MainActivity) context;
        activity.checkPermission(action, permissions);

    }

    public void openCamera() {

        MainActivity activity = (MainActivity) activityContext;
        activity.openCamera();

    }

    public boolean isUserLoggedIn() {
        return AppUtils.isUserLoggedIn(activityContext);
    }

    public String getUserAccessToken() {

        if (AppInstance.appUser != null) {
            return AppInstance.appUser.getAccess_token();
        } else {
            return null;
        }

    }

    public void finishFragment() {

        MainActivity activity = (MainActivity) activityContext;
        activity.finishFragment();

    }

    public boolean showErrorToast(final Fragment fragment, String errorMessage) {

        MainActivity activity = (MainActivity) activityContext;
        return activity.showErrorToast(activityContext, fragment, errorMessage);

    }

    public void openAddCropPage(boolean editCrop, Crop object) {

        MainActivity activity = (MainActivity) activityContext;
        activity.openAddCropPage(activity, editCrop, object);

    }

    public void openAddEquipmentFragment(String actionType, Equipment object) {

        MainActivity activity = (MainActivity) activityContext;
        activity.openAddEquipmentFragment(activity, actionType, object);

    }

    public boolean isNetworkAvailable() {
        return AppUtils.isNetworkAvailable(activityContext, true, 101);
    }

    public void addItemToCart(AddToCart object,
                              ApiServiceCallback callback) {

        if (isNetworkAvailable()) {

            ApiManager.callService(
                    activityContext,
                    AppConstants.API_INDEX.ADD_TO_CART,
                    callback,
                    true,
                    object,
                    getUserAccessToken());

        }

    }

}
