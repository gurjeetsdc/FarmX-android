package com.sdei.farmx.helper.utils;

import android.support.v4.app.Fragment;

import com.sdei.farmx.BuildConfig;

public class AppConstants {

    public static class API_URL {
        public static String IMAGES_BASE_URL = BuildConfig.BASE_URL + "images/";
    }

    public static class FragmentConstant {

        public static Fragment fragment;

    }

    public static class SocialEvent {
        public static final String FACEBOOK = "facebook";
        public static final String GOOGLE = "google";
    }

    public static class Notification {
        public static final int NOTIFICATION_ID = 0;
        public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
        public static final String REGISTRATION_COMPLETE = "registrationComplete";
        public static final String PUSH_NOTIFICATION = "pushNotification";
    }

    public static class AppPermission {
        public static final int NO_ACTION = 0;
        public static final int ACTION_OPEN_CAMERA = 101;
        public static final int ACTION_OPEN_GALLERY = 102;
    }

    public static class CustomGallery {

        public static final int MAXIMUM_IMAGES = 5;

        public static final String ACTION_PICK = "farmx.ACTION_PICK";
        public static final String ACTION_MULTIPLE_PICK = "farmx.ACTION_MULTIPLE_PICK";
    }

    public class API_INDEX {

        public static final int STATE = 111;
        public static final int REGISTER = 112;
        public static final int LOGIN = 113;
        public static final int VARIFY_OTP = 114;
        public static final int CATEGORIES = 115;
        public static final int ADD_CROP = 116;
        public static final int SOCIAL_REGISTER = 117;
        public static final int CROP_LISTING = 118;
        public static final int DELETE_CROP = 119;
        public static final int UPDATE_CROP = 120;
        public static final int INPUT_LISTING = 121;
        public static final int UPLOAD_IMAGE = 122;
        public static final int CROP_BID = 123;
        public static final int CROP_DETAIL = 124;
        public static final int USER_DETAIL = 125;
        public static final int LANGUAGE = 126;
        public static final int CROP_ACCEPT_BID = 127;
        public static final int INPUT_DETAIL = 128;
        public static final int EQUIPMENT_LISTING = 129;
        public static final int ADD_EQUIPMENT = 130;
        public static final int UPDATE_EQUIPMENT = 131;
        public static final int MANUFACTURER = 132;
        public static final int UPDATE_CROP_BID = 133;
        public static final int DELETE_EQUIPMENT = 134;
        public static final int USER_BIDS = 135;
        public static final int MY_CROP_LISTING = 136;
        public static final int ADD_TO_CART = 137;

    }

    public class API_STRING_CONSTANTS {

        public static final String YES = "Yes";
        public static final String NO = "No";

        public static final String COD = "COD";
        public static final String CHEQUE = "Cheque";
        public static final String NET_BANKING = "Net Banking";

        static final String GRAMS = "Grams";
        static final String GRAM = "Gram";
        static final String KG = "Kg";
        static final String QUINTAL = "Quintal";
        static final String TONNES = "Tonnes";
        static final String TONNE = "Tonne";
        public static final String COUNT = "Count";
        public static final String DOZEN = "Dozen";

        public static final String RENT = "rent";
        public static final String SELL = "sell";

    }

    public static class PendingTask {

        public static final int NO_TASK = 10;
        public static final int ADD_CROP = 11;
        public static final int MY_CROPS = 12;
        public static final int SESSION_EXPIRED = 13;
        public static final int CROP_DETAIL = 14;
        public static final int MY_BIDS = 15;
        public static final int ADD_EQUIPMENT = 16;
        public static final int MY_EQUIPMENTS = 17;
        public static final int EQUIPMENT_DETAIL = 18;

    }

    public class IMAGES {
        public static final String CROPS = "crops";
        public static final String EQUIPMENTS = "equipments";
    }

    public class CATEGORY_TYPE {
        public static final String CROPS = "crops";
        public static final String EQUIPMENTS = "equipments";
    }

    public class DATE_FORMAT {
        static final String FORMAT_1 = "dd/MM/yyyy";
        public static final String FORMAT_2 = "dd MMM yyyy";
        public static final String FORMAT_3 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        public static final String FORMAT_4 = "hh:mm a";
        static final String FORMAT_5 = "yyyy-MMM-dd hh:mm:ss";

    }

    public class RECYCLER_ACTION {
        public static final String EDIT = "edit";
        public static final String DELETE = "delete";
        public static final String BIDS = "bids";
        public static final String ADD_TO_CART = "add_to_cart";
    }

    public class CART_PRODUCT {
        public static final String INPUT = "INPUT";
    }

}
