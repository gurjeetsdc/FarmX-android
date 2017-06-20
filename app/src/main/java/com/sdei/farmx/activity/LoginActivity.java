package com.sdei.farmx.activity;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sdei.farmx.R;
import com.sdei.farmx.adapter.MyPagerAdapter;
import com.sdei.farmx.apimanager.ApiManager;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.customview.CustomPager;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.databinding.ActivityLoginBinding;
import com.sdei.farmx.db.DBSource;
import com.sdei.farmx.helper.AppInstance;
import com.sdei.farmx.helper.preferences.MyPreference;
import com.sdei.farmx.helper.preferences.PreferenceConstant;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppLogger;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class LoginActivity extends AppSocialLoginActivity
        implements View.OnClickListener, ApiServiceCallback {

    private User user;
    private GoogleApiClient mGoogleApiClient;

    private EditText userNameEt;
    private EditText passwordEt;
    private int REQUEST_CODE = 101;

    /**
     * Called when the activity is first created.
     * This is where we do all of our normal static set up: create views,
     * bind data to lists, etc.
     *
     * @param savedInstanceState containing the activity's previously frozen state, if there was one.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = DataBindingUtil.setContentView(LoginActivity.this,
                R.layout.activity_login);

        user = new User();

        binding.setUser(user);

        String messages[] = getResources().getStringArray(R.array.advertisement_messages);
        binding.setItems(new ArrayList<>(Arrays.asList(messages)));

        userNameEt = binding.usernameEt;
        passwordEt = binding.passwordEt;

        bindData();

    }

    /**
     * In this we are registering facebook callbacks and initializing GoogleApiClient
     */
    private void bindData() {

        AppUtils.setStatusBarTint(this, R.color.amber);

        callbackManager = CallbackManager.Factory.create();
        registerFacebookCallback(callbackManager, facebookCallback);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        AppUtils.addTextChangedListener(userNameEt);
        AppUtils.addTextChangedListener(passwordEt);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.register_tv:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;

            case R.id.forgot_tv:

                break;

            case R.id.login_facebook_ll:
                connectToFacebook(LoginActivity.this);
                break;

            case R.id.login_google_ll:
                googleSignIn(mGoogleApiClient);
                break;

            case R.id.login_btn_tv:
                AppUtils.hideKeyboard(LoginActivity.this);
                validateParams();
                break;

        }

    }

    @Override
    public void afterSocialLoginSuccess(User user) {

        if (user != null && user.getProviders() != null) {

            HashMap<String, String> map = new HashMap<>();
            this.user.setProviders(user.getProviders());

            if (user.getProviders().equals(AppConstants.SocialEvent.FACEBOOK)) {

                map.put("fbId", user.getFbId());

            } else {

                map.put("gId", user.getgId());

            }

            map.put("domain", user.getDomain());
            map.put("username", user.getUsername());
            map.put("providers", user.getProviders());
            map.put("deviceToken", MyPreference.getString(LoginActivity.this, PreferenceConstant.FIREBASE_REG_ID));
            map.put("client_id", getString(R.string.app_client_id));
            map.put("firstName", user.getFirstName());
            map.put("lastName", user.getLastName());

            String json;
            try {
                json = new ObjectMapper().writeValueAsString(map);
                AppLogger.log("", "" + json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            loginSocialUser(map);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {

            startActivity(getIntent());
            finishActivity(LoginActivity.this);

        } else if (requestCode == RC_SIGN_IN) {

            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);

        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (response.isSuccess()) {

            if (apiIndex == AppConstants.API_INDEX.LOGIN
                    || apiIndex == AppConstants.API_INDEX.SOCIAL_REGISTER) {

                try {

                    if (response.getData() != null) {

                        User user = AppUtils.getParsedData(response.getData(), User.class);

                        if (user != null) {
                            DBSource db = DBSource.getInstance(LoginActivity.this);
                            db.saveUser(user);
                            AppInstance.appUser = null;
                            initUserObject();
                        }

                    }

                    MyPreference.saveBoolean(LoginActivity.this, PreferenceConstant.USER_LOGGED_IN, true);
                    setResult(RESULT_OK);
                    finishActivity(LoginActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else if (response.getError() != null
                && response.getError().getMessage().equalsIgnoreCase(getString(R.string.st_msg))
                && TextUtils.isEmpty(user.getProviders())) {

            AppUtils.showTextInputError(
                    (TextInputLayout) passwordEt.getParentForAccessibility(),
                    getString(R.string.validate_wrong_password));

        } else if (response.getError() != null
                && TextUtils.isEmpty(user.getProviders())
                && response.getError().getMessage().equalsIgnoreCase("User invaild! Please try Again")) {

            AppUtils.showTextInputError(
                    (TextInputLayout) userNameEt.getParentForAccessibility(),
                    getString(R.string.validate_wrong_login_username));

        } else if (response.getError() != null) {

            Toast.makeText(LoginActivity.this, response.getError().getMessage(), Toast.LENGTH_SHORT).show();

        }

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onException(int apiIndex, Throwable t) {

        Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        AppUtils.hideProgressDialog();

    }

    @Override
    public void onError(int apiIndex, String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
        AppUtils.hideProgressDialog();
    }

    @Override
    public void onBackPressed() {

        if (getIntent().hasExtra("action")
                && getIntent().getStringExtra("action").equalsIgnoreCase("session_expired")) {
            return;
        }
        super.onBackPressed();
    }

    private void loginSocialUser(HashMap<String, String> map) {

        if (isNetworkAvailable(LoginActivity.this, true, 101)) {

            ApiManager.callService(
                    LoginActivity.this,
                    AppConstants.API_INDEX.SOCIAL_REGISTER,
                    LoginActivity.this,
                    true,
                    map,
                    null);

        }

    }

    private void validateParams() {

        if (TextUtils.isEmpty(user.getUsername())
                || user.getUsername().isEmpty()) {

            AppUtils.showTextInputError(
                    (TextInputLayout) userNameEt.getParentForAccessibility(),
                    getString(R.string.validate_empty_email));

        } else if (!AppUtils.validateEmail(user.getUsername())) {

            AppUtils.showTextInputError(
                    (TextInputLayout) userNameEt.getParentForAccessibility(),
                    getString(R.string.validate_login_username));

        } else if (user.getUsername().contains("@") && !AppUtils.validateEmail(user.getUsername())) {

            AppUtils.showTextInputError(
                    (TextInputLayout) userNameEt.getParentForAccessibility(),
                    getString(R.string.validate_email));

        } else if (TextUtils.isEmpty(user.getPassword())
                || user.getPassword().isEmpty()) {

            AppUtils.showTextInputError(
                    (TextInputLayout) passwordEt.getParentForAccessibility(),
                    getString(R.string.validate_password));

        } else if (user.getPassword().length() < 8) {

            AppUtils.showTextInputError(
                    (TextInputLayout) passwordEt.getParentForAccessibility(),
                    getString(R.string.validate_password_via_length));

        } else {

            loginUser();

        }

    }

    private void loginUser() {

        HashMap<String, String> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("password", user.getPassword());
        map.put("deviceToken", MyPreference.getString(LoginActivity.this, PreferenceConstant.FIREBASE_REG_ID));
        map.put("client_id", getString(R.string.app_client_id));

        callLoginUserApi(map);

    }

    private void callLoginUserApi(HashMap<String, String> map) {

        if (AppUtils.isNetworkAvailable(LoginActivity.this, true, 101)) {

            ApiManager.callService(
                    LoginActivity.this,
                    AppConstants.API_INDEX.LOGIN,
                    LoginActivity.this,
                    true,
                    map,
                    null);

        }

    }

}
