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
import com.sdei.farmx.databinding.FragmentMoreEquipmentsBinding;
import com.sdei.farmx.dataobject.Equipment;
import com.sdei.farmx.dataobject.EquipmentListingResponse;
import com.sdei.farmx.dataobject.RecyclerBindingList;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;

public class EquipmentMoreItemsFragment
        extends EquipmentFragment
        implements ApiServiceCallback, RecyclerMoreActionCallback, View.OnClickListener {

    private EquipmentListingResponse object;
    private RecyclerBindingList<Equipment> bindingList;

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
        FragmentMoreEquipmentsBinding binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_more_equipments, container, false);
        binding.setHandler(this);

        bindingList = new RecyclerBindingList<>();
        bindingList.setItemsList(new ArrayList<Equipment>());
        binding.setItems(bindingList);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        changeAppHeader(this, getString(R.string.equipments));
        API_ITEM_COUNT = 27;
        fetchEquipmentListing(EquipmentMoreItemsFragment.this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.add_equipment_fab:
                openAddEquipmentFragment("sell", null);
                break;

        }

    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(activityContext, "In progress", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChildItemClick(int parentIndex, int childIndex) {

    }

    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (!AppUtils.isSessionExpired(activityContext, response) && response.isSuccess()) {

            if (apiIndex == AppConstants.API_INDEX.EQUIPMENT_LISTING) {

                object = AppUtils.getParsedData(response.getData(), EquipmentListingResponse.class);

                if (object != null
                        && object.getEquipments() != null
                        && object.getEquipments().size() > 0) {

                    ArrayList<Equipment> dataList = bindingList.getItemsList();
                    parseEquipmentListData(dataList, object.getEquipments());
                    bindingList.setItemsList(dataList);

                }

            }

        }

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onException(int apiIndex, Throwable t) {

        String errorMessage = t.getMessage();

        if (showErrorToast(EquipmentMoreItemsFragment.this, errorMessage))
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

    }
}
