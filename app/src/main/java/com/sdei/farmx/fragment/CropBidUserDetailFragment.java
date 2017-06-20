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
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.callback.DialogCallback;
import com.sdei.farmx.databinding.FragmentCropBidUserDetailBinding;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.CropBid;
import com.sdei.farmx.dataobject.CropBidUpdateResponse;
import com.sdei.farmx.dataobject.TabItem;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sdei.farmx.apimanager.ApiManager.callService;

public class CropBidUserDetailFragment
        extends CropFragment
        implements View.OnClickListener, ApiServiceCallback {

    private Crop object;
    private CropBid cropBid;
    private int userPosition;

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
        FragmentCropBidUserDetailBinding binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_crop_bid_user_detail, container, false);
        getArgs();
        binding.setCropData(object);
        binding.setBidData(cropBid);
        binding.setHandler(this);
        binding.setTabs(getTabItems());

        return binding.getRoot();
    }

    public int isAccepted(String status,
                          int visibility1,
                          int visibility2) {
        if ((!TextUtils.isEmpty(status) && status.equalsIgnoreCase(BID_ACCEPTED))) {
            return visibility1;
        }
        return visibility2;
    }

    private ArrayList<TabItem> getTabItems() {

        String[] tabsItem = getResources().getStringArray(R.array.crop_bid_user_detail_tabs);
        String[] tabsKey = getResources().getStringArray(R.array.crop_bid_user_detail_key_tabs);

        ArrayList<TabItem> items = new ArrayList<>();

        for (int i = 0; i < tabsItem.length; i++) {
            TabItem item = new TabItem();
            item.setKey(tabsKey[i]);
            item.setValue(tabsItem[i]);
            items.add(item);
        }

        return items;

    }

    private void getArgs() {

        object = (Crop) getArguments().getSerializable("data");
        userPosition = getArguments().getInt("position");
        cropBid = object.getBids().get(userPosition);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        changeAppHeader(this, getString(R.string.view_bid));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.accept_ll:

                CropBid acceptedCropBid = hasAlreadyAcceptedBid();

                if (acceptedCropBid != null) {

                    String message = getString(R.string.alert_already_has_accepted_bid);
                    message = message.replace("{{user}}", acceptedCropBid.getUserObj().getFirstName()
                            + " "
                            + acceptedCropBid.getUserObj().getLastName());

                    AppUtils.openPAlertDialog(
                            activityContext,
                            message,
                            getString(R.string.ok),
                            new DialogCallback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onFailure() {

                                }
                            });

                } else {

                    acceptUserBid();

                }

                break;
        }

    }

    private void acceptUserBid() {

        if (isNetworkAvailable()) {

            HashMap<String, String> map = new HashMap<>();
            map.put("status", BID_ACCEPTED);
            map.put("bidId", cropBid.getId());
            callService(
                    activityContext,
                    AppConstants.API_INDEX.CROP_ACCEPT_BID,
                    this,
                    true,
                    map,
                    getUserAccessToken());

        }

    }

    private CropBid hasAlreadyAcceptedBid() {

        if (object.getBids() != null && object.getBids().size() > 0) {

            for (int i = 0; i < object.getBids().size(); i++) {

                if (object.getBids().get(i).getStatus() != null
                        && object.getBids().get(i).getStatus().equalsIgnoreCase(BID_ACCEPTED)) {
                    return object.getBids().get(i);
                }

            }

        }

        return null;

    }

    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (!AppUtils.isSessionExpired(activityContext, response)
                && response.isSuccess()
                && apiIndex == AppConstants.API_INDEX.CROP_ACCEPT_BID) {

            CropBidUpdateResponse updatedCropObj = AppUtils.getParsedData(response.getData(), CropBidUpdateResponse.class);

            ArrayList<CropBid> bids = object.getBids();
            bids.remove(userPosition);
            bids.add(userPosition, updatedCropObj.getBid());
            object.setBids(bids);

            cropBid.setStatus(BID_ACCEPTED);

        } else if (response.getError() != null
                && !TextUtils.isEmpty(response.getError().getMessage())) {

            Toast.makeText(activityContext, response.getError().getMessage(), Toast.LENGTH_SHORT).show();

        }

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onException(int apiIndex, Throwable t) {

        if (showErrorToast(null, t.getMessage()))
            Toast.makeText(activityContext, t.getMessage(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onError(int apiIndex, String message) {

        Toast.makeText(activityContext, "" + message, Toast.LENGTH_SHORT).show();
        AppUtils.hideProgressDialog();

    }

}
