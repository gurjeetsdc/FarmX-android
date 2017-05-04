package com.sdei.farmx.helper.utils;

import android.support.v4.app.Fragment;

public class AppConstants {

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
        public static final String ACTION_PICK = "farmx.ACTION_PICK";
        public static final String ACTION_MULTIPLE_PICK = "farmx.ACTION_MULTIPLE_PICK";
    }

}
