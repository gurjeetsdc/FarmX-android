package com.sdei.farmx.service;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppLogger;
import com.sdei.farmx.helper.utils.NotificationUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String TAG = "FirebaseMsgingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            AppLogger.log(TAG,
                    "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            AppLogger.log(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }

        }

    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            Intent pushNotification = new Intent(AppConstants.Notification.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            // play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();
        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {

        AppLogger.log(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            AppLogger.log(TAG, "title: " + title);
            AppLogger.log(TAG, "message: " + message);
            AppLogger.log(TAG, "isBackground: " + isBackground);
            AppLogger.log(TAG, "payload: " + payload.toString());
            AppLogger.log(TAG, "imageUrl: " + imageUrl);
            AppLogger.log(TAG, "timestamp: " + timestamp);

            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());

            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(AppConstants.Notification.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                notificationUtils.playNotificationSound();

            } else {

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    imageUrl = null;
                }

                notificationUtils.showNotificationMessage(title, message, timestamp, imageUrl);

            }

        } catch (JSONException e) {
            AppLogger.log(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            AppLogger.log(TAG, "Exception: " + e.getMessage());
        }
    }

}
