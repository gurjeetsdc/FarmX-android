package com.sdei.farmx.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.sdei.farmx.R;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppSocialLoginActivity implements View.OnClickListener {

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

        ActivityRegisterBinding binding
                = DataBindingUtil.setContentView(RegisterActivity.this, R.layout.activity_register);

        user = new User();
        binding.setUser(user);

    }

    @Override
    public void afterSocialLoginSuccess(User user) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_ll:
            case R.id.login_tv:
                finish();
                break;

        }

    }

}
