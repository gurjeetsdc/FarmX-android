package com.sdei.farmx.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sdei.farmx.R;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.callback.RecyclerMoreActionCallback;
import com.sdei.farmx.databinding.FragmentMoreInputBinding;
import com.sdei.farmx.dataobject.FarmInput;
import com.sdei.farmx.dataobject.InputListingResponse;
import com.sdei.farmx.dataobject.RecyclerBindingList;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;

public class InputMoreItemsFragment
        extends InputFragment
        implements View.OnClickListener, RecyclerMoreActionCallback, ApiServiceCallback {

    private InputListingResponse object;
    private RecyclerBindingList<FarmInput> bindingList;

    private int totalItems;

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

        FragmentMoreInputBinding binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_more_input, container, false);
        binding.setHandler(InputMoreItemsFragment.this);
        bindingList = new RecyclerBindingList<>();
        bindingList.setItemsList(new ArrayList<FarmInput>());
        binding.setItems(bindingList);
        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        totalItems = getArguments().getInt("total");
        changeAppHeader(this, getString(R.string.inputs));
        fetchInputListing(27, InputMoreItemsFragment.this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.action_first_ll:
                Toast.makeText(activityContext, "In Progress", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_second_ll:
                Toast.makeText(activityContext, "In Progress", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    @Override
    public void onItemClick(int position) {

        openFarmInputDetailPage(bindingList.getItemsList().get(position));

    }

    @Override
    public void onChildItemClick(int parentIndex, int childIndex) {
        Toast.makeText(activityContext, "In Progress", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (response.isSuccess()) {

            if (apiIndex == AppConstants.API_INDEX.INPUT_LISTING) {

                object = AppUtils.getParsedData(response.getData(), InputListingResponse.class);

                if (object != null
                        && object.getInputs() != null
                        && object.getInputs().size() > 0) {

                    ArrayList<FarmInput> dataList = bindingList.getItemsList();
                    dataList.addAll(object.getInputs());
                    bindingList.setItemsList(dataList);

                }

            }

        }

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onException(int apiIndex, Throwable t) {

        String errorMessage = t.getMessage();

        if (showErrorToast(InputMoreItemsFragment.this, errorMessage))
            Toast.makeText(activityContext, t.getMessage(), Toast.LENGTH_SHORT).show();

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onError(int apiIndex, String message) {
        Toast.makeText(activityContext, "" + message, Toast.LENGTH_SHORT).show();
        AppUtils.hideProgressDialog();
    }

    @Override
    public void itemAction(String type, int position) {

        switch (type) {

            case AppConstants.RECYCLER_ACTION.ADD_TO_CART:
                break;

        }

    }

}
