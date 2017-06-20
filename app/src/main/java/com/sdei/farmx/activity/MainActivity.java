package com.sdei.farmx.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sdei.farmx.R;
import com.sdei.farmx.apimanager.ApiManager;
import com.sdei.farmx.callback.DialogCallback;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.dataobject.AppHeaderObject;
import com.sdei.farmx.dataobject.RecyclerBindingList;
import com.sdei.farmx.dataobject.NavigationDrawerItem;
import com.sdei.farmx.databinding.ActivityMainBinding;
import com.sdei.farmx.databinding.NavHeaderMainBinding;
import com.sdei.farmx.dataobject.NavigationHeader;
import com.sdei.farmx.fragment.CropAddFragment;
import com.sdei.farmx.fragment.CropBidListFragment;
import com.sdei.farmx.fragment.CropBidUserDetailFragment;
import com.sdei.farmx.fragment.CropDashboardFragment;
import com.sdei.farmx.fragment.CropDetailFragment;
import com.sdei.farmx.fragment.CropMyBidsFragment;
import com.sdei.farmx.fragment.CropMyCropsFragment;
import com.sdei.farmx.fragment.EquipmentAddFragment;
import com.sdei.farmx.fragment.EquipmentMyEquipmentsFragment;
import com.sdei.farmx.fragment.InputDashboardFragment;
import com.sdei.farmx.fragment.CropMoreItemsFragment;
import com.sdei.farmx.fragment.HomeFragment;
import com.sdei.farmx.fragment.InputDetailFragment;
import com.sdei.farmx.helper.AppInstance;
import com.sdei.farmx.helper.preferences.MyPreference;
import com.sdei.farmx.helper.preferences.PreferenceConstant;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppLogger;
import com.sdei.farmx.helper.utils.AppUtils;
import com.sdei.farmx.helper.utils.NotificationUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppActivity
        implements View.OnClickListener, RecyclerCallback {

    private static final int APP_PERMISSIONS = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;
    private static final int REQUEST_CUSTOM_GALLERY = 103;

    private int permissionAction;

    private LinearLayout navigationViewLl;
    private DrawerLayout drawer;
    private RecyclerBindingList<NavigationDrawerItem> bindingList;
    private String mCurrentPhotoPath;

    private BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // checking for type intent filter
            if (intent.getAction().equals(AppConstants.Notification.REGISTRATION_COMPLETE)) {

                String regId = MyPreference.getString(MainActivity.this, PreferenceConstant.FIREBASE_REG_ID);
                AppLogger.log("MainActivity", "Firebase reg id: " + regId);

            } else if (intent.getAction().equals(AppConstants.Notification.PUSH_NOTIFICATION)) {
                // new push notification is received

                String message = intent.getStringExtra("message");
                Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

            }

        }

    };

    BroadcastReceiver networkChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!AppUtils.hasNetworkConnection(context)
                    && ApiManager.communicatingToServer) {
                ApiManager.communicatingToServer = false;
                Toast.makeText(context, "Internet not available", Toast.LENGTH_SHORT).show();
            }

        }
    };

    /**
     * Called when the activity is first created.
     * This is where we do all of our normal static set up: create views,
     * bind data to lists, etc.
     *
     * @param savedInstanceState containing the activity's previously frozen state, if there was one.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding
                = DataBindingUtil.setContentView(this, R.layout.activity_main);
        headerObject = new AppHeaderObject();
        binding.setHeader(headerObject);

        bindingList = new RecyclerBindingList();
        bindingList.setItemsList(new ArrayList<NavigationDrawerItem>());
        binding.setItem(bindingList);
        binding.setCallback(MainActivity.this);

        drawer = binding.drawerLayout;
        navigationViewLl = binding.navigationViewLl;

        bindData();

    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.net.wifi.STATE_CHANGE");
        registerReceiver(networkChangeReceiver, filter);

        // register GCM registration complete receiver
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(mRegistrationBroadcastReceiver,
                        new IntentFilter(AppConstants.Notification.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(mRegistrationBroadcastReceiver,
                        new IntentFilter(AppConstants.Notification.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
        updateNavigationDrawerItems();

    }

    private void updateNavigationDrawerItems() {

        initUserObject();
        setNavigationViewHeader();
        ArrayList<NavigationDrawerItem> dataList = bindingList.getItemsList();
        dataList.clear();
        AppUtils.getNavigationItemsList(dataList, MainActivity.this);
        bindingList.setItemsList(dataList);

        if (bindingList.getAdapter() != null)
            bindingList.getAdapter().notifyDataSetChanged();

    }

    @Override
    protected void onPause() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        //LocalBroadcastManager.getInstance(this).unregisterReceiver(networkChangeReceiver);
        super.onPause();


    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkChangeReceiver);
        super.onDestroy();
        AppInstance.appUser = null;
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else if (AppConstants.FragmentConstant.fragment instanceof HomeFragment) {

            finishActivity(MainActivity.this);

        } else if (AppConstants.FragmentConstant.fragment instanceof CropAddFragment) {

            AppUtils.finishFragment(getSupportFragmentManager(),
                    AppConstants.FragmentConstant.fragment);
            if (AppConstants.FragmentConstant.fragment instanceof CropMyCropsFragment) {
                AppUtils.finishFragment(getSupportFragmentManager(),
                        AppConstants.FragmentConstant.fragment);
                openMyCropsPage(MainActivity.this);
            }
            changeAppHeader(AppConstants.FragmentConstant.fragment,
                    getTitleName(AppConstants.FragmentConstant.fragment));

        } else {

            AppUtils.finishFragment(getSupportFragmentManager(),
                    AppConstants.FragmentConstant.fragment);
            changeAppHeader(AppConstants.FragmentConstant.fragment,
                    getTitleName(AppConstants.FragmentConstant.fragment));

        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.nav_menu_ll:
                drawer.openDrawer(Gravity.START);
                break;

            case R.id.home_ll:
                AppUtils.clearFragmentStack(getSupportFragmentManager());
                changeAppHeader(
                        AppConstants.FragmentConstant.fragment,
                        getTitleName(AppConstants.FragmentConstant.fragment));
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == APP_PERMISSIONS
                && grantResults.length > 0
                && permissionAction != AppConstants.AppPermission.NO_ACTION) {

            if (permissionAction == AppConstants.AppPermission.ACTION_OPEN_CAMERA
                    && AppUtils.hasPermission(MainActivity.this, Manifest.permission.CAMERA)
                    && AppUtils.hasPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                openCamera();
                permissionAction = 0;

            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE
                && resultCode == RESULT_OK) {

            if (AppConstants.FragmentConstant.fragment instanceof CropAddFragment) {
                CropAddFragment fragment = (CropAddFragment) AppConstants.FragmentConstant.fragment;
                fragment.afterCameraClick(mCurrentPhotoPath);
            } else if (AppConstants.FragmentConstant.fragment instanceof EquipmentAddFragment) {
                EquipmentAddFragment fragment = (EquipmentAddFragment) AppConstants.FragmentConstant.fragment;
                fragment.afterCameraClick(mCurrentPhotoPath);
            }

        } else if (requestCode == REQUEST_CUSTOM_GALLERY
                && resultCode == Activity.RESULT_OK) {

            String[] paths = data.getStringExtra("data").split("\\|");

            if (AppConstants.FragmentConstant.fragment instanceof CropAddFragment) {
                CropAddFragment fragment = (CropAddFragment) AppConstants.FragmentConstant.fragment;
                fragment.fetchedImagesFromGallery(paths);
            } else if (AppConstants.FragmentConstant.fragment instanceof EquipmentAddFragment) {
                EquipmentAddFragment fragment = (EquipmentAddFragment) AppConstants.FragmentConstant.fragment;
                fragment.fetchedImagesFromGallery(paths);
            }

        } else if (resultCode == Activity.RESULT_OK) {

            performPendingTaskBeforeLogin(requestCode);

        }

    }

    private void performPendingTaskBeforeLogin(int pendingTask) {

        switch (pendingTask) {

            case AppConstants.PendingTask.SESSION_EXPIRED:
                performPendingTaskBeforeSessionExpired();
                break;

            case AppConstants.PendingTask.ADD_CROP:
                openAddCropPage(MainActivity.this, false, null);
                break;

            case AppConstants.PendingTask.ADD_EQUIPMENT:
                openAddEquipmentFragment(MainActivity.this, equipmentActionType, null);
                break;

            case AppConstants.PendingTask.MY_CROPS:
                openMyCropsPage(MainActivity.this);
                break;

            case AppConstants.PendingTask.MY_EQUIPMENTS:
                openMyEquipmentsFragment(MainActivity.this);
                break;

            case AppConstants.PendingTask.MY_BIDS:
                openMyBidsPage(MainActivity.this);
                break;

            case AppConstants.PendingTask.NO_TASK:
                refreshCurrentPageListingData();
                break;

        }

    }

    private void refreshCurrentPageListingData() {

        if (AppConstants.FragmentConstant.fragment instanceof InputDashboardFragment) {

            InputDashboardFragment fragment = (InputDashboardFragment) AppConstants.FragmentConstant.fragment;
            fragment.fetchInputsList();

        } else if (AppConstants.FragmentConstant.fragment instanceof CropDashboardFragment) {

            CropDashboardFragment fragment = (CropDashboardFragment) AppConstants.FragmentConstant.fragment;
            fragment.fetchCropListing();

        } else if (AppConstants.FragmentConstant.fragment instanceof CropMoreItemsFragment) {

            CropMoreItemsFragment fragment = (CropMoreItemsFragment) AppConstants.FragmentConstant.fragment;
            fragment.fetchCropListing();

        } else if (AppConstants.FragmentConstant.fragment instanceof CropDetailFragment) {

            CropDetailFragment fragment = (CropDetailFragment) AppConstants.FragmentConstant.fragment;
            fragment.fetchPageData();

        } else if (AppConstants.FragmentConstant.fragment instanceof InputDetailFragment) {

//            InputDetailFragment fragment = (InputDetailFragment) AppConstants.FragmentConstant.fragment;
//            fragment.fetchPageData();

        }

    }

    private void performPendingTaskBeforeSessionExpired() {

        if (AppConstants.FragmentConstant.fragment instanceof CropAddFragment) {

            CropAddFragment fragment = (CropAddFragment) AppConstants.FragmentConstant.fragment;
            fragment.bindData();

        } else if (AppConstants.FragmentConstant.fragment instanceof EquipmentAddFragment) {

            EquipmentAddFragment fragment = (EquipmentAddFragment) AppConstants.FragmentConstant.fragment;
            fragment.bindData();

        } else {
            refreshCurrentPageListingData();
        }

    }

    /**
     * This method will set the side menu for the app and will open HomeFragment
     * screen to user.
     */
    void bindData() {

        navHeader = new NavigationHeader();
        openHomePage();

    }

    private void openHomePage() {

        HomeFragment fragment = new HomeFragment();
        AppUtils.openHomeFragment(getSupportFragmentManager(), fragment);
        changeAppHeader(fragment, null);

    }

    private void performNavigationItemAction(int position) {

        NavigationDrawerItem item = bindingList.getItemsList().get(position);

//        if (item.getKey().equals(getString(R.string.key_equipments))) {
//
//            openMyEquipmentsFragment(MainActivity.this);
//
//        } else
        if (item.getKey().equals(getString(R.string.key_farm_inputs))) {

            openFragment(new InputDashboardFragment());

        } else if (item.getKey().equals(getString(R.string.key_log_out))) {

            AppUtils.openPNAlertDialog(
                    MainActivity.this,
                    getString(R.string.message_logout),
                    getString(R.string.ok),
                    new DialogCallback() {
                        @Override
                        public void onSuccess() {

                            AppUtils.logoutUser(MainActivity.this);
                            updateNavigationDrawerItems();

                            if (AppConstants.FragmentConstant.fragment instanceof CropAddFragment
                                    || AppConstants.FragmentConstant.fragment instanceof CropMyBidsFragment
                                    || AppConstants.FragmentConstant.fragment instanceof CropMyCropsFragment
                                    || AppConstants.FragmentConstant.fragment instanceof EquipmentMyEquipmentsFragment
                                    || AppConstants.FragmentConstant.fragment instanceof EquipmentAddFragment
                                    || AppConstants.FragmentConstant.fragment instanceof CropBidListFragment
                                    || AppConstants.FragmentConstant.fragment instanceof CropBidUserDetailFragment) {

                                AppUtils.clearFragmentStack(getSupportFragmentManager());
                                changeAppHeader(
                                        AppConstants.FragmentConstant.fragment,
                                        getTitleName(AppConstants.FragmentConstant.fragment));

                            } else {
                                refreshCurrentPageListingData();
                            }

                        }

                        @Override
                        public void onFailure() {

                        }

                    });

        } else if (item.getKey().equals(getString(R.string.key_loging))
                || (!isUserLoggedIn() && item.getKey().equals(getString(R.string.key_my_account)))) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openLoginActivity(
                            MainActivity.this,
                            "login",
                            AppConstants.PendingTask.NO_TASK);
                }
            }, 500);

        } else {

            Toast.makeText(MainActivity.this, "In progress", Toast.LENGTH_SHORT).show();

        }

    }

    private void performNavigationChildAction(int parentIndex, int childIndex) {

        ArrayList<NavigationDrawerItem> childItems
                = bindingList.getItemsList().get(parentIndex).getChildItems();

        if (childItems.get(childIndex).getKey().equals(getString(R.string.key_my_crops))) {
            openMyCropsPage(MainActivity.this);
        } else if (childItems.get(childIndex).getKey().equals(getString(R.string.key_my_bids))) {
            openMyBidsPage(MainActivity.this);
        } else {
            Toast.makeText(this, "In progress", Toast.LENGTH_SHORT).show();
        }

    }

    private void openMyBidsPage(final Context context) {

        if (isUserLoggedIn()) {

            openFragment(new CropMyBidsFragment());

        } else {

            showLoginToContinueAlert(context, AppConstants.PendingTask.MY_BIDS);

        }

    }

    /**
     * set the Navigation Drawer Header
     */
    private void setNavigationViewHeader() {

        if (navigationViewLl.getChildCount() > 1) {
            navigationViewLl.removeViewAt(0);
        }

        NavHeaderMainBinding binding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.nav_header_main,
                navigationViewLl,
                false);
        binding.setUser(AppInstance.appUser);
        binding.setHeader(navHeader);

        navigationViewLl.addView(binding.getRoot(), 0);

    }

    /**
     * Check the app permission for Marshmallow or above OS devices
     *
     * @param action      to perform after allowing permission
     * @param permissions permissions to request
     * @return true if all requested permissions are given by user
     */
    public boolean checkPermission(int action, String... permissions) {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && permissions != null) {

            permissionAction = action;
            List<String> listPermissionsNeeded = new ArrayList<>();

            for (String permission : permissions) {

                if (ActivityCompat.checkSelfPermission(MainActivity.this, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(permission);
                }

            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), APP_PERMISSIONS);
                return false;
            }

        }

        return true;

    }

    /**
     * Show the alert with message when user deny all the request permissions
     *
     * @param requestPermissionRationale will be true when user deny all the request permissions
     * @param alreadyAskForPermission    will be true when user already has to ask for
     *                                   forcefully allow the permissions using app settings
     * @param positiveButtonText         text to shown on Alert Positive button
     * @param permissions                list of requested permissions
     */
    public void requestPermissionRationale(final boolean requestPermissionRationale,
                                           final boolean alreadyAskForPermission,
                                           String positiveButtonText,
                                           final String... permissions) {

        if (!requestPermissionRationale
                && alreadyAskForPermission) {
            return;
        }

        AppUtils.openPNAlertDialogWithTitle(
                MainActivity.this,
                getString(R.string.permission_denied),
                getString(R.string.permission_denied_message),
                positiveButtonText,
                new DialogCallback() {
                    @Override
                    public void onSuccess() {

                        if (requestPermissionRationale) {
                            ActivityCompat.requestPermissions(MainActivity.this, permissions,
                                    APP_PERMISSIONS);
                        } else {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, APP_PERMISSIONS);

                            MyPreference.saveBoolean(MainActivity.this,
                                    PreferenceConstant.PERMISSION_DENIED_REQUEST,
                                    true);

                        }
                    }

                    @Override
                    public void onFailure() {

                        if (!alreadyAskForPermission
                                && !requestPermissionRationale) {
                            MyPreference.saveBoolean(MainActivity.this,
                                    PreferenceConstant.PERMISSION_DENIED_REQUEST,
                                    true);
                        }

                    }

                });

    }

    /**
     * will open the camera app
     */
    public void openCamera() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri uri;

                if (Build.VERSION.SDK_INT > 23) {
                    uri = FileProvider.getUriForFile(this,
                            "com.sdei.farmx.fileprovider",
                            photoFile);
                } else {
                    uri = Uri.fromFile(photoFile);
                }

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    /**
     * returns the File path to save the camera image
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp
                = AppUtils.getSimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        if (!image.exists()) {
            image.getParentFile().mkdirs();
            image.createNewFile();
        }

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void openCustomGallery(int remainingCount) {
        Intent intent = new Intent(AppConstants.CustomGallery.ACTION_MULTIPLE_PICK);
        intent.putExtra("count", remainingCount);
        startActivityForResult(intent, REQUEST_CUSTOM_GALLERY);
    }

    @Override
    public void onItemClick(int position) {

        NavigationDrawerItem object = bindingList.getItemsList().get(position);

        if (object.getChildItems() != null
                && object.getChildItems().size() > 0) {

            bindingList.getItemsList()
                    .get(position)
                    .setChecked(!bindingList.getItemsList().get(position).isChecked());

        } else {

            drawer.closeDrawer(GravityCompat.START);
            performNavigationItemAction(position);

        }

    }

    @Override
    public void onChildItemClick(int parentIndex, int childIndex) {

        bindingList.getItemsList()
                .get(parentIndex)
                .setChecked(!bindingList.getItemsList().get(parentIndex).isChecked());
        drawer.closeDrawer(GravityCompat.START);

        performNavigationChildAction(parentIndex, childIndex);

    }

}
