package com.sdei.farmx.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sdei.farmx.R;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.callback.DialogCallback;
import com.sdei.farmx.callback.RecyclerMoreActionCallback;
import com.sdei.farmx.databinding.FragmentMyEquipmentsBinding;
import com.sdei.farmx.dataobject.ApiQueryStringItems;
import com.sdei.farmx.dataobject.Equipment;
import com.sdei.farmx.dataobject.EquipmentListingResponse;
import com.sdei.farmx.dataobject.FilterIndex;
import com.sdei.farmx.dataobject.RecyclerBindingList;
import com.sdei.farmx.helper.AppInstance;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;

import static com.sdei.farmx.apimanager.ApiManager.callService;

public class EquipmentMyEquipmentsFragment
        extends EquipmentFragment
        implements View.OnClickListener, ApiServiceCallback, RecyclerMoreActionCallback {

    private FilterIndex filterIndex;
    private FragmentMyEquipmentsBinding binding;

    private RecyclerBindingList<Equipment> bindingList;

    private ArrayList<Equipment> activeItems;

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
                = DataBindingUtil.inflate(inflater, R.layout.fragment_my_equipments, container, false);
        filterIndex = new FilterIndex();
        binding.setFilterIndex(filterIndex);
        binding.setHandler(EquipmentMyEquipmentsFragment.this);

        bindingList = new RecyclerBindingList<>();
        bindingList.setItemsList(new ArrayList<Equipment>());
        binding.setItem(bindingList);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        changeAppHeader(EquipmentMyEquipmentsFragment.this, getString(R.string.my_equipments));
        fetchMyEquipments();

    }

    private void fetchMyEquipments() {

        ApiQueryStringItems apiQueryItem = new ApiQueryStringItems();
        apiQueryItem.count = 25;
        apiQueryItem.page = 1;
        apiQueryItem.search = "";
        apiQueryItem.sortBy = "createdAt desc";
        apiQueryItem.seller = AppInstance.appUser.getId();
        apiQueryItem.list = "";
        getEquipmentListing(apiQueryItem, EquipmentMyEquipmentsFragment.this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.active_rl:
                filterIndex.setFilterIndex(0);
                if (activeItems != null) {
                    ArrayList<Equipment> items = bindingList.getItemsList();
                    items.clear();
                    items.addAll(activeItems);
                    bindingList.setItemsList(items);
                    bindingList.getAdapter().notifyDataSetChanged();
                }
                break;

            case R.id.expired_rl:
                filterIndex.setFilterIndex(1);
                ArrayList<Equipment> items = bindingList.getItemsList();
                activeItems = new ArrayList<>(items);
                items.clear();
                bindingList.setItemsList(items);
                bindingList.getAdapter().notifyDataSetChanged();
                break;

            case R.id.add_equipment_fab:
                openAddEquipmentFragment("add", null);
                break;

        }

    }

    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (!AppUtils.isSessionExpired(activityContext, response)) {

            if (response.isSuccess()) {

                if (apiIndex == AppConstants.API_INDEX.EQUIPMENT_LISTING) {

                    EquipmentListingResponse object = AppUtils.getParsedData(response.getData(), EquipmentListingResponse.class);

                    if (object != null
                            && object.getEquipments() != null
                            && object.getEquipments().size() > 0) {

                        ArrayList<Equipment> dataList = bindingList.getItemsList();
                        parseEquipmentListData(dataList, object.getEquipments());
                        bindingList.setItemsList(dataList);
                        binding.setItem(bindingList);

                    }

                } else if (apiIndex == AppConstants.API_INDEX.DELETE_EQUIPMENT) {

                    Toast.makeText(
                            activityContext,
                            getString(R.string.equipment_delete_successfully),
                            Toast.LENGTH_SHORT).show();
                    removeItemFromBindingObject();

                }

            } else if (response.getError() != null
                    && response.getError().getMessage().equals("Not Found")) {

                Toast.makeText(activityContext, getString(R.string.equipment_not_available), Toast.LENGTH_SHORT).show();

            }

        }

        AppUtils.hideProgressDialog();

    }

    public String getPriceUnitForRentedItem(Equipment object) {

        if(object.getRentSell().equalsIgnoreCase(AppConstants.API_STRING_CONSTANTS.SELL)) {
            return "";
        } else {
            return "/"+object.getPriceUnit();
        }

    }

    private void removeItemFromBindingObject() {

        final ArrayList<Equipment> dataList = bindingList.getItemsList();
        dataList.remove(deletedItemIndex);
        bindingList.getAdapter().notifyItemRemoved(deletedItemIndex);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bindingList.setItemsList(dataList);
                bindingList.getAdapter().notifyDataSetChanged();
            }
        }, 500);

    }

    @Override
    public void onException(int apiIndex, Throwable t) {

        if (showErrorToast(EquipmentMyEquipmentsFragment.this, t.getMessage()))
            Toast.makeText(activityContext, t.getMessage(), Toast.LENGTH_SHORT).show();

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onError(int apiIndex, String message) {
        Toast.makeText(activityContext, "" + message, Toast.LENGTH_SHORT).show();
        AppUtils.hideProgressDialog();
    }

    @Override
    public void onItemClick(int position) {

        Toast.makeText(activityContext, "In progress", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onChildItemClick(int parentIndex, int childIndex) {

    }

    @Override
    public void itemAction(String type, final int position) {

        switch (type) {

            case AppConstants.RECYCLER_ACTION.EDIT:
                openAddEquipmentFragment("edit", bindingList.getItemsList().get(position));
                break;

            case AppConstants.RECYCLER_ACTION.DELETE:
                AppUtils.openPNAlertDialog(
                        activityContext,
                        getString(R.string.alert_delete_equipment),
                        getString(R.string.ok),
                        new DialogCallback() {
                            @Override
                            public void onSuccess() {
                                deleteEquipment(position);
                            }

                            @Override
                            public void onFailure() {

                            }
                        });
                break;

        }

    }

    private int deletedItemIndex;

    private void deleteEquipment(int position) {

        if(isNetworkAvailable()) {

            String equipmentId = bindingList.getItemsList().get(position).getId();
            deletedItemIndex = position;
            callService(activityContext,
                    AppConstants.API_INDEX.DELETE_EQUIPMENT,
                    EquipmentMyEquipmentsFragment.this,
                    true,
                    equipmentId,
                    getUserAccessToken());

        }

    }

    public void refreshItemsList(boolean fetchDataFromServer) {

        if(fetchDataFromServer) {
            ArrayList<Equipment> dataList = bindingList.getItemsList();
            dataList.clear();
            bindingList.setItemsList(dataList);
            fetchMyEquipments();
        } else if(bindingList.getAdapter() != null) {
            bindingList.getAdapter().notifyDataSetChanged();
        }

    }


}
