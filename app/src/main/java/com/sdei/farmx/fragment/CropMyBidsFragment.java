package com.sdei.farmx.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sdei.farmx.R;
import com.sdei.farmx.apimanager.ApiManager;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.databinding.FragmentMyBidsBinding;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.CropBid;
import com.sdei.farmx.dataobject.FilterIndex;
import com.sdei.farmx.dataobject.RecyclerBindingList;
import com.sdei.farmx.helper.AppInstance;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;

public class CropMyBidsFragment extends CropFragment
        implements View.OnClickListener, ApiServiceCallback, RecyclerCallback {

    private int INVALID_RECORDS = 0;

    private FilterIndex filterIndex;
    private RecyclerBindingList<CropBid> bindingList;

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
        FragmentMyBidsBinding binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_my_bids, container, false);
        filterIndex = new FilterIndex();
        binding.setFilterIndex(filterIndex);
        binding.setHandler(CropMyBidsFragment.this);

        bindingList = new RecyclerBindingList<>();
        bindingList.setItemsList(new ArrayList<CropBid>());
        binding.setItem(bindingList);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        changeAppHeader(CropMyBidsFragment.this, getString(R.string.my_bids));
        getUserBids();

    }

    private void getUserBids() {

        if (AppUtils.isNetworkAvailable(activityContext, true, 101)
                && AppInstance.appUser != null) {

            ApiManager.callService(
                    activityContext,
                    AppConstants.API_INDEX.USER_BIDS,
                    CropMyBidsFragment.this,
                    true,
                    AppInstance.appUser.getId(),
                    getUserAccessToken());

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.active_rl:
                filterIndex.setFilterIndex(0);
                break;

            case R.id.expired_rl:
                filterIndex.setFilterIndex(1);
                break;

            case R.id.add_crop_fab:
                openAddCropPage(false, null);
                break;

        }

    }

    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (!AppUtils.isSessionExpired(activityContext, response)) {

            if (response.isSuccess()) {

                if (apiIndex == AppConstants.API_INDEX.USER_BIDS) {

                    ArrayList<CropBid> userBids = AppUtils.getParsedDataArrayFromResponse(response, CropBid.class);

                    if (userBids != null
                            && userBids.size() > 0) {

                        ArrayList<CropBid> dataList = bindingList.getItemsList();

                        for (int i = 0; i < userBids.size(); i++) {

                            CropBid obj = userBids.get(i);
                            Crop cropObj = AppUtils.getParsedData(obj.getCrop(), Crop.class);

                            if (cropObj != null) {
                                obj.setCropObj(cropObj);
                                dataList.add(obj);
                            } else {
                                INVALID_RECORDS = INVALID_RECORDS + 1;
                            }

                        }

                        bindingList.setItemsList(dataList);

                    }

                }

            } else if (response.getError() != null
                    && !TextUtils.isEmpty(response.getError().getMessage())) {

                Toast.makeText(activityContext, response.getError().getMessage(), Toast.LENGTH_SHORT).show();

            }

        }

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onException(int apiIndex, Throwable t) {

        if (showErrorToast(CropMyBidsFragment.this, t.getMessage()))
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

        openCropDetailPage(bindingList.getItemsList().get(position).getCropObj().getId());

    }

    @Override
    public void onChildItemClick(int parentIndex, int childIndex) {

    }

}
