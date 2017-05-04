package com.sdei.farmx.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sdei.farmx.R;
import com.sdei.farmx.adapter.GridImagesAdapter;
import com.sdei.farmx.adapter.LoginPagerAdapter;
import com.sdei.farmx.adapter.SingleSelectionAdapter;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.customview.CustomPager;
import com.sdei.farmx.dataobject.NavigationDrawerItem;
import com.sdei.farmx.dataobject.SingleSelectionItem;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;

public class MyBindingsAdapter {

    /**
     * Set the top compound drawable of the TextView
     * @param view to set compound drawable
     * @param iconId drawable id to set over view
     */
    @BindingAdapter({"icon"})
    public static void setCompoundDrawablesWithIntrinsicBounds(TextView view, int iconId) {
        view.setCompoundDrawablesWithIntrinsicBounds(0, iconId, 0, 0);
    }

    /**
     * Set the background drawable
     * @param view whose background to set
     * @param drawableId drawable to set over the given view
     */
    @BindingAdapter({"background"})
    public static void setViewBackground(View view, int drawableId) {

        Drawable drawable = null;

        if (drawableId != 0) {
            drawable = AppUtils.getDrawable(view.getContext(), drawableId);
        }

        view.setBackground(drawable);

    }

    /**
     * Set the background drawable or color
     * @param view to operate on
     * @param drawableId drawable id to set as background
     * @param backgroundColor color id to set as background color
     */
    @BindingAdapter({"background_drawable", "background_color"})
    public static void setHeaderBackground(View view,
                                           int drawableId,
                                           int backgroundColor) {

        Drawable drawable = null;

        if (drawableId != 0) {
            drawable = AppUtils.getDrawable(view.getContext(), drawableId);
        }

        view.setBackground(drawable);

        if (backgroundColor != 0) {
            view.setBackgroundColor(AppUtils.getColor(view.getContext(), backgroundColor));
        }

    }

    @BindingAdapter({"child_views", "child_callback"})
    public static void setNavChildViews(LinearLayout navChildLl,
                                        final ArrayList<NavigationDrawerItem> childItems,
                                        final RecyclerCallback callback) {

        if (childItems != null && childItems.size() > 0) {

            Context context = navChildLl.getContext();

            LayoutInflater inflater = AppUtils.getLayoutInflater(context);

            for (int i = 0; i < childItems.size(); i++) {

                View view = inflater.inflate(R.layout.row_nav_child, navChildLl, false);

                LinearLayout parentLl = (LinearLayout) view.findViewById(R.id.parent_ll);
                ImageView iconIv = (ImageView) view.findViewById(R.id.icon_iv);
                TextView nameTv = (TextView) view.findViewById(R.id.item_name_tv);

                nameTv.setText(childItems.get(i).getItemName());
                iconIv.setBackgroundResource(childItems.get(i).getDrawable());

                parentLl.setTag(i);

                parentLl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int pos = Integer.parseInt(view.getTag().toString());
                        callback.onChildItemClick(childItems.get(pos).getParentPosition(), pos);

                    }
                });

                navChildLl.addView(view);


            }

        }

    }

    @BindingAdapter({"items", "item_click_listener"})
    public static void setPopupAdapter(RecyclerView view,
                                       ArrayList<SingleSelectionItem> items,
                                       RecyclerCallback callback) {

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        view.setLayoutManager(mLayoutManager);
        view.setItemAnimator(new DefaultItemAnimator());

        SingleSelectionAdapter adapter = new SingleSelectionAdapter(items, callback);
        view.setAdapter(adapter);

    }

    @BindingAdapter({"attachToPager"})
    public static void bindViewPagerTabs(final TabLayout view, CustomPager pager) {
        view.setupWithViewPager(pager, true);
    }

    @BindingAdapter({"adapter"})
    public static void setPagerAdapter(CustomPager viewPager, ArrayList<String> pagerItems) {
        LoginPagerAdapter adapter = new LoginPagerAdapter(viewPager.getContext(), pagerItems);
        viewPager.setAdapter(adapter);

        viewPager.setScrollDuration();
    }

    @BindingAdapter({"paddingTop"})
    public static void setStatusBarTopPadding(View view, float topPadding) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setPadding(0, (int) topPadding, 0, 0);
        }

    }

    @BindingAdapter({"textCaps"})
    public static void setText(View view, String value) {

        if (view instanceof TextView)
            ((TextView) view).setText(value.toUpperCase());
        else if (view instanceof Button) {
            ((Button) view).setText(value.toUpperCase());
        }

    }

    @BindingAdapter({"colorFilter"})
    public static void setColorFilter(View view, int color) {

        view.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);

    }

    @BindingAdapter({"viewSelected"})
    public static void setSelected(TextView view, boolean status) {
        view.setSelected(status);
    }

    /**
     * Set the bitmap as background of a view
     * @param view over to set the bitmap
     * @param path Path of the file which will converted to bitmap.
     */
    @BindingAdapter({"fileSrc"})
    public static void setBackgroundFromFilePath(ImageView view, String path) {

        DisplayMetrics metrics = view.getContext().getResources().getDisplayMetrics();

        int targetW = metrics.widthPixels / 3;
        int targetH = metrics.widthPixels / 3;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        view.setImageBitmap(bitmap);

    }

}
