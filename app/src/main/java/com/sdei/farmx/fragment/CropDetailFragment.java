package com.sdei.farmx.fragment;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdei.farmx.R;
import com.sdei.farmx.apimanager.ApiManager;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.customview.CustomPager;
import com.sdei.farmx.databinding.FragmentCropDetailBinding;
import com.sdei.farmx.databinding.RowPagerShippingBinding;
import com.sdei.farmx.databinding.RowPagerTermsAndConditionsBinding;
import com.sdei.farmx.databinding.RowPagerUserDetailBinding;
import com.sdei.farmx.dataobject.Category;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.CropBid;
import com.sdei.farmx.dataobject.ItemDateObject;
import com.sdei.farmx.dataobject.TabItem;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.helper.AppInstance;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppLogger;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sdei.farmx.apimanager.ApiManager.callService;

public class CropDetailFragment
        extends CropFragment
        implements ApiServiceCallback, View.OnClickListener {

    private CustomPager pager;

    private String cropId;

    private FragmentCropDetailBinding binding;
    private static Crop cropObj;

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
                = DataBindingUtil.inflate(inflater, R.layout.fragment_crop_detail, container, false);
        getArgs();
        pager = binding.pager;
        return binding.getRoot();

    }

    private ArrayList<TabItem> getTabItems() {

        String[] tabsItem = getResources().getStringArray(R.array.crop_detail_tabs);
        String[] tabsKey = getResources().getStringArray(R.array.crop_detail_key_tabs);

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

        cropId = getArguments().getString("cropId");

    }

    private double bidPrice = 0;

    private void hasCurrentUserBid() {

        if (cropObj.getBids() == null) {
            cropObj.setBids(new ArrayList<CropBid>());
        }

        ArrayList<CropBid> bids = cropObj.getBids();

        if (bids.size() > 0) {

            double highestBid = 0;

            for (int i = 0; i < bids.size(); i++) {

                if (AppInstance.appUser != null
                        && AppInstance.appUser.getId() != null
                        && bids.get(i).getUserObj() != null
                        && bids.get(i).getUserObj().getId() != null
                        && bids.get(i).getUserObj().getId().equals(AppInstance.appUser.getId())) {
                    cropObj.setHasBid(false);
                    cropObj.setAlreadyBid(true);

                    bidPrice = Double.parseDouble(bids.get(i).getAmount());

                    cropObj.setMyBidPrice(bids.get(i).getAmount());
                    cropObj.setBidStatus(bids.get(i).getStatus());
                    cropObj.setMyBidId(bids.get(i).getId());
                }

                double amount = Double.parseDouble(bids.get(i).getAmount());

                if (highestBid < amount) {
                    highestBid = amount;
                }

            }

            cropObj.setHighestBid(String.valueOf(highestBid));

        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fetchPageData();

    }

    public void fetchPageData() {

        getCropDetail(cropId, this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bid_now_tv:

                if (!isUserLoggedIn()) {

                    showLoginAlertMessage(AppConstants.PendingTask.CROP_DETAIL);

                } else if (TextUtils.isEmpty(cropObj.getMyBidPrice())
                        || cropObj.getMyBidPrice().isEmpty()) {

                    Toast.makeText(activityContext, getString(R.string.fill_bid_amount), Toast.LENGTH_SHORT).show();

                } else if (Long.parseLong(cropObj.getMyBidPrice()) == 0) {

                    cropObj.setMyBidPrice("");
                    Toast.makeText(activityContext, getString(R.string.fill_bid_amount), Toast.LENGTH_SHORT).show();

                } else if (AppUtils.isNetworkAvailable(activityContext, true, 101)) {

                    bidCrop();

                }

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
                if (index2 < cropObj.getImages().size()) {
                    index2 = index2 + 1;
                    pager.setCurrentItem(index2);
                }
                break;

            case R.id.pay_now_tv:
                Toast.makeText(activityContext, "In progress", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    private void bidCrop() {

        HashMap<String, String> map = new HashMap<>();
        int index;

        if (!cropObj.isAlreadyBid()) {
            map.put("crop", cropObj.getId());
            map.put("user", AppInstance.appUser.getId());
            map.put("status", "Pending");
            map.put("type", "CROP");
            index = AppConstants.API_INDEX.CROP_BID;
        } else {

            if (bidPrice != Double.parseDouble(cropObj.getMyBidPrice())) {
                index = AppConstants.API_INDEX.UPDATE_CROP_BID;
                map.put("bidId", cropObj.getMyBidId());
            } else {
                return;
            }

        }

        map.put("amount", cropObj.getMyBidPrice());
        map.put("time", AppUtils.getCurrentUtcTime());

        try {
            String json = new ObjectMapper().writeValueAsString(map);
            AppLogger.log("", "" + json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        callService(activityContext,
                index,
                CropDetailFragment.this,
                true,
                map,
                getUserAccessToken());

    }

    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (!AppUtils.isSessionExpired(activityContext, response)) {

            if (response.isSuccess()) {

                if (apiIndex == AppConstants.API_INDEX.CROP_BID
                        || apiIndex == AppConstants.API_INDEX.UPDATE_CROP_BID) {

                    cropObj.setHasBid(true);
                    AppUtils.hideProgressDialog();

                } else if (apiIndex == AppConstants.API_INDEX.CROP_DETAIL) {

                    cropObj = AppUtils.getParsedData(response.getData(), Crop.class);

                    parseCropRemainingData(cropObj);

                    hasCurrentUserBid();
                    binding.setData(cropObj);
                    binding.setHandler(CropDetailFragment.this);
                    binding.setTabs(getTabItems());
                    changeAppHeader(this, cropObj.getName());

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

    @BindingAdapter({"customLayout"})
    public static void setCustomView(LinearLayout parent, String type) {

        parent.removeAllViews();

        if (type.equals(parent.getContext().getString(R.string.key_terms_and_conditions))) {

            RowPagerTermsAndConditionsBinding binding
                    = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_pager_terms_and_conditions, parent, false);
            binding.setTermsAndConditions("Terms and Conditions");
            parent.addView(binding.getRoot());

        } else if (type.equals(parent.getContext().getString(R.string.key_shipping))) {

            RowPagerShippingBinding binding
                    = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_pager_shipping, parent, false);
            binding.setData(cropObj);
            parent.addView(binding.getRoot());

        }

    }

    public boolean isAccepted(Crop crop) {
        return !crop.isHasBid()
                && crop.getBidStatus() != null
                && crop.getBidStatus().equalsIgnoreCase(BID_ACCEPTED);
    }

    public String getTenPercentOfAmount(Crop crop) {

        double price = Double.parseDouble(crop.getPrice());
        double discountedPrice = (10 * price) / 100;
        return (String.valueOf(discountedPrice));

    }

}
