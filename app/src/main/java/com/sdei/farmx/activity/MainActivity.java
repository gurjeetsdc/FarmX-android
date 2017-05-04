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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sdei.farmx.R;
import com.sdei.farmx.adapter.NavigationItemsAdapter;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.dataobject.AppHeaderObject;
import com.sdei.farmx.dataobject.NavigationDrawerItem;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.databinding.ActivityMainBinding;
import com.sdei.farmx.databinding.NavHeaderMainBinding;
import com.sdei.farmx.fragment.AddCropFragment;
import com.sdei.farmx.fragment.BuyCropFragment;
import com.sdei.farmx.fragment.HomeFragment;
import com.sdei.farmx.helper.preferences.MyPreference;
import com.sdei.farmx.helper.preferences.PreferenceConstant;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppLogger;
import com.sdei.farmx.helper.utils.AppUtils;
import com.sdei.farmx.helper.utils.NotificationUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppActivity implements View.OnClickListener {

    private static final int APP_PERMISSIONS = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;
    private static final int REQUEST_GALLERY = 103;

    private int permissionAction;

    private LinearLayout navigationViewLl;

    private DrawerLayout drawer;

    private RecyclerView navRecyclerView;

    private NavigationItemsAdapter nAdapter;
    private ArrayList<NavigationDrawerItem> navItemsArrayList;

    private AppHeaderObject headerObject;

    private String mCurrentPhotoPath;

    RecyclerCallback recyclerCallback = new RecyclerCallback() {
        @Override
        public void onItemClick(int position) {

            NavigationDrawerItem object = navItemsArrayList.get(position);

            if (object.getChildItems() != null
                    && object.getChildItems().size() > 0) {

                navItemsArrayList.get(position).setChecked(!navItemsArrayList.get(position).isChecked());

            } else {

                drawer.closeDrawer(GravityCompat.START);

            }

        }

        @Override
        public void onChildItemClick(int parentIndex, int childIndex) {

            navItemsArrayList.get(parentIndex).setChecked(!navItemsArrayList.get(parentIndex).isChecked());
            nAdapter.notifyDataSetChanged();
            drawer.closeDrawer(GravityCompat.START);

        }

    };

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

    /**
     * Called when the activity is first created.
     * This is where we do all of our normal static set up: create views,
     * bind data to lists, etc.
     * @param savedInstanceState containing the activity's previously frozen state, if there was one.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        headerObject = new AppHeaderObject();
        binding.setHeader(headerObject);

        drawer = binding.drawerLayout;
        navigationViewLl = binding.navigationViewLl;
        navRecyclerView = binding.navItemsRv;

        bindData();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppConstants.Notification.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(AppConstants.Notification.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());

    }

    @Override
    protected void onPause() {

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();

    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {

            drawer.closeDrawer(GravityCompat.START);

        } else if (AppConstants.FragmentConstant.fragment instanceof AddCropFragment
                || AppConstants.FragmentConstant.fragment instanceof BuyCropFragment) {

            AppUtils.finishFragment(getSupportFragmentManager(),
                    AppConstants.FragmentConstant.fragment);
            changeAppHeader(AppConstants.FragmentConstant.fragment);

        } else if (AppConstants.FragmentConstant.fragment instanceof HomeFragment) {

            finish();

        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.nav_menu_ll:
                drawer.openDrawer(Gravity.START);
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

            if (AppConstants.FragmentConstant.fragment instanceof AddCropFragment) {
                AddCropFragment fragment = (AddCropFragment) AppConstants.FragmentConstant.fragment;
                fragment.afterCameraClick(mCurrentPhotoPath);
            }

        } else if (requestCode == REQUEST_GALLERY
                && resultCode == Activity.RESULT_OK) {

            String[] all_path = data.getStringExtra("data").split("\\|");

        }

    }

    /**
     * This method will set the side menu for the app and will open HomeFragment
     * screen to user.
     */
    void bindData() {

        setNavigationItemsAdapter();

        HomeFragment fragment = new HomeFragment();
        AppUtils.openFragment(getSupportFragmentManager(), fragment);
        changeAppHeader(fragment);

    }

    /**
     * set the Navigation Drawer menu items
     */
    private void setNavigationItemsAdapter() {

        navItemsArrayList = AppUtils.getNavigationItemsList(MainActivity.this);
        setNavigationViewHeader();

        nAdapter = new NavigationItemsAdapter(navItemsArrayList, recyclerCallback);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        navRecyclerView.setLayoutManager(mLayoutManager);
        navRecyclerView.setItemAnimator(new DefaultItemAnimator());
        navRecyclerView.setAdapter(nAdapter);

    }

    /**
     * set the Navigation Drawer Header
     */
    private void setNavigationViewHeader() {

        NavHeaderMainBinding binding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.nav_header_main,
                navigationViewLl,
                false);
        User user = new User();
        user.setFirstName("Rahul Sharma");
        user.setId("123456789");
        binding.setUser(user);

        navigationViewLl.addView(binding.getRoot(), 0);

    }

    /**
     * Change the app header depending upon current fragment instance.
     *
     * @param fragment instance to change the App header accordingly
     */
    public void changeAppHeader(Fragment fragment) {

        if (fragment instanceof HomeFragment) {

            headerObject.setBackgroundDrawable(R.drawable.dashboard_amber_gradient);
            headerObject.setBackgroundColor(0);
            headerObject.setHasCenteredImage(true);
            headerObject.setShowSearch(true);

            AppUtils.setStatusBarTint(this, R.color.amber);

        } else if (fragment instanceof AddCropFragment
                || fragment instanceof BuyCropFragment) {

            headerObject.setBackgroundDrawable(0);
            headerObject.setBackgroundColor(R.color.crop_green);
            headerObject.setShowSearch(false);
            headerObject.setHasCenteredImage(false);

            AppUtils.setStatusBarTint(this, R.color.crop_green);

        }

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

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle(getString(R.string.permission_denied));
        builder.setMessage(getString(R.string.permission_denied_message));
        builder.setCancelable(false);

        builder.setPositiveButton(positiveButtonText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();

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
                });

        builder.setNegativeButton(getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();

                        if (!alreadyAskForPermission
                                && !requestPermissionRationale) {
                            MyPreference.saveBoolean(MainActivity.this,
                                    PreferenceConstant.PERMISSION_DENIED_REQUEST,
                                    true);
                        }

                    }
                });

        builder.show();

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
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.sdei.farmx.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    /**
     * returns the File path to save the camera image
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp
                = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * will open the Gallery app
     */
    public void openGallery() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY);
        } else {
            openCustomGallery();
        }

    }

    private void openCustomGallery() {
        Intent intent = new Intent(AppConstants.CustomGallery.ACTION_MULTIPLE_PICK);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

}
