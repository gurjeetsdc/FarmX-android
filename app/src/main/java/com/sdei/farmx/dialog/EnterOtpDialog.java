package com.sdei.farmx.dialog;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sdei.farmx.R;
import com.sdei.farmx.apimanager.ApiManager;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.callback.DialogCallback;
import com.sdei.farmx.databinding.DialogEnterOtpBinding;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.db.DBSource;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

public class EnterOtpDialog extends Dialog implements
        View.OnClickListener,
        ApiServiceCallback {

    private Context context;
    private DialogCallback callback;

    private EditText otpEt;

    private User user;

    public EnterOtpDialog(@NonNull Context context, DialogCallback callback) {
        super(context, android.R.style.Theme_NoTitleBar_Fullscreen);
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DialogEnterOtpBinding binding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.dialog_enter_otp, null, false);
        setContentView(binding.getRoot());
        binding.setHandler(EnterOtpDialog.this);

        otpEt = binding.otpEt;

        DBSource db = DBSource.getInstance(context);
        user = db.getUser();

        TextView view = binding.enterOtp;
        view.setText("Enter the OTP (" + user.getOtp() + ")");

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.submit_tv:
                validateOtp();
                break;

            case R.id.cross_iv:
                dismiss();
                callback.onFailure();
                break;

        }

    }

    private void validateOtp() {

        String value = otpEt.getText().toString();

        if (TextUtils.isEmpty(value)) {
            value = "0";
        }

        if (AppUtils.isNetworkAvailable(context, true, 101)
                && user.getOtp() == Integer.parseInt(value)) {

            ApiManager.callService(
                    context,
                    AppConstants.API_INDEX.VARIFY_OTP,
                    EnterOtpDialog.this,
                    true,
                    null,
                    value);

        } else {

            Toast.makeText(
                    context,
                    context.getString(R.string.enter_valid_otp),
                    Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (response.isSuccess()) {

            if (apiIndex == AppConstants.API_INDEX.VARIFY_OTP) {
                dismiss();
                callback.onSuccess();
            }

        }

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onException(int apiIndex, Throwable t) {

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onError(int apiIndex, String message) {

    }

}
