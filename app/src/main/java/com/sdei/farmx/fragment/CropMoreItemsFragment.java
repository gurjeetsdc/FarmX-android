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
import com.sdei.farmx.databinding.FragmentMoreCropBinding;
import com.sdei.farmx.dataobject.ApiQueryStringItems;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.CropListingResponse;
import com.sdei.farmx.dataobject.RecyclerBindingList;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;

public class CropMoreItemsFragment
        extends CropFragment
        implements View.OnClickListener, RecyclerMoreActionCallback, ApiServiceCallback {

    private final int API_ITEM_COUNT = 27;

    private CropListingResponse object;
    private RecyclerBindingList<Crop> bindingList;

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

        FragmentMoreCropBinding binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_more_crop, container, false);
        binding.setHandler(CropMoreItemsFragment.this);
        bindingList = new RecyclerBindingList<>();
        bindingList.setItemsList(new ArrayList<Crop>());
        binding.setItems(bindingList);
        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        totalItems = getArguments().getInt("total");
        changeAppHeader(this, getString(R.string.crops));
        fetchCropListing();

    }

    public void fetchCropListing() {

        ArrayList<Crop> dataList = bindingList.getItemsList();
        dataList.clear();
        bindingList.setItemsList(dataList);

        ApiQueryStringItems apiQueryItem = new ApiQueryStringItems();
        apiQueryItem.count = API_ITEM_COUNT;
        apiQueryItem.page = 1;
        apiQueryItem.search = "";
        apiQueryItem.sortBy = "createdAt desc";
        //apiQueryItem.seller = getUserId();
        apiQueryItem.seller = "";
        apiQueryItem.list = "guest";

        getCropsListing(apiQueryItem, CropMoreItemsFragment.this);

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

            case R.id.add_crop_fab:
                openAddCropPage(false, null);
                break;

        }

    }

    @Override
    public void onItemClick(int position) {

        openCropDetailPage(bindingList.getItemsList().get(position).getId());

    }

    @Override
    public void onChildItemClick(int parentIndex, int childIndex) {

    }

    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (response.isSuccess()) {

            if (apiIndex == AppConstants.API_INDEX.CROP_LISTING) {

                object = AppUtils.getParsedData(response.getData(), CropListingResponse.class);

                if (object != null
                        && object.getCrops() != null
                        && object.getCrops().size() > 0) {

                    ArrayList<Crop> dataList = bindingList.getItemsList();
                    parseCropListData(dataList, object.getCrops());
                    bindingList.setItemsList(dataList);

                }

            }

        }

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onException(int apiIndex, Throwable t) {

        String errorMessage = t.getMessage();

        if (showErrorToast(CropMoreItemsFragment.this, errorMessage))
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
