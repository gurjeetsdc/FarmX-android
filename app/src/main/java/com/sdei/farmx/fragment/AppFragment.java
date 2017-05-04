package com.sdei.farmx.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.sdei.farmx.activity.LoginActivity;
import com.sdei.farmx.activity.MainActivity;
import com.sdei.farmx.helper.utils.AppUtils;

abstract class AppFragment extends Fragment {

    public Context activityContext;

    public void openFragment(Fragment fragment) {

        AppUtils.openFragmentWithoutPopBack(getFragmentManager(), fragment);

    }

    public void changeAppHeader(Fragment fragment) {

        if(activityContext != null && activityContext instanceof MainActivity) {
            MainActivity activity = (MainActivity) activityContext;
            activity.changeAppHeader(fragment);
        }

    }

    public void openLoginPage() {

        MainActivity activity = (MainActivity) activityContext;
        activity.openActivity(activity, LoginActivity.class);

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

}
