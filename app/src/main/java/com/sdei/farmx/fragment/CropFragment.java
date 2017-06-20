package com.sdei.farmx.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import com.sdei.farmx.apimanager.ApiManager;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.dataobject.ApiQueryStringItems;
import com.sdei.farmx.dataobject.Category;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.CropBid;
import com.sdei.farmx.dataobject.ItemDateObject;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class CropFragment extends AppFragment {

    public static final String BID_ACCEPTED = "Accepted";

    public void getCropsListing(ApiQueryStringItems apiQueryItem,
                                ApiServiceCallback serviceCallback) {

        if (AppUtils.isNetworkAvailable(activityContext, true, 101)) {

            ApiManager.callService(activityContext,
                    AppConstants.API_INDEX.CROP_LISTING,
                    serviceCallback,
                    true,
                    apiQueryItem,
                    getUserAccessToken());

        }

    }

    public void getMyCropsListing(ApiQueryStringItems apiQueryItem,
                                  ApiServiceCallback callback) {

        if (AppUtils.isNetworkAvailable(activityContext, true, 101)) {

            ApiManager.callService(activityContext,
                    AppConstants.API_INDEX.MY_CROP_LISTING,
                    callback,
                    true,
                    apiQueryItem,
                    getUserAccessToken());

        }

    }

    public void openCropDetailPage(String cropId) {

        CropDetailFragment fragment = new CropDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("cropId", cropId);
        fragment.setArguments(bundle);
        openFragment(fragment);

    }

    public void getCropDetail(String cropId, ApiServiceCallback callback) {

        if (AppUtils.isNetworkAvailable(activityContext, false, 101)) {

            ApiManager.callService(
                    activityContext,
                    AppConstants.API_INDEX.CROP_DETAIL,
                    callback,
                    true,
                    cropId,
                    getUserAccessToken());

        } else {

            AppUtils.hideProgressDialog();

        }

    }

    public void parseCropListData(ArrayList<Crop> dataList, ArrayList<Crop> cropList) {

        for (int i = 0; i < cropList.size(); i++) {

            Crop obj = cropList.get(i);
            parseCropRemainingData(obj);
            dataList.add(obj);

        }

    }

    public void parseCropRemainingData(Crop obj) {

        User userObj = AppUtils.getParsedData(obj.getSeller(), User.class);
        obj.setUser(userObj);

        Category catObj = AppUtils.getParsedData(obj.getCategory(), Category.class);
        obj.setCategoryObj(catObj);

        if (obj.getAvailableFrom() != null && !TextUtils.isEmpty(obj.getAvailableFrom().toString())) {
            ItemDateObject dateObject = AppUtils.getParsedData(obj.getAvailableFrom(), ItemDateObject.class);
            obj.setAvailableFromObj(dateObject);
        }

        ArrayList<CropBid> bids = obj.getBids();

        if (bids != null && bids.size() > 0) {
            for (int i = 0; i < bids.size(); i++) {
                CropBid bid = bids.get(i);
                User bidUserObj = AppUtils.getParsedData(bid.getUser(), User.class);
                bid.setUserObj(bidUserObj);
            }
            obj.setBids(bids);
        }

    }

}
