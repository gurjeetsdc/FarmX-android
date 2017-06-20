package com.sdei.farmx.fragment;

import android.os.Bundle;

import com.sdei.farmx.apimanager.ApiManager;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.dataobject.ApiQueryStringItems;
import com.sdei.farmx.dataobject.FarmInput;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

public class InputFragment extends AppFragment {

    public void fetchInputListing(int count, ApiServiceCallback callback) {

        ApiQueryStringItems apiQueryItem = new ApiQueryStringItems();
        apiQueryItem.count = count;
        apiQueryItem.page = 1;
        apiQueryItem.search = "";
        apiQueryItem.sortBy = "createdAt desc";
        apiQueryItem.seller = "";
        getInputsListing(apiQueryItem, callback);

    }

    public void openFarmInputDetailPage(FarmInput object) {

        InputDetailFragment fragment = new InputDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", object);
        fragment.setArguments(bundle);
        openFragment(fragment);

    }

    public void getInputsListing(ApiQueryStringItems apiQueryItem,
                                 ApiServiceCallback serviceCallback) {

        if (AppUtils.isNetworkAvailable(activityContext, true, 101)) {
            ApiManager.callService(activityContext,
                    AppConstants.API_INDEX.INPUT_LISTING,
                    serviceCallback,
                    true,
                    apiQueryItem,
                    getUserAccessToken());
        }

    }

}
