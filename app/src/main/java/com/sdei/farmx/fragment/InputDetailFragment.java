package com.sdei.farmx.fragment;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.sdei.farmx.R;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.callback.DialogCallback;
import com.sdei.farmx.customview.CustomPager;
import com.sdei.farmx.databinding.FragmentFarmInputDetailBinding;
import com.sdei.farmx.databinding.RowPagerInputDetailBinding;
import com.sdei.farmx.databinding.RowPagerTermsAndConditionsBinding;
import com.sdei.farmx.databinding.RowPagerUserDetailBinding;
import com.sdei.farmx.dataobject.FarmInput;
import com.sdei.farmx.dataobject.TabItem;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;
import java.util.Arrays;

public class InputDetailFragment
        extends InputFragment
        implements ApiServiceCallback, View.OnClickListener {

    private CustomPager pager;

    private FragmentFarmInputDetailBinding binding;
    private static FarmInput inputObj;

    private String weightUnits[];
    private boolean manuallySelectedSpinnerValue = false;

    private Spinner quantityUnitSpinner;
    private EditText quantityEt;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_farm_input_detail, container, false);
        getArgs();

        pager = binding.pager;
        quantityUnitSpinner = binding.quantityUnitSpinner;
        quantityEt = binding.quantityEt;

        binding.setData(inputObj);
        weightUnits = filterWeightUnits();
        binding.setWeightUnits(new ArrayList<>(Arrays.asList(weightUnits)));
        binding.setHandler(InputDetailFragment.this);
        binding.setTabs(getTabItems());
        bindData();
        return binding.getRoot();
    }

    private void bindData() {

        String quantityUnit = inputObj.getQuantityUnit();
        inputObj.setSelectedQuantityUnit(quantityUnit);

    }

    private void getArgs() {

        inputObj = (FarmInput) getArguments().getSerializable("data");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (inputObj.getName() != null && inputObj.getName() != null)
            changeAppHeader(this, inputObj.getName());

        setViewListeners();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.pay_now_tv:
                Toast.makeText(activityContext, "In progress", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_first_ll:
                validateUserInputValue();
                break;

            case R.id.action_second_ll:
                Toast.makeText(activityContext, "In progress", Toast.LENGTH_SHORT).show();
                break;

            case R.id.left_arrow_ll:
                int index1 = pager.getCurrentItem();
                if (index1 > 0) {
                    index1 = index1 - 1;
                    pager.setCurrentItem(index1);
                }
                break;

            case R.id.right_arrow_ll:
                int index2 = pager.getCurrentItem();
                if (index2 < inputObj.getImages().size()) {
                    index2 = index2 + 1;
                    pager.setCurrentItem(index2);
                }
                break;

        }

    }

    private void validateUserInputValue() {

        if (!isUserLoggedIn()) {

            showLoginAlertMessage(AppConstants.PendingTask.EQUIPMENT_DETAIL);

        } else if (TextUtils.isEmpty(inputObj.getRequiredQuantity())
                || inputObj.getRequiredQuantity().isEmpty()
                || Long.parseLong(inputObj.getRequiredQuantity()) == 0) {
            Toast.makeText(activityContext, getString(R.string.error_enter_quantity), Toast.LENGTH_SHORT).show();
            quantityEt.requestFocus();
        } else {

            String selectedUnit = inputObj.getSelectedQuantityUnit();
            String availableQuantityUnit = inputObj.getQuantityUnit();

            double requiredQuantity;

            if (TextUtils.isEmpty(inputObj.getRequiredQuantity())
                    || inputObj.getRequiredQuantity().isEmpty()
                    || Integer.parseInt(inputObj.getRequiredQuantity()) == 0) {
                requiredQuantity = 0;
                inputObj.setTotalPrice("0");
            } else if (selectedUnit.equalsIgnoreCase(availableQuantityUnit)) {
                requiredQuantity = Double.parseDouble(inputObj.getRequiredQuantity());
            } else {
                requiredQuantity = AppUtils.getQuantityInRequiredUnit(inputObj.getRequiredQuantity(), selectedUnit, availableQuantityUnit);
            }

            double availableQuantity = Double.parseDouble(inputObj.getQuantity());

            if (requiredQuantity <= availableQuantity) {

                Toast.makeText(activityContext, "In progress", Toast.LENGTH_SHORT).show();

            } else {

                AppUtils.openPAlertDialog(
                        activityContext,
                        getString(R.string.enter_more_quantity),
                        getString(R.string.ok),
                        new DialogCallback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onFailure() {

                            }
                        });

            }

        }

    }

    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (!AppUtils.isSessionExpired(activityContext, response)) {

            if (response.isSuccess()) {

                if (apiIndex == AppConstants.API_INDEX.INPUT_DETAIL) {

                    inputObj = AppUtils.getParsedData(response.getData(), FarmInput.class);
                    binding.setData(inputObj);
                    changeAppHeader(this, inputObj.getName());

                    AppUtils.hideProgressDialog();

                }

            } else if (response.getError() != null
                    && !TextUtils.isEmpty(response.getError().getMessage())) {

                Toast.makeText(activityContext, response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                AppUtils.hideProgressDialog();

            }

        }

    }

    @Override
    public void onException(int apiIndex, Throwable t) {

        String errorMessage = t.getMessage();

        if (showErrorToast(null, errorMessage))
            Toast.makeText(activityContext, t.getMessage(), Toast.LENGTH_SHORT).show();

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onError(int apiIndex, String message) {
        Toast.makeText(activityContext, "" + message, Toast.LENGTH_SHORT).show();
        AppUtils.hideProgressDialog();
    }

    @BindingAdapter({"customInputDetailLayout"})
    public static void setCustomView(LinearLayout parent, String type) {

        parent.removeAllViews();

        if (type.equals(parent.getContext().getString(R.string.key_input_address_detail))) {

            RowPagerInputDetailBinding binding
                    = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_pager_input_detail, parent, false);
            binding.setData(inputObj);
            parent.addView(binding.getRoot());

        } else if (type.equals(parent.getContext().getString(R.string.key_terms_and_conditions))) {

            RowPagerTermsAndConditionsBinding binding
                    = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_pager_terms_and_conditions, parent, false);
            binding.setTermsAndConditions("Terms and Conditions");
            parent.addView(binding.getRoot());

        } else if (type.equals(parent.getContext().getString(R.string.key_seller_profile))) {

            RowPagerUserDetailBinding binding
                    = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_pager_user_detail, parent, false);
            binding.setData(inputObj.getUser());
            parent.addView(binding.getRoot());

        }

    }

    public boolean validAddress(User object) {
        return (!TextUtils.isEmpty(object.getDistrict()) || !TextUtils.isEmpty(object.getState()));
    }

    private String[] filterWeightUnits() {

        if (inputObj.getQuantityUnit().equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.COUNT)
                || inputObj.getQuantityUnit().equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.DOZEN)) {

            return new String[]{inputObj.getQuantityUnit()};

        } else {

            int index = 0;

            String units[] = getResources().getStringArray(R.array.input_weight_units);

            for (int i = 0; i < units.length; i++) {

                if (!TextUtils.isEmpty(inputObj.getQuantityUnit())
                        && inputObj.getQuantityUnit().equals(units[i])) {
                    index = i;
                }


            }

            String[] items = new String[index + 1];
            System.arraycopy(units, 0, items, 0, index + 1);
            return items;

        }

    }

    private ArrayList<TabItem> getTabItems() {

        ArrayList<TabItem> items = new ArrayList<>();

        TabItem item = new TabItem();
        item.setKey(getString(R.string.key_seller_profile));
        item.setValue(getString(R.string.seller_profile));
        items.add(item);

        item = new TabItem();
        item.setKey(getString(R.string.key_input_address_detail));
        item.setValue(getString(R.string.input_detail));
        items.add(item);

        item = new TabItem();
        item.setKey(getString(R.string.key_terms_and_conditions));
        item.setValue(getString(R.string.terms_and_conditions));
        items.add(item);

        return items;

    }

    private void setViewListeners() {

        setOnTouchListener(quantityUnitSpinner);

        quantityUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (manuallySelectedSpinnerValue
                        && weightUnits != null
                        && position >= 0 && position < weightUnits.length) {
                    manuallySelectedSpinnerValue = false;
                    inputObj.setSelectedQuantityUnit(weightUnits[position]);
                    getTotalAmountValue(inputObj.getRequiredQuantity());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        quantityEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                getTotalAmountValue(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void getTotalAmountValue(String typedValue) {

        if (!TextUtils.isEmpty(typedValue)) {
            inputObj.setRequiredQuantity(typedValue);
        } else {
            inputObj.setRequiredQuantity("");
        }

        String selectedUnit = inputObj.getSelectedQuantityUnit();
        String priceUnit = inputObj.getPriceUnit();

        double requiredQuantity;

        if (TextUtils.isEmpty(inputObj.getRequiredQuantity())
                || inputObj.getRequiredQuantity().isEmpty()
                || Integer.parseInt(inputObj.getRequiredQuantity()) == 0) {
            requiredQuantity = 0;
            inputObj.setTotalPrice("0");
        } else if (selectedUnit.equalsIgnoreCase(priceUnit)) {
            requiredQuantity = Double.parseDouble(inputObj.getRequiredQuantity());
        } else {
            requiredQuantity = AppUtils.getQuantityInRequiredUnit(inputObj.getRequiredQuantity(), selectedUnit, priceUnit);
        }

        double totalPrice = requiredQuantity * Double.parseDouble(inputObj.getPrice());
        inputObj.setTotalPrice(String.valueOf(totalPrice));

    }

    private void setOnTouchListener(final Spinner spinner) {

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                manuallySelectedSpinnerValue = true;
                return false;

            }
        });

    }

}
