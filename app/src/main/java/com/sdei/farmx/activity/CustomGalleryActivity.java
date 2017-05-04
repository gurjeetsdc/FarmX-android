package com.sdei.farmx.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.sdei.farmx.R;
import com.sdei.farmx.helper.utils.AppLogger;

public class CustomGalleryActivity extends Activity implements View.OnClickListener {

    private GridView grdImages;

    private ImageAdapter imageAdapter;
    private String[] arrPath;
    private boolean[] thumbnailsselection;
    private int ids[];
    private int count;

    /**
     * Overrides methods
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_custom_gallery);

        grdImages = (GridView) findViewById(R.id.grdImages);

        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        final String orderBy = MediaStore.Images.Media._ID;

        @SuppressWarnings("deprecation")
        Cursor imageCursor
                = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, null, null, orderBy);
        int image_column_index
                = imageCursor.getColumnIndex(MediaStore.Images.Media._ID);
        this.count = imageCursor.getCount();
        this.arrPath = new String[this.count];
        ids = new int[count];
        this.thumbnailsselection = new boolean[this.count];

        for (int i = 0; i < this.count; i++) {
            imageCursor.moveToPosition(i);
            ids[i] = imageCursor.getInt(image_column_index);
            int dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Images.Media.DATA);
            arrPath[i] = imageCursor.getString(dataColumnIndex);
        }

        imageCursor.close();

        imageAdapter = new ImageAdapter();
        grdImages.setAdapter(imageAdapter);

    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    /**
     * This method used to set bitmap.
     *
     * @param iv represented ImageView
     * @param id represented id
     */
    private void setBitmap(final ImageView iv, final int id) {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return MediaStore.Images.Thumbnails.getThumbnail(
                        getApplicationContext().getContentResolver(),
                        id,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        null);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iv.setImageBitmap(result);
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_iv:
                setResult(Activity.RESULT_CANCELED);
                finish();
                break;

            case R.id.btnSelect:
                sendSelectedImages();
                break;

        }

    }

    private void sendSelectedImages() {

        final int len = thumbnailsselection.length;

        int cnt = 0;

        String selectImages = "";

        for (int i = 0; i < len; i++) {

            if (thumbnailsselection[i]) {
                cnt++;
                selectImages = selectImages + arrPath[i] + "|";
            }

        }

        if (cnt > 0) {

            AppLogger.log("SelectedImages", selectImages);
            Intent i = new Intent();
            i.putExtra("data", selectImages);
            setResult(Activity.RESULT_OK, i);
            finish();

        }

    }


    /**
     * List adapter
     *
     * @author tasol
     */

    class ImageAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return count;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.custom_gallery_item, null);
                holder.imgThumb = (ImageView) convertView.findViewById(R.id.imgThumb);
                holder.chkImage = (CheckBox) convertView.findViewById(R.id.chkImage);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.chkImage.setId(position);
            holder.imgThumb.setId(position);

            holder.chkImage.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (thumbnailsselection[id]) {
                        cb.setChecked(false);
                        thumbnailsselection[id] = false;
                    } else {
                        cb.setChecked(true);
                        thumbnailsselection[id] = true;
                    }
                }
            });

            holder.imgThumb.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    int id = holder.chkImage.getId();
                    if (thumbnailsselection[id]) {
                        holder.chkImage.setChecked(false);
                        thumbnailsselection[id] = false;
                    } else {
                        holder.chkImage.setChecked(true);
                        thumbnailsselection[id] = true;
                    }
                }
            });

            try {
                setBitmap(holder.imgThumb, ids[position]);
            } catch (Throwable e) {
            }
            holder.chkImage.setChecked(thumbnailsselection[position]);
            holder.id = position;
            return convertView;
        }
    }


    /**
     * Inner class
     *
     * @author tasol
     */
    class ViewHolder {
        ImageView imgThumb;
        CheckBox chkImage;
        int id;
    }

}
