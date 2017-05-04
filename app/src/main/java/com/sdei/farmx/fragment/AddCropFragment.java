package com.sdei.farmx.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdei.farmx.R;
import com.sdei.farmx.activity.MainActivity;
import com.sdei.farmx.adapter.GridImagesAdapter;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.databinding.FragmentAddCropBinding;
import com.sdei.farmx.dataobject.SingleSelectionItem;
import com.sdei.farmx.dialog.SingleSelectionDialog;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;

public class AddCropFragment extends AppFragment implements View.OnClickListener {

    private static GridImagesAdapter adapter;
    private SingleSelectionDialog sDialog = null;

    private ArrayList<String> gridImagesArrayList;

    private String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    RecyclerCallback itemClickListener = new RecyclerCallback() {
        @Override
        public void onItemClick(int position) {

            if (gridImagesArrayList != null && gridImagesArrayList.size() > 0) {
                gridImagesArrayList.remove(position);
                adapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onChildItemClick(int parentIndex, int childIndex) {

        }
    };

    /**
     * Called when a fragment is first attached to its context
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    /**
     * Called to have the fragment instantiate its user interface view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentAddCropBinding binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_add_crop, container, false);

        gridImagesArrayList = new ArrayList<>();
        binding.setHandler(AddCropFragment.this);
        binding.setImages(gridImagesArrayList);
        binding.setItemClickListener(itemClickListener);

        return binding.getRoot();
    }

    /**
     * Called when the fragment's activity has been created and this fragment's view hierarchy instantiated
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        changeAppHeader(this);
//        checkPermissions(activityContext,
//                AppConstants.AppPermission.NO_ACTION,
//                PERMISSIONS);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.publish_tv:
                break;

            case R.id.add_images:
                openAddImagesSourcesDialog();
                break;

        }

    }

    @BindingAdapter({"gridAdapter", "itemClickListener"})
    public static void setGridAdapter(RecyclerView view,
                                      ArrayList<String> items,
                                      RecyclerCallback callback) {

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(view.getContext(), 3);
        view.setLayoutManager(mLayoutManager);
        view.setItemAnimator(new DefaultItemAnimator());

        adapter = new GridImagesAdapter(items, callback);
        view.setAdapter(adapter);

    }

    /**
     * opens the Image sources dialog from which user either select Camera or Gallery option.
     */
    private void openAddImagesSourcesDialog() {

        ArrayList<SingleSelectionItem> items = new ArrayList<>();
        items.add(AppUtils.getSingleSelectionItem(0, "", getString(R.string.camera), 0));
        items.add(AppUtils.getSingleSelectionItem(1, "", getString(R.string.gallery), 0));

        if (sDialog == null) {

            sDialog = new SingleSelectionDialog(activityContext,
                    items,
                    new RecyclerCallback() {
                        @Override
                        public void onItemClick(int position) {
                            performImagesAction(position);
                            sDialog.dismiss();
                        }

                        @Override
                        public void onChildItemClick(int parentIndex, int childIndex) {

                        }

                    });

            sDialog.setHeader(getString(R.string.add_image));

            sDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    sDialog = null;
                }
            });

            sDialog.show();
        }

    }

    /**
     * Perform the action requested from Image sources dialog
     * @param action either 0 or 1. 0 for camera and 1 for gallery.
     */
    private void performImagesAction(int action) {

        switch (action) {

            case 0:
                openCameraWithPermissionsCheck();
                break;

            case 1:
                openPhoneGallery();
                break;

        }

    }

    /**
     *
     * Open up the Gallery app after checking the required permissions.
     * If the permissions were not given yet by the user then it will request the
     * user to give permission for read from External Storage.
     *
     */
    private void openPhoneGallery() {

        String externalStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE;

        boolean hasReadExternalStoragePermission = AppUtils.hasPermission(activityContext, externalStoragePermission);
        boolean requestReadExternalStorageRationale
                = ActivityCompat.shouldShowRequestPermissionRationale((Activity) activityContext, externalStoragePermission);

        if (hasReadExternalStoragePermission) {

            openGallery();

        } else if (requestReadExternalStorageRationale) {

            checkPermissions(activityContext,
                    AppConstants.AppPermission.ACTION_OPEN_GALLERY, externalStoragePermission);

        } else {

            MainActivity activity = (MainActivity) activityContext;
            activity.requestPermissionRationale(false, false, getString(android.R.string.ok), externalStoragePermission);

        }

    }

    /**
     * open up the Gallery App by using the Activity reference
     */
    private void openGallery() {

        MainActivity activity = (MainActivity) activityContext;
        activity.openGallery();

    }

    /**
     *
     * Open up the camera app after checking the required permissions.
     * If the permissions were not given yet by the user then it will request the
     * user to give permission for open Camera and to write into the External Storage.
     *
     */
    private void openCameraWithPermissionsCheck() {

        String cameraPermission = Manifest.permission.CAMERA;
        String externalStoragePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        boolean hasCameraPermission = AppUtils.hasPermission(activityContext, cameraPermission);
        boolean hasWriteStoragePermission = AppUtils.hasPermission(activityContext, externalStoragePermission);

        boolean requestCameraPermissionRationale
                = ActivityCompat.shouldShowRequestPermissionRationale((Activity) activityContext, cameraPermission);
        boolean requestStoragePermissionRationale
                = ActivityCompat.shouldShowRequestPermissionRationale((Activity) activityContext, externalStoragePermission);

        if (hasCameraPermission
                && hasWriteStoragePermission) {

            openCamera();

        } else if ((!hasCameraPermission && requestCameraPermissionRationale)
                && (!hasWriteStoragePermission && requestStoragePermissionRationale)) {

            checkPermissions(activityContext,
                    AppConstants.AppPermission.ACTION_OPEN_CAMERA,
                    PERMISSIONS);

        } else if (!hasCameraPermission
                && requestCameraPermissionRationale) {

            checkPermissions(activityContext,
                    AppConstants.AppPermission.ACTION_OPEN_CAMERA, cameraPermission);

        } else if (!hasWriteStoragePermission
                && requestStoragePermissionRationale) {

            checkPermissions(activityContext,
                    AppConstants.AppPermission.ACTION_OPEN_CAMERA, externalStoragePermission);

        } else {

            MainActivity activity = (MainActivity) activityContext;
            activity.requestPermissionRationale(false, false, getString(android.R.string.ok), PERMISSIONS);

        }

    }

    /**
     * Add the path of the last clicked camera image to images arraylist
     * @param path path of the camera image
     */
    public void afterCameraClick(String path) {
        gridImagesArrayList.add(path);
    }

}
