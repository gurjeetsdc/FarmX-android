package com.sdei.farmx.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.db.DBSource;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppLogger;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Base activity for Login and Registration page
 * will handle Facebook and Google sdk callbacks
 */
abstract class AppSocialLoginActivity extends AppActivity {

    protected static final int RC_SIGN_IN = 9001;

    protected CallbackManager callbackManager;
    protected String[] readPermissions = {"email", "public_profile"};

    FacebookCallback<LoginResult> facebookCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                @Override
                public void onCompleted(JSONObject userInfo, GraphResponse response) {

                    String id = userInfo.optString("id");

                    if (id == null) {
                        return;
                    }

                    User mUser = new User();
                    mUser.setFirstName(userInfo.optString("first_name"));
                    mUser.setLastName(userInfo.optString("last_name"));
                    mUser.setUsername(userInfo.optString("email"));
                    mUser.setFbId(id);
                    mUser.setProviders(AppConstants.SocialEvent.FACEBOOK);

                    DBSource.getInstance(AppSocialLoginActivity.this).saveUserFacebookData(mUser);

                    afterSocialLoginSuccess(mUser);

                }

            });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, first_name, last_name, email, gender"); // Parámetros que pedimos a facebook
            request.setParameters(parameters);
            request.executeAsync();

        }

        @Override
        public void onCancel() {



        }

        @Override
        public void onError(FacebookException error) {

            AppLogger.log("AppSocialLoginActivity", error.getMessage());

        }

    };

    /**
     * Called when user click on Login with Facebook button and
     * will return facebook user data if user already logged in otherwise open the Facebook
     * login screen
     *
     * @param activity current Activity reference
     */
    public void connectToFacebook(Activity activity) {

        if (isNetworkAvailable(activity, true, 0)) {

            AccessToken accessToken = AccessToken.getCurrentAccessToken();

            if (accessToken == null) {

                LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList(readPermissions));

            } else {

                User user = DBSource.getInstance(AppSocialLoginActivity.this).getFacebookUser();

                if (user != null) {
                    afterSocialLoginSuccess(user);
                } else {
                    LoginManager.getInstance().logOut();
                    connectToFacebook(activity);
                }

            }

        }

    }

    /**
     * @param callbackManager manages the callbacks into the FacebookSdk from an Activity's or
     *                        Fragment's onActivityResult() method.
     * @param callback        class for the Facebook SDK
     */
    public void registerFacebookCallback(final CallbackManager callbackManager,
                                         final FacebookCallback<LoginResult> callback) {

        LoginManager.getInstance().registerCallback(callbackManager, callback);

    }

    public abstract void afterSocialLoginSuccess(User user);

    /**
     * Called when user click on Login with Google button and
     * user is prompted to select a Google account to sign in with.
     */
    public void googleSignIn(GoogleApiClient mGoogleApiClient) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Called after user logged in with Google.
     *
     * @param result potentially contain a GoogleSignInAccount details
     */
    public void handleGoogleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            User user = new User();
            user.setFirstName(acct.getGivenName());
            user.setLastName(acct.getFamilyName());
            user.setgId(acct.getId());
            user.setProviders(AppConstants.SocialEvent.GOOGLE);
            user.setUsername(acct.getEmail());
            afterSocialLoginSuccess(user);

        }

    }

}
