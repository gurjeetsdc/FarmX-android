package com.sdei.farmx.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sdei.farmx.R;
import com.sdei.farmx.adapter.NothingSelectedSpinnerAdapter;
import com.sdei.farmx.apimanager.ApiManager;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.dataobject.District;
import com.sdei.farmx.dataobject.State;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.databinding.ActivityRegisterBinding;
import com.sdei.farmx.db.DBSource;
import com.sdei.farmx.helper.preferences.MyPreference;
import com.sdei.farmx.helper.preferences.PreferenceConstant;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterActivity extends AppSocialLoginActivity
        implements View.OnClickListener, ApiServiceCallback {

    private User user;

    private ArrayList<String> states;
    private ArrayList<String> districtsArrayList;
    private ArrayList<State> statesList;

    private Spinner stateSpinner;
    private Spinner districtSpinner;

    private ScrollView scrollView;

    private EditText fNameEt;
    private EditText lNameEt;
    private EditText emailEt;
    private EditText passwordEt;
    private EditText confirmPasswordEt;
    private EditText mobileEt;
    private EditText pincodeEt;

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

        ActivityRegisterBinding binding
                = DataBindingUtil.setContentView(RegisterActivity.this, R.layout.activity_register);
        user = new User();
        binding.setUser(user);

        states = new ArrayList<>();
        districtsArrayList = new ArrayList<>();

        binding.setStates(states);
        binding.setDistricts(districtsArrayList);

        initViews(binding);

    }

    @Override
    protected void onResume() {
        super.onResume();

        bindData();
        setViewListeners();

    }

    private void initViews(ActivityRegisterBinding binding) {

        stateSpinner = binding.stateSpinner;
        districtSpinner = binding.districtSpinner;

        scrollView = binding.scrollView;

        fNameEt = binding.firstNameEt;
        lNameEt = binding.lastNameEt;
        emailEt = binding.emailEt;
        passwordEt = binding.passwordEt;
        confirmPasswordEt = binding.confirmPasswordEt;
        mobileEt = binding.mobileNumberEt;
        pincodeEt = binding.pinEt;

    }

    private void setViewListeners() {

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) view).setTextColor(ContextCompat.getColor(RegisterActivity.this, R.color.white));

                position = position - 1;
                if (position < 0) {
                    position = 0;
                }

                if (userInteracting && position < states.size()) {

                    districtsArrayList.clear();
                    districtSpinner.setSelection(0);
                    user.setState(states.get(position));
                    getDistrictArrayList();

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        districtSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP
                        && TextUtils.isEmpty(user.getState())
                        && districtsArrayList.size() == 1
                        && districtsArrayList.get(0).equals("")) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.please_select_state), Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) view).setTextColor(ContextCompat.getColor(RegisterActivity.this, R.color.white));
                position = position - 1;

                if (position < 0) {
                    position = 0;
                }

                if (userInteracting && position < districtsArrayList.size())
                    user.setDistrict(districtsArrayList.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        AppUtils.addTextChangedListener(fNameEt);
        AppUtils.addTextChangedListener(lNameEt);
        AppUtils.addTextChangedListener(emailEt);
        AppUtils.addTextChangedListener(passwordEt);
        AppUtils.addTextChangedListener(confirmPasswordEt);
        AppUtils.addTextChangedListener(mobileEt);

    }

    private void getDistrictArrayList() {

        for (int i = 0; i < statesList.size(); i++) {

            if (user.getState().equalsIgnoreCase(statesList.get(i).getStateName())) {

                ArrayList<District> districtList = statesList.get(i).getDistricts();

                if (districtList != null && districtList.size() > 0) {

                    for (int j = 0; j < districtList.size(); j++) {
                        districtsArrayList.add(districtList.get(j).getDistrictName());
                    }

                }

            }
        }

        if (districtsArrayList.size() > 0) {
            NothingSelectedSpinnerAdapter adapter
                    = (NothingSelectedSpinnerAdapter) districtSpinner.getAdapter();
            ArrayAdapter<String> sAdapter = adapter.getAdapter();
            sAdapter.notifyDataSetChanged();
        }

    }

    boolean userInteracting = false;

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userInteracting = true;
    }

    private void bindData() {

        AppUtils.setStatusBarTint(this, R.color.amber);

        districtsArrayList.add("");
        statesList = DBSource.getInstance(RegisterActivity.this).getStates();

        if (isNetworkAvailable(RegisterActivity.this, true, 404)
                && (statesList == null || statesList.size() == 0)) {

            getStateListing(this, this);

        } else {

            setSpinnerStatesListing(false);

        }

    }

    private void setSpinnerStatesListing(boolean refreshAdapter) {

        if (statesList != null && statesList.size() > 0) {

            states.clear();

            for (int i = 0; i < statesList.size(); i++) {
                states.add(statesList.get(i).getStateName());
            }

            if (refreshAdapter) {
                NothingSelectedSpinnerAdapter adapter
                        = (NothingSelectedSpinnerAdapter) stateSpinner.getAdapter();
                ArrayAdapter<String> sAdapter = adapter.getAdapter();
                sAdapter.notifyDataSetChanged();
            }

        }

    }

    @Override
    public void afterSocialLoginSuccess(User user) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_ll:
            case R.id.login_tv:
                finishActivity(RegisterActivity.this);
                break;

            case R.id.register_btn_tv:
                AppUtils.hideKeyboard(RegisterActivity.this);
                registerUser();
                break;

        }

    }

    private void registerUser() {

        if (validateParameters() && isNetworkAvailable(RegisterActivity.this, true, 101)) {

            if (user.getPincode() != null && user.getPincode().equals("")) {
                user.setPincode(null);
            }

            ApiManager.callService(
                    RegisterActivity.this,
                    AppConstants.API_INDEX.REGISTER,
                    RegisterActivity.this,
                    true,
                    user,
                    null);

        }

    }

    private boolean validateParameters() {

        trimSpacesFromFields();

        if (TextUtils.isEmpty(user.getFirstName())) {

            AppUtils.showTextInputError(
                    (TextInputLayout) fNameEt.getParentForAccessibility(),
                    getString(R.string.validate_first_name));
            fNameEt.requestFocus();
            scrollView.scrollTo(0, fNameEt.getTop());

        } else if (!AppUtils.validateString(user.getFirstName())) {

            AppUtils.showTextInputError(
                    (TextInputLayout) fNameEt.getParentForAccessibility(),
                    getString(R.string.enter_valid_first_name));
            fNameEt.requestFocus();
            scrollView.scrollTo(0, fNameEt.getTop());

        } else if (TextUtils.isEmpty(user.getLastName())) {

            AppUtils.showTextInputError(
                    (TextInputLayout) lNameEt.getParentForAccessibility(),
                    getString(R.string.validate_last_name));
            lNameEt.requestFocus();

        } else if (!AppUtils.validateString(user.getLastName())) {

            AppUtils.showTextInputError(
                    (TextInputLayout) lNameEt.getParentForAccessibility(),
                    getString(R.string.enter_valid_last_name));
            lNameEt.requestFocus();

        } else if (TextUtils.isEmpty(user.getUsername())
                || user.getUsername().isEmpty()
                || !AppUtils.validateEmail(user.getUsername())) {

            AppUtils.showTextInputError(
                    (TextInputLayout) emailEt.getParentForAccessibility(),
                    getString(R.string.validate_email));
            emailEt.requestFocus();

        } else if (TextUtils.isEmpty(user.getPassword())
                || user.getPassword().isEmpty()) {

            AppUtils.showTextInputError(
                    (TextInputLayout) passwordEt.getParentForAccessibility(),
                    getString(R.string.validate_password));
            passwordEt.requestFocus();

        } else if (user.getPassword().length() < 8) {

            AppUtils.showTextInputError(
                    (TextInputLayout) passwordEt.getParentForAccessibility(),
                    getString(R.string.validate_password_via_length));
            passwordEt.requestFocus();

        } else if (TextUtils.isEmpty(user.getConfirmPassword())
                || user.getConfirmPassword().isEmpty()) {

            AppUtils.showTextInputError(
                    (TextInputLayout) confirmPasswordEt.getParentForAccessibility(),
                    getString(R.string.validate_confirm_password));
            confirmPasswordEt.requestFocus();

        } else if (!user.getPassword().equals(user.getConfirmPassword())) {

            AppUtils.showTextInputError(
                    (TextInputLayout) confirmPasswordEt.getParentForAccessibility(),
                    getString(R.string.message_password_not_matched));
            confirmPasswordEt.requestFocus();

        } else if (AppUtils.validMobileNumber(user.getMobile()) != 0) {

            if (AppUtils.validMobileNumber(user.getMobile()) == 3) {
                AppUtils.showTextInputError(
                        (TextInputLayout) mobileEt.getParentForAccessibility(),
                        getString(R.string.valid_phone_number));
            } else {
                AppUtils.showTextInputError(
                        (TextInputLayout) mobileEt.getParentForAccessibility(),
                        getString(R.string.validate_phone_number));
            }
            mobileEt.requestFocus();

            if (AppUtils.validMobileNumber(user.getMobile()) == 2)
                user.setMobile("");

        } else if (!TextUtils.isEmpty(user.getPincode())
                && !user.getPincode().isEmpty()
                && (Integer.parseInt(user.getPincode()) == 0 || user.getPincode().length() != 6)) {

            AppUtils.showTextInputError(
                    (TextInputLayout) pincodeEt.getParentForAccessibility(),
                    getString(R.string.error_valid_pincode));
            pincodeEt.requestFocus();

            if (Integer.parseInt(user.getPincode()) == 0)
                user.setPincode(null);

        } else {

            ((TextInputLayout) pincodeEt.getParentForAccessibility()).setError(null);
            ((TextInputLayout) pincodeEt.getParentForAccessibility()).setErrorEnabled(false);

            user.setFullName(
                    user.getFirstName() + " " + user.getLastName());
            user.setDeviceToken(
                    MyPreference.getString(RegisterActivity.this, PreferenceConstant.FIREBASE_REG_ID));
            return true;

        }

        return false;
    }

    private void trimSpacesFromFields() {

        if (user.getFirstName() != null) {
            user.setFirstName(user.getFirstName().trim());
        }

        if (user.getLastName() != null) {
            user.setLastName(user.getLastName().trim());
        }

        if (user.getUsername() != null) {
            user.setUsername(user.getUsername().trim());
        }

        if (user.getPassword() != null) {
            user.setPassword(user.getPassword().trim());
        }

        if (user.getConfirmPassword() != null) {
            user.setConfirmPassword(user.getConfirmPassword().trim());
        }

        if (user.getMobile() != null) {
            user.setMobile(user.getMobile().trim());
        }

        if (user.getAddress() != null) {
            user.setAddress(user.getAddress().trim());
        }

        if (user.getCity() != null) {
            user.setCity(user.getCity().trim());
        }

        if (user.getPincode() != null) {
            user.setPincode(user.getPincode().trim());
        }

    }

    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (response != null && response.isSuccess()) {

            if (apiIndex == AppConstants.API_INDEX.STATE) {

                statesList = AppUtils.getParsedDataArrayFromResponse(response, State.class);
                setSpinnerStatesListing(true);
                DBSource.getInstance(RegisterActivity.this).saveStates(statesList);

            } else if (apiIndex == AppConstants.API_INDEX.REGISTER) {

                try {

                    User user = AppUtils.getParsedData(response.getData(), User.class);
                    DBSource db = DBSource.getInstance(RegisterActivity.this);
                    db.saveUser(user);

                    //showOtpEnterDialog();
                    Toast.makeText(
                            RegisterActivity.this,
                            getString(R.string.registered_successfully),
                            Toast.LENGTH_SHORT).show();
                    finishActivity(RegisterActivity.this);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        } else if (response.getError() != null
                && response.getError().getCode() == 301) {

            AppUtils.showTextInputError(
                    (TextInputLayout) emailEt.getParentForAccessibility(),
                    getString(R.string.email_already_exist));
            emailEt.requestFocus();

        } else if (response.getError() != null
                && !TextUtils.isEmpty(response.getError().getMessage())) {

            Toast.makeText(RegisterActivity.this, "" + response.getError().getMessage(), Toast.LENGTH_SHORT).show();

        }

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onException(int apiIndex, Throwable t) {
        if (showErrorToast(RegisterActivity.this, null, t.getMessage()))
            Toast.makeText(RegisterActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

        AppUtils.hideProgressDialog();
    }

    @Override
    public void onError(int apiIndex, String message) {
        Toast.makeText(RegisterActivity.this, "" + message, Toast.LENGTH_SHORT).show();
        AppUtils.hideProgressDialog();
    }

}
