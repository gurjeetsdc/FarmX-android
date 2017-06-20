package com.sdei.farmx.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.sdei.farmx.R;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.callback.RecyclerMoreActionCallback;
import com.sdei.farmx.databinding.ActivityCustomGalleryBinding;
import com.sdei.farmx.dataobject.CustomGalleryData;
import com.sdei.farmx.helper.utils.AppConstants;

import java.util.ArrayList;

public class CustomGalleryActivity extends AppActivity implements View.OnClickListener {

    private ArrayList<CustomGalleryData> objects;
    private int imagesCount = AppConstants.CustomGallery.MAXIMUM_IMAGES;

    RecyclerMoreActionCallback callback = new RecyclerMoreActionCallback() {

        @Override
        public void itemAction(String type, int position) {

        }

        @Override
        public void onItemClick(int position) {

            int count = getSelectedItemsCount();

            if (objects.get(position).isChecked()
                    || count < imagesCount) {
                objects.get(position).setChecked(!objects.get(position).isChecked());
            } else {
                objects.get(position).setChecked(false);
            }

        }

        @Override
        public void onChildItemClick(int parentIndex, int childIndex) {

        }

    };

    private int getSelectedItemsCount() {

        int count = 0;

        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).isChecked()) {
                count = count + 1;
            }
        }

        return count;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCustomGalleryBinding binding
                = DataBindingUtil.setContentView(CustomGalleryActivity.this, R.layout.activity_custom_gallery);
        getExtras();
        getGalleryData();
        binding.setItems(objects);
        binding.setItemClickListener(callback);

    }

    private void getExtras() {

        if(getIntent().hasExtra("count")) {
            imagesCount = getIntent().getIntExtra("count", AppConstants.CustomGallery.MAXIMUM_IMAGES);
        }

    }

    /**
     * Get the images data from user devices so that we
     * can display thumbnail of images in our custom gallery
     */
    private void getGalleryData() {

        objects = new ArrayList<>();

        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;

        @SuppressWarnings("deprecation")
        Cursor imageCursor
                = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, null, null, orderBy);
        int image_column_index
                = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
        int count = imageCursor.getCount();

        for (int i = 0; i < count; i++) {

            imageCursor.moveToPosition(i);

            CustomGalleryData object = new CustomGalleryData();
            object.setId(imageCursor.getInt(image_column_index));
            int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            object.setPath(imageCursor.getString(dataColumnIndex));
            objects.add(object);

        }

    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_ll:
                setResult(Activity.RESULT_CANCELED);
                finishActivity(CustomGalleryActivity.this);
                break;

            case R.id.btnSelect:
                sendSelectedImages();
                break;

        }

    }

    /**
     * In this we are sending selected images path. Maximum 5 images path will be send.
     */
    private void sendSelectedImages() {

        String selectImages = "";
        boolean hasSelection = false;

        for (int i = 0; i < objects.size(); i++) {

            if (objects.get(i).isChecked()) {
                hasSelection = true;
                selectImages = selectImages + objects.get(i).getPath() + "|";
            }

        }

        if (hasSelection) {

            Intent i = new Intent();
            i.putExtra("data", selectImages);
            setResult(Activity.RESULT_OK, i);
            finishActivity(CustomGalleryActivity.this);

        }

    }

}
