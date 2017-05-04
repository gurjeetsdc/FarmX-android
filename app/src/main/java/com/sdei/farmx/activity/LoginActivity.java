package com.sdei.farmx.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sdei.farmx.R;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.databinding.ActivityLoginBinding;

import java.util.ArrayList;

public class LoginActivity extends AppSocialLoginActivity implements View.OnClickListener {

    private User user;

    /**
     * Called when the activity is first created.
     * This is where we do all of our normal static set up: create views,
     * bind data to lists, etc.
     * @param savedInstanceState containing the activity's previously frozen state, if there was one.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = DataBindingUtil.setContentView(LoginActivity.this,
                R.layout.activity_login);

        ArrayList<String> pagerItems = new ArrayList<>();
        user = new User();
        setPageItemsArrayList(pagerItems);

        binding.setUser(user);
        binding.setItems(pagerItems);

        bindData();

    }

    /**
     * In this we are registering facebook callbacks and initializing GoogleApiClient
     */
    private void bindData() {

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

    }

    private void setPageItemsArrayList(ArrayList<String> pagerItems) {

        pagerItems.add("Farmers earn more with\nbuy/sell and rent/serve");
        pagerItems.add("second item");
        pagerItems.add("third item");

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.register_tv:
                openActivity(LoginActivity.this, RegisterActivity.class);
                break;

            case R.id.forgot_tv:

                break;

            case R.id.login_facebook_ll:
                connectToFacebook(LoginActivity.this);
                break;

            case R.id.login_google_ll:
                googleSignIn();
                break;

        }

    }

    @Override
    public void afterSocialLoginSuccess(User user) {

        this.user.setEmail(user.getEmail());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

}
