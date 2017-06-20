package com.sdei.farmx.binding;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sdei.farmx.R;
import com.sdei.farmx.adapter.MyPagerAdapter;
import com.sdei.farmx.adapter.MyRecyclerAdapter;
import com.sdei.farmx.adapter.NothingSelectedSpinnerAdapter;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.callback.RecyclerMoreActionCallback;
import com.sdei.farmx.customview.CustomPager;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.Equipment;
import com.sdei.farmx.dataobject.FarmInput;
import com.sdei.farmx.dataobject.NavigationDrawerItem;
import com.sdei.farmx.dataobject.RecyclerBindingList;
import com.sdei.farmx.dataobject.TabItem;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.helper.AppInstance;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class MyBindingsAdapter {

    /**
     * Set the top compound drawable of the TextView
     *
     * @param view   to set compound drawable
     * @param iconId drawable id to set over view
     */
    @BindingAdapter({"drawableTop"})
    public static void setDrawableTop(AppCompatTextView view, int iconId) {

        if (iconId != 0) {

            Drawable drawableTop = AppCompatResources.getDrawable(view.getContext(), iconId);
            view.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);

        }

    }

    @BindingAdapter({"drawableStart"})
    public static void setDrawableStart(View view, int drawableId) {

        if (drawableId != 0) {

            Drawable drawable = AppCompatResources.getDrawable(view.getContext(), drawableId);

            if (view instanceof AppCompatEditText) {

                ((AppCompatEditText) view).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

            } else if (view instanceof AppCompatTextView) {

                ((AppCompatTextView) view).setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

            }

        }

    }

    @BindingAdapter({"drawableEnd"})
    public static void setDrawableEnd(AppCompatTextView view, int drawableId) {

        if (drawableId != 0) {

            Drawable drawable = AppCompatResources.getDrawable(view.getContext(), drawableId);
            view.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);

        }

    }

    @BindingAdapter({"drawableStartFromString"})
    public static void setDrawableStartFromString(AppCompatTextView view, String actionName) {

        Drawable drawable = null;

        if (!TextUtils.isEmpty(actionName)) {
            switch (actionName) {
                case "sort_by":
                    drawable = AppCompatResources.getDrawable(view.getContext(), R.drawable.ic_sort_by);
                    break;
                case "filter":
                    drawable = AppCompatResources.getDrawable(view.getContext(), R.drawable.ic_filter);
                    break;
            }
        }

        view.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

    }

    @BindingAdapter({"loadPagerImage", "type", "requiredHeight"})
    public static void loadPagerImage(ImageView view,
                                      String image,
                                      String type,
                                      float height) {

        DisplayMetrics metrics = view.getContext().getResources().getDisplayMetrics();

        String url = "";

        if (!TextUtils.isEmpty(image)) {
            url = AppConstants.API_URL.IMAGES_BASE_URL + type + "/" + image;
        }

        Glide
                .with(view.getContext())
                .load(url)
                .override(metrics.widthPixels, Math.round(height))
                .error(R.drawable.ic_place_holder_grid)
                .fitCenter()
                .into(view);

    }

    @BindingAdapter({"loadImage", "placeholder", "type"})
    public static void loadImages(ImageView view,
                                  String image,
                                  int placeHolder,
                                  String type) {

        String url = "";

        if (!TextUtils.isEmpty(image)) {
            url = AppConstants.API_URL.IMAGES_BASE_URL + type + "/" + image;
        }

        Glide.with(view.getContext())
                .load(url)
                .centerCrop()
                .error(placeHolder)
                .into(view);

    }

    @BindingAdapter("visibility")
    public static void setVisibility(View view, String value) {

        if (TextUtils.isEmpty(value)) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }

    }

    @BindingAdapter("visibilityBasedOnArrayList")
    public static void setViewVisibility(View view, ArrayList items) {

        if (items == null || items.size() <= 1) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }

    }

    @BindingAdapter("visibilityWithBoolean")
    public static void setVisibilityWithBoolean(View view, boolean status) {

        if (!status) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Set the background drawable
     *
     * @param view       whose background to set
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
     * Set the background drawable
     *
     * @param view       whose background to set
     * @param drawableId drawable to set over the given view
     */
    @BindingAdapter({"svgBackground"})
    public static void setViewSvgBackground(View view, int drawableId) {

        Drawable drawable = AppCompatResources.getDrawable(view.getContext(), drawableId);
        view.setBackground(drawable);

    }

    @BindingAdapter({"imageResource"})
    public static void setImageResource(ImageView view, int drawable) {
        view.setImageResource(drawable);
    }

    /**
     * Set the background drawable or color
     *
     * @param view            to operate on
     * @param drawableId      drawable id to set as background
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

    @BindingAdapter("backgroundColor")
    public static void setBackgroundColor(View view, int color) {
        if (color != 0)
            view.setBackgroundColor(ContextCompat.getColor(view.getContext(), color));
    }

    @BindingAdapter("backgroundParallaxColor")
    public static void setBackgroundParallaxColor(final RelativeLayout view, int id) {

        FrameLayout parent = (FrameLayout) view.getParent();

        final ScrollView scrollView = (ScrollView) parent.findViewById(id);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                int scrollY = scrollView.getScrollY();
                int baseColor = ContextCompat.getColor(view.getContext(), R.color.amber);
                float alpha = Math.min(1, (float) scrollY / view.getHeight());
                view.setBackgroundColor(getColorWithAlpha(alpha, baseColor));

            }
        });

    }

    private static int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        return a + rgb;
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

    @BindingAdapter({"attachToPager"})
    public static void bindViewPagerTabs(final TabLayout view, CustomPager pager) {
        view.setupWithViewPager(pager, true);
    }

    @BindingAdapter({"tabs", "pager"})
    public static void setTabs(TabLayout tabLayout,
                               ArrayList<TabItem> tabsItem,
                               final CustomPager pager) {

        if (tabsItem != null) {

            for (TabItem item : tabsItem) {
                TextView view
                        = (TextView) AppUtils.getLayoutInflater(tabLayout.getContext()).inflate(R.layout.text_tab, tabLayout, false);
                view.setText(item.getValue());
                tabLayout.addTab(tabLayout.newTab().setCustomView(view));
            }

            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        }

        pager.setPagingEnabled(false);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @BindingAdapter({"textCaps"})
    public static void setText(View view, String value) {

        try {

            if (view instanceof TextView)
                ((TextView) view).setText(value.toUpperCase());
            else if (view instanceof Button) {
                ((Button) view).setText(value.toUpperCase());
            }

        } catch (Exception e) {
            e.printStackTrace();
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

    @BindingAdapter({"setImageBitmap"})
    public static void setImageBitmap(final ImageView view, final int bitmapId) {

        final Context context = view.getContext();
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return MediaStore.Images.Thumbnails.getThumbnail(
                        context.getContentResolver(),
                        bitmapId,
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        null);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                view.setImageBitmap(result);
            }

        }.execute();

    }

    @BindingAdapter({"defaultSpinnerAdapter", "selectedItem"})
    public static void setSpinnerAdapter(Spinner spinner,
                                         ArrayList<String> items,
                                         String selectedItem) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(spinner.getContext(),
                R.layout.row_spinner_item,
                items);
        adapter.setDropDownViewResource(R.layout.row_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (!TextUtils.isEmpty(selectedItem)) {

            if (items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).equals(selectedItem)) {
                        spinner.setSelection(i);
                    }
                }
            }

        }

    }

    @BindingAdapter({"spinnerAdapter", "hint", "hintTextColor", "layoutIndex", "selectedItem"})
    public static void setSpinnerAdapterWithHint(Spinner mSpinner,
                                                 ArrayList<String> items,
                                                 String hint,
                                                 int hintTextColor,
                                                 int layoutIndex,
                                                 String selectedItem) {

        int arrayAdapterLayout
                = layoutIndex == 1 ? R.layout.row_spinner_item_1 : R.layout.row_spinner_item;
        int spinnerNothingSelectedLayout
                = layoutIndex == 1 ? R.layout.row_spinner_nothing_selected_1 : R.layout.row_spinner_nothing_selected;

        ArrayAdapter<String> adapter
                = new ArrayAdapter<>(mSpinner.getContext(),
                arrayAdapterLayout,
                items);
        adapter.setDropDownViewResource(R.layout.row_spinner_dropdown_item);
        mSpinner.setAdapter(new NothingSelectedSpinnerAdapter(
                adapter,
                spinnerNothingSelectedLayout,
                hint,
                hintTextColor,
                mSpinner.getContext()));

        if (!TextUtils.isEmpty(selectedItem)) {

            if (items.size() > 0) {
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).equals(selectedItem)) {
                        mSpinner.setSelection(i + 1);
                    }
                }
            }

        }

    }

    @BindingAdapter({"gridAdapter", "layout", "columnNum", "itemClickListener"})
    public static void setGridAdapter(RecyclerView view,
                                      final ArrayList<?> dataList,
                                      int layout,
                                      int columnNum,
                                      RecyclerMoreActionCallback callback) {

        GridLayoutManager mLayoutManager = new GridLayoutManager(view.getContext(), columnNum);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                String type = "";
                if (dataList.get(position) instanceof Equipment) {
                    type = ((Equipment) dataList.get(position)).getType();
                } else if (dataList.get(position) instanceof FarmInput) {
                    type = ((FarmInput) dataList.get(position)).getType();
                } else if (dataList.get(position) instanceof Crop) {
                    type = ((Crop) dataList.get(position)).getType();
                }
                if (!TextUtils.isEmpty(type)
                        && type.equals("header")) {
                    return 3;
                } else {
                    return 1;
                }
            }
        });

        view.setHasFixedSize(true);
        view.setLayoutManager(mLayoutManager);
        view.setItemAnimator(new DefaultItemAnimator());

        MyRecyclerAdapter adapter = new MyRecyclerAdapter(layout, dataList, callback);
        view.setAdapter(adapter);

    }

    @BindingAdapter({"recyclerLinearAdapter", "layout", "onItemClickListener"})
    public static void setRecyclerLinearAdapter(RecyclerView view,
                                                RecyclerBindingList object,
                                                int layout,
                                                RecyclerCallback callback) {

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        view.setLayoutManager(mLayoutManager);
        view.setItemAnimator(new DefaultItemAnimator());

        MyRecyclerAdapter adapter = new MyRecyclerAdapter(layout, object.getItemsList(), callback);
        view.setAdapter(adapter);
        object.setAdapter(adapter);

    }

    @BindingAdapter("buttonTintList")
    public static void setButtonTintList(AppCompatRadioButton button, int selectedColor) {

        int defaultColor = ContextCompat.getColor(button.getContext(), R.color.gray_dark);
        selectedColor = ContextCompat.getColor(button.getContext(), selectedColor);

        ColorStateList colorStateList = new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked}
                }, new int[]{defaultColor, selectedColor}
        );

        CompoundButtonCompat.setButtonTintList(button, colorStateList);

    }

    @SuppressWarnings("deprecation")
    @BindingAdapter("htmlText")
    public static void setHtmlText(TextView textView, String text) {

        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(text);
        }

        textView.setText(result);

    }

    @BindingAdapter({"enable", "requiredStatus"})
    public static void setEnabled(View view, String currentStatus, String requiredStatus) {
        view.setEnabled(currentStatus.equals(requiredStatus));
    }

    @BindingAdapter("currencyText")
    public static void setCurrencyText(TextView view, String currency) {

        if (!TextUtils.isEmpty(currency)) {

            view.setText(AppUtils.getFormattedCurrencyValue(view.getContext(), currency));

        }

    }

    @BindingAdapter({"currencyTextWithPrefix", "prefix", "suffix"})
    public static void setCurrencyTextWithPrefix(TextView view,
                                                 String currency,
                                                 String prefix,
                                                 String suffix) {

        if (!TextUtils.isEmpty(currency)) {

            String value = prefix + AppUtils.getFormattedCurrencyValue(view.getContext(), currency) + suffix;
            view.setText(value);

        }

    }

    @BindingAdapter({"pagerAdapter", "resource", "autoScroll", "type", "handleVisibility"})
    public static void setPagerAdapter(CustomPager viewPager,
                                       ArrayList<?> pagerItems,
                                       int resource,
                                       boolean autoScroll,
                                       String type,
                                       boolean handleVisibility) {
        MyPagerAdapter adapter
                = new MyPagerAdapter(viewPager.getContext(), resource, pagerItems, type);
        viewPager.setAdapter(adapter);
        viewPager.setScrollDuration();

        if (autoScroll && pagerItems != null && pagerItems.size() > 1)
            AppUtils.startPagerTimer(viewPager, pagerItems.size());

        if (handleVisibility && (pagerItems == null || pagerItems.size() == 0)) {
            viewPager.setVisibility(View.GONE);
        } else {
            viewPager.setVisibility(View.VISIBLE);
        }

    }

    @BindingAdapter({"userVisibility", "visibilityType"})
    public static void setVisibilityUsingUser(View view,
                                              String userId,
                                              int visibilityType) {

        if (AppInstance.appUser != null
                && !TextUtils.isEmpty(userId)
                && userId.equals(AppInstance.appUser.getId())) {

            view.setVisibility(visibilityType);

        } else {

            view.setVisibility(View.VISIBLE);

        }

    }

    @BindingAdapter({"textDate",
            "requiredDateFormat",
            "showDefaultValue",
            "prefix"})
    public static void setDateText(TextView view,
                                   String dateValue,
                                   String requiredDateFormat,
                                   boolean showDefaultValue,
                                   String prefix) {

        String value = "";
        if (!TextUtils.isEmpty(dateValue)) {
            value = prefix + AppUtils.getFormattedDate(dateValue, requiredDateFormat);
        } else if (showDefaultValue) {
            value = prefix + "______";
        }
        view.setText(value);
    }

    @BindingAdapter({"textUserAddress"})
    public static void setTextUserAddress(TextView view, User user) {

        String address = AppUtils.getValidUserDisplayAddress(user);
        view.setText(address);

    }

    @BindingAdapter({"textInputAddress"})
    public static void setTextInputAddress(TextView view, FarmInput data) {

        String address = "";

        if (data.getDistrict() != null && !TextUtils.isEmpty(data.getDistrict())) {
            address = data.getDistrict();
        }

        if (data.getState() != null && !TextUtils.isEmpty(data.getState())) {
            if (!TextUtils.isEmpty(address))
                address = address + ", " + data.getState();
            else
                address = data.getState();
        }

        view.setText(address);

    }

}
