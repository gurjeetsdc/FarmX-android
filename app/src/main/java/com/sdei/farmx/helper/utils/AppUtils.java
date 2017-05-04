package com.sdei.farmx.helper.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.sdei.farmx.R;
import com.sdei.farmx.dataobject.NavigationDrawerItem;
import com.sdei.farmx.dataobject.SingleSelectionItem;

import java.util.ArrayList;

public class AppUtils {

    private static LayoutInflater inflater;
    private static Dialog networkAlertDialog = null;

    private static int deviceApiVersion() {
        return Build.VERSION.SDK_INT;
    }

    public static int getColor(Context context, int id) {

        if (deviceApiVersion() >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }

    }

    public static Drawable getDrawable(Context context, int id) {

        if (deviceApiVersion() >= 23) {
            return context.getResources().getDrawable(id, context.getTheme());
        } else {
            return context.getResources().getDrawable(id);
        }

    }

    public static void setStatusBarTint(Context context, int color) {

        int color2 = getColor(context, color);
        setStatusBarColor(context, color2);

    }

    private static void setStatusBarColor(Context context, int color) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = ((Activity) context).getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            color = getColorWithAlpha(color, 0.4f);
            window.setStatusBarColor(color);
        }

    }

    private static int getColorWithAlpha(int color, float fraction) {

        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        r = darkenColor(r, fraction);
        g = darkenColor(g, fraction);
        b = darkenColor(b, fraction);
        int alpha = Color.alpha(color);

        return Color.argb(alpha, r, g, b);

    }

    private static int darkenColor(int color, double fraction) {
        return (int) Math.max(color - (color * fraction), 0);
    }

    public static LayoutInflater getLayoutInflater(Context context) {

        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        return inflater;

    }

    public static ArrayList<NavigationDrawerItem> getNavigationItemsList(Context context) {

        ArrayList<NavigationDrawerItem> itemsList = new ArrayList<>();

        itemsList.add(AppUtils.getNavigationDrawerItem(0, 0,
                context.getString(R.string.my_account),
                R.drawable.ic_nav_my_account,
                true, null,
                false));

        itemsList.add(AppUtils.getNavigationDrawerItem(1, 0,
                context.getString(R.string.crops),
                R.drawable.ic_nav_crop,
                false,
                getNavChildItems(context, 1),
                false));
        itemsList.add(AppUtils.getNavigationDrawerItem(2, 0,
                context.getString(R.string.farm_inputs),
                R.drawable.ic_nav_farm_inputs,
                false, getNavChildItems(context, 2),
                false));
        itemsList.add(AppUtils.getNavigationDrawerItem(3, 0,
                context.getString(R.string.farm_equipments),
                R.drawable.ic_nav_farm_equipement,
                false, getNavChildItems(context, 3),
                false));
        itemsList.add(AppUtils.getNavigationDrawerItem(4, 0,
                context.getString(R.string.lease_land),
                R.drawable.ic_nav_land,
                false, getNavChildItems(context, 4),
                false));
        itemsList.add(AppUtils.getNavigationDrawerItem(5, 0,
                context.getString(R.string.services),
                R.drawable.ic_nav_services,
                true, getNavChildItems(context, 5),
                false));

        itemsList.add(AppUtils.getNavigationDrawerItem(6, 0,
                context.getString(R.string.my_orders),
                R.drawable.ic_nav_my_order,
                false, null, false));
        itemsList.add(AppUtils.getNavigationDrawerItem(7, 0,
                context.getString(R.string.my_bookings),
                R.drawable.ic_nav_my_bookings,
                false, null, false));
        itemsList.add(AppUtils.getNavigationDrawerItem(8, 0,
                context.getString(R.string.notifications),
                R.drawable.ic_nav_notification,
                true, null, false));

        itemsList.add(AppUtils.getNavigationDrawerItem(9, 0,
                context.getString(R.string.settings),
                R.drawable.ic_nav_settings,
                false, null, false));
        itemsList.add(AppUtils.getNavigationDrawerItem(10, 0,
                context.getString(R.string.contact_us),
                R.drawable.ic_nav_contact_us,
                false, null, false));
        itemsList.add(AppUtils.getNavigationDrawerItem(11, 0,
                context.getString(R.string.sign_out),
                R.drawable.ic_nav_logout,
                false, null, false));

        return itemsList;

    }

    private static ArrayList<NavigationDrawerItem> getNavChildItems(Context context,
                                                                    int parentPosition) {

        ArrayList<NavigationDrawerItem> childItems = new ArrayList<>();

        switch (parentPosition) {

            case 1:
                childItems.add(AppUtils.getNavigationDrawerItem(0,
                        parentPosition,
                        context.getString(R.string.my_crops),
                        R.drawable.ic_nav_my_crop,
                        false, null, false));
                childItems.add(AppUtils.getNavigationDrawerItem(1,
                        parentPosition,
                        context.getString(R.string.my_bids),
                        R.drawable.ic_nav_my_bid,
                        false, null, false));
                break;

        }

        return childItems;

    }

    private static NavigationDrawerItem getNavigationDrawerItem(int index,
                                                                int parentPosition,
                                                                String itemName,
                                                                int drawable,
                                                                boolean showDivider,
                                                                ArrayList<NavigationDrawerItem> childItems,
                                                                boolean checked) {

        NavigationDrawerItem object = new NavigationDrawerItem();

        object.setIndex(index);
        object.setParentPosition(parentPosition);
        object.setItemName(itemName);
        object.setDrawable(drawable);
        object.setShowDivider(showDivider);
        object.setChildItems(childItems);
        object.setChecked(checked);

        return object;

    }

    /**
     * This method helps in opening fragment with help of FragmentManager object
     *
     * @param targetFragment Contains Fragment to open
     * @param manager        FragmentManager objects helps in opening Target Fragment
     */
    public static void openFragment(FragmentManager manager, Fragment targetFragment) {

        try {

            String fragmentName = targetFragment.getClass().getName();
            AppConstants.FragmentConstant.fragment = targetFragment;
            manager.popBackStack();
            manager.beginTransaction()
                    .replace(R.id.main_fragment, targetFragment, fragmentName)
                    .addToBackStack(fragmentName)
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * This method helps in opening fragment with help of FragmentManager object
     *
     * @param targetFragment Contains Fragment to open
     * @param manager        FragmentManager objects helps in opening Target Fragment
     */
    public static void openFragmentWithoutPopBack(FragmentManager manager,
                                                  Fragment targetFragment) {

        try {

            String fragmentName = targetFragment.getClass().getName();
            AppConstants.FragmentConstant.fragment = targetFragment;
            manager.beginTransaction()
                    .add(R.id.main_fragment, targetFragment, fragmentName)
                    .addToBackStack(fragmentName)
                    .commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void finishFragment(FragmentManager manager, Fragment fragment) {

        try {

            if (manager != null) {

                removeFragmentFromBackStack(manager, fragment);

                int count = manager.getBackStackEntryCount();
                count = count - 2;

                if (count >= 0) {
                    FragmentManager.BackStackEntry backEntry
                            = manager.getBackStackEntryAt(count);
                    String str = backEntry.getName();
                    AppConstants.FragmentConstant.fragment = manager.findFragmentByTag(str);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void removeFragmentFromBackStack(FragmentManager manager, Fragment fragment) {

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();
        manager.popBackStack();

    }

    public static boolean isNetworkAvailable(Context context,
                                             boolean showNetworkAlert,
                                             int action) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean status = (activeNetworkInfo != null && activeNetworkInfo.isConnected());

        if (!status && showNetworkAlert) {
            showNetworkAlert(context, action);
        }

        return status;

    }

    /**
     * This method is used to check whether app is connected to network or not
     *
     * @param context contains context
     * @param action  contains action, if it is 404 thin finish activity
     */
    private static void showNetworkAlert(final Context context, final int action) {

        try {

            if (context != null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage(context.getString(R.string.network_not_available))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (action == 404)
                                    ((Activity) context).finish();

                                networkAlertDialog = null;

                            }
                        });

                networkAlertDialog = builder.create();
                networkAlertDialog.show();

                networkAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        networkAlertDialog = null;
                    }
                });

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static SingleSelectionItem getSingleSelectionItem(int index,
                                                             String key,
                                                             String itemName,
                                                             int rightDrawable) {

        SingleSelectionItem object = new SingleSelectionItem();

        object.setIndex(index);
        object.setKey(key);
        object.setItemName(itemName);
        object.setRightDrawable(rightDrawable);

        return object;

    }

    public static boolean hasPermission(Context context, String permission) {

        return android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                ||
                ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;

    }

}
