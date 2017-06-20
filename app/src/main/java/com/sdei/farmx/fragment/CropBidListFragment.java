package com.sdei.farmx.fragment;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sdei.farmx.R;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.databinding.FragmentCropBidListBinding;
import com.sdei.farmx.dataobject.Category;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.CropBid;
import com.sdei.farmx.dataobject.RecyclerBindingList;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;

public class CropBidListFragment
        extends CropFragment
        implements RecyclerCallback, View.OnClickListener, ApiServiceCallback {

    private FragmentCropBidListBinding binding;

    private Crop cropObj;
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
        binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_crop_bid_list, container, false);
        getArgs();
        bindingList = new RecyclerBindingList<>();
        bindingList.setItemsList(new ArrayList<CropBid>());

        binding.setData(bindingList);
        binding.setHandler(this);
        return binding.getRoot();
    }

    private void getArgs() {

        cropObj = (Crop) getArguments().getSerializable("data");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        changeAppHeader(this, getString(R.string.bids));
        getCropDetail(cropObj.getId(), this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onItemClick(int position) {

        CropBidUserDetailFragment fragment = new CropBidUserDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", cropObj);
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        openFragment(fragment);

    }

    @Override
    public void onChildItemClick(int parentIndex, int childIndex) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.sort_ll:
                break;

        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (!AppUtils.isSessionExpired(activityContext, response)) {

            if (response.isSuccess()) {

                if (apiIndex == AppConstants.API_INDEX.CROP_DETAIL) {

                    cropObj = AppUtils.getParsedData(response.getData(), Crop.class);

                    Category catObj = AppUtils.getParsedData(cropObj.getCategory(), Category.class);
                    cropObj.setCategoryObj(catObj);

                    if (cropObj.getBids() != null && cropObj.getBids().size() > 0) {
                        ArrayList<CropBid> dataList = bindingList.getItemsList();
                        dataList.clear();

                        for (int i = 0; i < cropObj.getBids().size(); i++) {

                            CropBid bid = cropObj.getBids().get(i);
                            User userObj = AppUtils.getParsedData(bid.getUser(), User.class);
                            bid.setUserObj(userObj);
                            dataList.add(bid);

                        }

                        bindingList.setItemsList(dataList);
                        binding.setData(bindingList);
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

        String errorMessage = t.getMessage();

        if (showErrorToast(CropBidListFragment.this, errorMessage))
            Toast.makeText(activityContext, t.getMessage(), Toast.LENGTH_SHORT).show();

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onError(int apiIndex, String message) {
        Toast.makeText(activityContext, "" + message, Toast.LENGTH_SHORT).show();
        AppUtils.hideProgressDialog();
    }

}
