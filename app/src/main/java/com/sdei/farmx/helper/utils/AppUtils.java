package com.sdei.farmx.helper.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sdei.farmx.R;
import com.sdei.farmx.activity.AppActivity;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.DialogCallback;
import com.sdei.farmx.callback.NetworkErrorCallback;
import com.sdei.farmx.customview.CustomPager;
import com.sdei.farmx.dataobject.NavigationDrawerItem;
import com.sdei.farmx.dataobject.SingleSelectionItem;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.db.DBSource;
import com.sdei.farmx.helper.AppInstance;
import com.sdei.farmx.helper.preferences.MyPreference;
import com.sdei.farmx.helper.preferences.PreferenceConstant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtils {

    private static LayoutInflater inflater;
    private static Dialog networkAlertDialog = null;
    private static ProgressDialog pDialog = null;

    public static void showProgressDialog(Context context) {

        if (pDialog == null) {
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(context.getString(R.string.please_wait));
            pDialog.show();
            pDialog.setCancelable(false);
            pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    pDialog = null;
                }
            });
        }

    }

    public static void showProgressDialog(Context context, String message) {

        if (pDialog == null) {
            pDialog = new ProgressDialog(context);
            pDialog.setMessage(message);
            pDialog.show();
            pDialog.setCancelable(false);
            pDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    pDialog = null;
                }
            });
        }

    }

    public static void hideProgressDialog() {

        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }

    }

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

        return ContextCompat.getDrawable(context, id);

//        if (deviceApiVersion() >= 23) {
//            return context.getResources().getDrawable(id, context.getTheme());
//        } else {
//            return context.getResources().getDrawable(id);
//        }

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

    public static ArrayList<NavigationDrawerItem> getNavigationItemsList(ArrayList<NavigationDrawerItem> itemsList, Context context) {

        itemsList.add(AppUtils.getNavigationDrawerItem(0, 0,
                context.getString(R.string.my_account),
                context.getString(R.string.key_my_account),
                R.drawable.ic_nav_my_account,
                true, null,
                false));

        itemsList.add(AppUtils.getNavigationDrawerItem(1, 0,
                context.getString(R.string.crops),
                context.getString(R.string.key_crops),
                R.drawable.ic_nav_crop,
                false,
                getNavChildItems(context, 1),
                false));
        itemsList.add(AppUtils.getNavigationDrawerItem(2, 0,
                context.getString(R.string.farm_inputs),
                context.getString(R.string.key_farm_inputs),
                R.drawable.ic_nav_farm_inputs,
                false, getNavChildItems(context, 2),
                false));
        itemsList.add(AppUtils.getNavigationDrawerItem(3, 0,
                context.getString(R.string.my_equipments),
                context.getString(R.string.key_equipments),
                R.drawable.ic_nav_farm_equipement,
                false, getNavChildItems(context, 3),
                false));
        itemsList.add(AppUtils.getNavigationDrawerItem(4, 0,
                context.getString(R.string.lands),
                context.getString(R.string.key_land),
                R.drawable.ic_nav_land,
                false, getNavChildItems(context, 4),
                false));
        itemsList.add(AppUtils.getNavigationDrawerItem(5, 0,
                context.getString(R.string.services),
                context.getString(R.string.key_services),
                R.drawable.ic_nav_services,
                true, getNavChildItems(context, 5),
                false));

        itemsList.add(AppUtils.getNavigationDrawerItem(6, 0,
                context.getString(R.string.my_orders),
                context.getString(R.string.key_my_orders),
                R.drawable.ic_nav_my_order,
                false, null, false));
        itemsList.add(AppUtils.getNavigationDrawerItem(7, 0,
                context.getString(R.string.my_bookings),
                context.getString(R.string.key_my_bookings),
                R.drawable.ic_nav_my_bookings,
                false, null, false));
        itemsList.add(AppUtils.getNavigationDrawerItem(8, 0,
                context.getString(R.string.notifications),
                context.getString(R.string.key_notifications),
                R.drawable.ic_nav_notification,
                true, null, false));

        itemsList.add(AppUtils.getNavigationDrawerItem(9, 0,
                context.getString(R.string.settings),
                context.getString(R.string.key_settings),
                R.drawable.ic_nav_settings,
                false, null, false));
        itemsList.add(AppUtils.getNavigationDrawerItem(10, 0,
                context.getString(R.string.contact_us),
                context.getString(R.string.key_contact_us),
                R.drawable.ic_nav_contact_us,
                false, null, false));

        if (isUserLoggedIn(context)) {

            itemsList.add(AppUtils.getNavigationDrawerItem(11, 0,
                    context.getString(R.string.log_out),
                    context.getString(R.string.key_log_out),
                    R.drawable.ic_nav_logout,
                    false, null, false));

        } else {

            itemsList.add(AppUtils.getNavigationDrawerItem(11, 0,
                    context.getString(R.string.login),
                    context.getString(R.string.key_loging),
                    R.drawable.ic_nav_login,
                    false, null, false));

        }

        return itemsList;

    }

    public static boolean isUserLoggedIn(Context context) {
        return MyPreference.getBoolean(context, PreferenceConstant.USER_LOGGED_IN);
    }

    public static void initUserObject(Context context) {

        if (AppInstance.appUser == null) {
            AppInstance.appUser = DBSource.getInstance(context).getUser();
        }

    }

    public static void logoutUser(Context context) {

        String lang = getAppLanguage(context);

        AppInstance.appUser = null;
        MyPreference.clearPreferenceData(context);
        LoginManager.getInstance().logOut();
        AppUtils.clearDatabase(context);

        MyPreference.saveString(context, PreferenceConstant.APP_LANGUAGE, lang);

    }

    private static ArrayList<NavigationDrawerItem> getNavChildItems(Context context,
                                                                    int parentPosition) {

        ArrayList<NavigationDrawerItem> childItems = new ArrayList<>();

        switch (parentPosition) {

            case 1:
                childItems.add(AppUtils.getNavigationDrawerItem(0,
                        parentPosition,
                        context.getString(R.string.my_crops),
                        context.getString(R.string.key_my_crops),
                        R.drawable.ic_nav_my_crop,
                        false, null, false));
                childItems.add(AppUtils.getNavigationDrawerItem(1,
                        parentPosition,
                        context.getString(R.string.my_bids),
                        context.getString(R.string.key_my_bids),
                        R.drawable.ic_nav_my_bid,
                        false, null, false));
                break;

        }

        return childItems;

    }

    private static NavigationDrawerItem getNavigationDrawerItem(int index,
                                                                int parentPosition,
                                                                String itemName,
                                                                String key,
                                                                int drawable,
                                                                boolean showDivider,
                                                                ArrayList<NavigationDrawerItem> childItems,
                                                                boolean checked) {

        NavigationDrawerItem object = new NavigationDrawerItem();

        object.setKey(key);
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
    public static void openHomeFragment(FragmentManager manager, Fragment targetFragment) {

        try {

            String fragmentName = targetFragment.getClass().getName();
            AppConstants.FragmentConstant.fragment = targetFragment;
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

    public static void clearFragmentStack(FragmentManager manager) {

        if (manager != null) {

            for (int i = 0; i < manager.getBackStackEntryCount() - 1; i++) {

                finishFragment(manager, AppConstants.FragmentConstant.fragment);
                i = i - 1;

            }

        }

    }

    public static void finishFragment(FragmentManager manager, Fragment fragment) {

        try {

            if (manager != null) {

                removeFragmentFromBackStack(manager, fragment);
                int count = manager.getBackStackEntryCount();
                count = count - 1;

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

        manager.popBackStackImmediate();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        transaction.commit();

    }

    public static void openPNAlertDialog(Context context,
                                         String message,
                                         String positiveLabel,
                                         final DialogCallback callback) {

        createAlertDialog(
                context,
                null,
                message,
                positiveLabel,
                true,
                callback);

    }

    public static void openPNAlertDialogWithTitle(Context context,
                                                  String title,
                                                  String message,
                                                  String positiveLabel,
                                                  final DialogCallback callback) {

        createAlertDialog(
                context,
                title,
                message,
                positiveLabel,
                true,
                callback);

    }

    public static void openPAlertDialog(Context context,
                                        String message,
                                        String positiveLabel,
                                        final DialogCallback callback) {

        createAlertDialog(
                context,
                null,
                message,
                positiveLabel,
                false,
                callback);

    }

    public static void openPAlertDialogWithTitle(final Context context,
                                                 String title,
                                                 String message,
                                                 String pLabel,
                                                 final DialogCallback callback) {

        createAlertDialog(
                context,
                title,
                message,
                pLabel,
                false,
                callback);

    }

    private static void createAlertDialog(
            Context context,
            String title,
            String message,
            String positiveLabel,
            boolean showNegativeButton,
            final DialogCallback callback) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        builder.setMessage(message);
        builder.setCancelable(false);

        builder.setPositiveButton(positiveLabel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                        callback.onSuccess();

                    }
                });

        if (showNegativeButton) {

            builder.setNegativeButton(context.getString(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                            callback.onFailure();

                        }
                    });

        }

        builder.show();

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

    public static boolean isNetworkAvailable(Context context,
                                             NetworkErrorCallback callback) {

        boolean status = hasNetworkConnection(context);

        if (!status) {
            showNetworkAlert(context, callback);
        }

        return status;

    }

    public static boolean hasNetworkConnection(Context context) {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());

    }

    /**
     * This method is used to check whether app is connected to network or not
     *
     * @param context  contains context
     * @param callback callback to handle alert action on screen
     */
    private static void showNetworkAlert(
            final Context context,
            final NetworkErrorCallback callback) {

        try {

            if (context != null) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setMessage(context.getString(R.string.network_not_available))
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                callback.onNetworkYesClicked();
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

    public static void hideKeyboard(Activity activity) {
        try {

            View view = activity.getWindow().getCurrentFocus();

            if (view != null && view.getWindowToken() != null) {

                IBinder binder = view.getWindowToken();

                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binder, 0);

            }

        } catch (NullPointerException e) {

            e.printStackTrace();

        }
    }

    public static void showTextInputError(TextInputLayout textInputLayout, String message) {
        textInputLayout.setErrorEnabled(true);
        textInputLayout.setError(message);
    }

    public static void addTextChangedListener(final EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.length() > 0) {
                        TextInputLayout layout = (TextInputLayout) editText.getParentForAccessibility();
                        if (layout != null) {
                            layout.setError(null);
                            layout.setErrorEnabled(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

    }

    /**
     * This method  validates entered email according to EMAIL_PATTERN regular expression.
     *
     * @param email email to validate
     * @return true, if email is valid ,else returns false
     */
    public static boolean validateEmail(String email) {

        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,4})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    public static boolean validateString(String value) {
        final String EMAIL_PATTERN = "^[a-zA-Z ]+$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static <T> T getParsedData(Object object, Class<T> valueType) {

        if (object != null) {

            try {

                ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = writer.writeValueAsString(object);
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(json, valueType);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    public static <T> ArrayList<T> getParsedDataArrayFromResponse(ApiResponse response,
                                                                  final Class<T> valueType) {

        try {

            ObjectWriter writer = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = writer.writeValueAsString(response.getData());
            return getArrayListFromJson(json, valueType);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> ArrayList<T> getArrayListFromJson(String json, Class<T> valueType) {

        try {

            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(
                    json,
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, valueType));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    public static void showSpinnerErrorMessage(Spinner spinner, String message) {

        TextView errorText = (TextView) spinner.getSelectedView();
        errorText.setTextColor(Color.RED);
        errorText.setText(message);
        spinner.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

    }

    /**
     * This method helps in clearing local sqlite database
     *
     * @param context contains context
     */
    private static void clearDatabase(Context context) {

        try {

            DBSource db = DBSource.getInstance(context);
            db.clearDatabase();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean isSessionExpired(Context context,
                                           ApiResponse response) {

        boolean status = (response.getError() != null
                && response.getError().getCode() == 401
                && response.getError().getMessage().equalsIgnoreCase("authorization"));

        if (status) {
            showSessionExpiredAlert(context);
        }

        return status;
    }

    private static void showSessionExpiredAlert(final Context context) {

        AppUtils.logoutUser(context);
        openPAlertDialog(
                context,
                context.getString(R.string.session_expire_message),
                context.getString(R.string.ok),
                new DialogCallback() {
                    @Override
                    public void onSuccess() {
                        ((AppActivity) context).openLoginActivity(
                                context,
                                "session_expired",
                                AppConstants.PendingTask.SESSION_EXPIRED);
                    }

                    @Override
                    public void onFailure() {

                    }
                });

    }

    public static String getAppLanguage(Context context) {
        return MyPreference.getString(context, PreferenceConstant.APP_LANGUAGE);
    }

    public static String getBase64ForRequestedFile(Context context, String path) {

        byte[] array = getBitmapByteArray(context, path);

        if (array != null)
            return Base64.encodeToString(getBitmapByteArray(context, path), Base64.NO_WRAP);
        else
            return null;

    }

    private static byte[] getBitmapByteArray(Context context, String path) {

        //Bitmap bm = BitmapFactory.decodeFile(path);
        Bitmap bm = ImageUtils.getInstant().getCompressedBitmap(context, path);

        if (bm != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            return baos.toByteArray();
        }

        return null;

    }

    public static void startPagerTimer(final CustomPager viewPager, final int size) {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentSelection = viewPager.getCurrentItem();

                if (currentSelection < size - 1) {
                    viewPager.setCurrentItem(currentSelection + 1);
                } else {
                    viewPager.setCurrentItem(0);
                }

                startPagerTimer(viewPager, size);

            }
        }, 2000);

    }

    public static String getCurrentUtcTime() {
        SimpleDateFormat f = getSimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        f.setTimeZone(TimeZone.getTimeZone("UTC"));
        System.out.println(f.format(new Date()));
        return (f.format(new Date()));
    }

    public static int validMobileNumber(String number) {

        if (TextUtils.isEmpty(number)
                || number.isEmpty()) {
            return 1;
        } else if (Long.parseLong(number) == 0) {
            return 2;
        } else if (number.length() != 10) {
            return 3;
        }

        return 0;
    }

    public static String getFormattedCurrencyValue(Context context, String currency) {

        NumberFormat nf = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) nf).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) nf).setDecimalFormatSymbols(decimalFormatSymbols);
        String value = nf.format(Double.parseDouble(currency)).trim();
        return context.getString(R.string.rupee_symbol) + " " + value;

    }

    public static SimpleDateFormat getSimpleDateFormat(String format) {
        return new SimpleDateFormat(format, Locale.ENGLISH);
    }

    public static String getFormattedDate(String dateValue,
                                          String requiredDateFormat) {

        try {

            SimpleDateFormat requiredDFormat = AppUtils.getSimpleDateFormat(requiredDateFormat);

            SimpleDateFormat[] currentDateFormats = AppUtils.getSimpleDateFormats();
            Date currentDate = null;

            for (SimpleDateFormat format : currentDateFormats) {

                try {
                    currentDate = format.parse(dateValue);
                    break;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            return requiredDFormat.format(currentDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    private static SimpleDateFormat[] getSimpleDateFormats() {
        return new SimpleDateFormat[]{getSimpleDateFormat(AppConstants.DATE_FORMAT.FORMAT_1),
                getSimpleDateFormat(AppConstants.DATE_FORMAT.FORMAT_2),
                getSimpleDateFormat(AppConstants.DATE_FORMAT.FORMAT_3),
                getSimpleDateFormat(AppConstants.DATE_FORMAT.FORMAT_4),
                getSimpleDateFormat(AppConstants.DATE_FORMAT.FORMAT_5)};
    }

    public static double getQuantityInRequiredUnit(String value, String selectedUnit, String requiredUnit) {

        double val = Double.parseDouble(value);

        if (requiredUnit.equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.KG)) {

            switch (selectedUnit) {

                case AppConstants.API_STRING_CONSTANTS.GRAMS:
                case AppConstants.API_STRING_CONSTANTS.GRAM:
                    return (val * 0.001);

                case AppConstants.API_STRING_CONSTANTS.QUINTAL:
                    return (val * 100);

                case AppConstants.API_STRING_CONSTANTS.TONNES:
                case AppConstants.API_STRING_CONSTANTS.TONNE:
                    return (val * 1000);

            }

        } else if (requiredUnit.equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.GRAMS)
                || requiredUnit.equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.GRAM)) {

            switch (selectedUnit) {

                case AppConstants.API_STRING_CONSTANTS.KG:
                    return (val * 1000);

                case AppConstants.API_STRING_CONSTANTS.QUINTAL:
                    return (val * 100000);

                case AppConstants.API_STRING_CONSTANTS.TONNES:
                case AppConstants.API_STRING_CONSTANTS.TONNE:
                    return (val * 1000000);

            }

        } else if (requiredUnit.equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.QUINTAL)) {

            switch (selectedUnit) {

                case AppConstants.API_STRING_CONSTANTS.KG:
                    return (val * 0.01);

                case AppConstants.API_STRING_CONSTANTS.TONNES:
                case AppConstants.API_STRING_CONSTANTS.TONNE:
                    return (val * 10);

                case AppConstants.API_STRING_CONSTANTS.GRAMS:
                case AppConstants.API_STRING_CONSTANTS.GRAM:
                    return (val / 100000);

            }

        } else if (requiredUnit.equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.TONNES)
                || requiredUnit.equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.TONNE)) {

            switch (selectedUnit) {

                case AppConstants.API_STRING_CONSTANTS.KG:
                    return (val * 0.001);

                case AppConstants.API_STRING_CONSTANTS.QUINTAL:
                    return (val * 0.1);

                case AppConstants.API_STRING_CONSTANTS.GRAMS:
                case AppConstants.API_STRING_CONSTANTS.GRAM:
                    return (val / 1000000);

            }

        }

        return 0;

    }

    public static String[] removeElement(String[] original, int element) {
        String[] n = new String[original.length - 1];
        System.arraycopy(original, 0, n, 0, element);
        System.arraycopy(original, element + 1, n, element, original.length - element - 1);
        return n;
    }

    public static String getValidUserDisplayAddress(User user) {

        String address = "";

        if (user != null) {

            String district = getValidValue(user.getDistrict());
            String state = getValidValue(user.getState());

            if (!district.equals("") && !state.equals(""))
                address = district + ", " + state;
            else if (district.equals(""))
                address = state;
            else if (state.equals(""))
                address = district;

        }

        return address;

    }

    private static String getValidValue(String value) {

        if (!TextUtils.isEmpty(value) && !value.isEmpty()) {
            return value;
        }

        return "";

    }

}
