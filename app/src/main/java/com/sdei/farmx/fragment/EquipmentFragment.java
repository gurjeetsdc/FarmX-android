package com.sdei.farmx.fragment;

import android.text.TextUtils;

import com.sdei.farmx.apimanager.ApiManager;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.dataobject.ApiQueryStringItems;
import com.sdei.farmx.dataobject.Category;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.Equipment;
import com.sdei.farmx.dataobject.ItemDateObject;
import com.sdei.farmx.dataobject.Manufacturer;
import com.sdei.farmx.dataobject.RecyclerBindingList;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;

public class EquipmentFragment extends AppFragment {

    public int API_ITEM_COUNT = 27;

    public void getEquipmentListing(ApiQueryStringItems apiQueryItem,
                                    ApiServiceCallback serviceCallback) {

        if (AppUtils.isNetworkAvailable(activityContext, true, 101)) {

            ApiManager.callService(activityContext,
                    AppConstants.API_INDEX.EQUIPMENT_LISTING,
                    serviceCallback,
                    true,
                    apiQueryItem,
                    getUserAccessToken());

        }

    }

    public void fetchEquipmentListing(ApiServiceCallback callback) {

        ApiQueryStringItems apiQueryItem = new ApiQueryStringItems();
        apiQueryItem.count = API_ITEM_COUNT;
        apiQueryItem.page = 1;
        apiQueryItem.search = "";
        apiQueryItem.sortBy = "createdAt desc";
        apiQueryItem.seller = "";

        getEquipmentListing(apiQueryItem, callback);

    }

    public void parseEquipmentListData(ArrayList<Equipment> dataList, ArrayList<Equipment> equipmentList) {

        for (int i = 0; i < equipmentList.size(); i++) {

            Equipment obj = equipmentList.get(i);
            parseEquipmentRemainingData(obj);
            dataList.add(obj);

        }

    }

    public void parseEquipmentRemainingData(Equipment obj) {

        User userObj = AppUtils.getParsedData(obj.getUser(), User.class);
        obj.setUser(userObj);
        Category catObj = AppUtils.getParsedData(obj.getCategory(), Category.class);
        obj.setCategoryObj(catObj);
        Manufacturer manufacturerObj = AppUtils.getParsedData(obj.getCompanyManufacturer(), Manufacturer.class);
        obj.setManufacrurerObj(manufacturerObj);

        if (obj.getAvailableFrom() != null
                && !TextUtils.isEmpty(obj.getAvailableFrom().toString())) {
            ItemDateObject dateObject = AppUtils.getParsedData(obj.getAvailableFrom(), ItemDateObject.class);
            obj.setItemDateObj(dateObject);
        }

    }

}
