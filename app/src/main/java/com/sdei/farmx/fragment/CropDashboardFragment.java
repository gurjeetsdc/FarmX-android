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
import com.sdei.farmx.databinding.FragmentBuyCropBinding;
import com.sdei.farmx.dataobject.ApiQueryStringItems;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.CropListingResponse;
import com.sdei.farmx.dataobject.RecyclerBindingList;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;

public class CropDashboardFragment extends CropFragment
        implements View.OnClickListener, ApiServiceCallback, RecyclerMoreActionCallback {

    private final int SINGLE_ROW_ITEM_COUNT = 3;

    private CropListingResponse object;
    private RecyclerBindingList<Crop> bindingList;

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

        FragmentBuyCropBinding binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_buy_crop, container, false);

        binding.setHandler(CropDashboardFragment.this);

        bindingList = new RecyclerBindingList<>();
        bindingList.setItemsList(new ArrayList<Crop>());
        binding.setItems(bindingList);

        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        changeAppHeader(this, getString(R.string.crops));
        fetchCropListing();

    }

    public void fetchCropListing() {

        ArrayList<Crop> dataList = bindingList.getItemsList();
        dataList.clear();
        bindingList.setItemsList(dataList);

        ApiQueryStringItems apiQueryItem = new ApiQueryStringItems();
        apiQueryItem.count = SINGLE_ROW_ITEM_COUNT;
        apiQueryItem.page = 1;
        apiQueryItem.search = "";
        apiQueryItem.sortBy = "createdAt desc";
        //apiQueryItem.seller = getUserId();
        apiQueryItem.seller = "";
        apiQueryItem.list = "guest";
        getCropsListing(apiQueryItem, CropDashboardFragment.this);

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
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (!AppUtils.isSessionExpired(activityContext, response) && response.isSuccess()) {

            if (apiIndex == AppConstants.API_INDEX.CROP_LISTING) {

                object = AppUtils.getParsedData(response.getData(), CropListingResponse.class);

                if (object != null
                        && object.getCrops() != null && object.getCrops().size() > 0) {

                    ArrayList<Crop> dataList = bindingList.getItemsList();

                    if (object.getTotal() > SINGLE_ROW_ITEM_COUNT) {
                        Crop headerObject = new Crop();
                        headerObject.setId(null);
                        headerObject.setName(getString(R.string.more_crops));
                        headerObject.setType("header");
                        headerObject.setTotal(object.getTotal() - SINGLE_ROW_ITEM_COUNT);
                        dataList.add(0, headerObject);
                    }

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

        if (showErrorToast(CropDashboardFragment.this, errorMessage))
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

        if (bindingList.getItemsList().get(position).getType().equals("header")) {

            CropMoreItemsFragment fragment = new CropMoreItemsFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("total", bindingList.getItemsList().get(position).getTotal());
            fragment.setArguments(bundle);

            openFragment(fragment);

        } else {

            openCropDetailPage(bindingList.getItemsList().get(position).getId());

        }

    }

    @Override
    public void onChildItemClick(int parentIndex, int childIndex) {

    }

    @Override
    public void itemAction(String type, int position) {

    }
}
