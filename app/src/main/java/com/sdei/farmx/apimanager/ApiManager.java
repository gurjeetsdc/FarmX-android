package com.sdei.farmx.apimanager;

import android.content.Context;
import android.text.TextUtils;

import com.sdei.farmx.R;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.dataobject.AddToCart;
import com.sdei.farmx.dataobject.ApiQueryStringItems;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.Equipment;
import com.sdei.farmx.dataobject.ImagePath;
import com.sdei.farmx.dataobject.User;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiManager {

    private static ApiResponse result = null;
    public static boolean communicatingToServer = false;

    public static ApiResponse callService(Context context,
                                          final int apiIndex,
                                          final ApiServiceCallback serviceCallback,
                                          boolean showProgressDialog,
                                          Object object,
                                          String authorization) {
        communicatingToServer = true;
        if (showProgressDialog) {

            if (apiIndex == AppConstants.API_INDEX.UPLOAD_IMAGE) {
                AppUtils.showProgressDialog(context, context.getString(R.string.uploading_image));
            } else {
                AppUtils.showProgressDialog(context);
            }

        }

        Call<ApiResponse> callback
                = getApiInterfaceCallback(context,
                apiIndex,
                object,
                authorization);

        if (callback != null) {

            callback.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                    if (response.body() == null) {

                        serviceCallback.onError(apiIndex, response.raw().message());

                    } else {

                        result = response.body();
                        serviceCallback.onSuccess(apiIndex, result);

                    }

                    communicatingToServer = false;

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    serviceCallback.onException(apiIndex, t);
                    communicatingToServer = false;
                }
            });

        } else {

            serviceCallback.onError(apiIndex, "unable to create callback");
            communicatingToServer = false;

        }

        return result;

    }

    @SuppressWarnings("unchecked")
    private static Call<ApiResponse> getApiInterfaceCallback(Context context,
                                                             int apiIndex,
                                                             Object object,
                                                             String authorization) {

        try {

            if (!TextUtils.isEmpty(authorization)) {
                authorization = "Bearer " + authorization;
            }

            ApiInterface apiInterface = ApiClient.createService(ApiInterface.class, authorization);

            switch (apiIndex) {

                case AppConstants.API_INDEX.ADD_TO_CART:
                    return apiInterface.addToCart((AddToCart) object);

                case AppConstants.API_INDEX.STATE:
                    return apiInterface.getStates();

                case AppConstants.API_INDEX.REGISTER:
                    return apiInterface.registerUser((User) object);

                case AppConstants.API_INDEX.LOGIN:
                    return apiInterface.loginUser((HashMap<String, String>) object);

                case AppConstants.API_INDEX.VARIFY_OTP:
                    return apiInterface.varifyOtp(object.toString());

                case AppConstants.API_INDEX.CATEGORIES:
                    return apiInterface.getCategories(object.toString(), "name");

                case AppConstants.API_INDEX.SOCIAL_REGISTER:
                    return apiInterface.socialRegisterUser((HashMap<String, String>) object);

                case AppConstants.API_INDEX.ADD_CROP:
                    return apiInterface.addCrop((Crop) object);

                case AppConstants.API_INDEX.CROP_LISTING:
                    ApiQueryStringItems obj = (ApiQueryStringItems) object;
                    return apiInterface.getCropListing(String.valueOf(obj.count),
                            String.valueOf(obj.page),
                            obj.sortBy,
                            obj.search,
                            obj.seller,
                            obj.list);

                case AppConstants.API_INDEX.MY_CROP_LISTING:
                    ApiQueryStringItems myCropQueryString = (ApiQueryStringItems) object;
                    return apiInterface.getMyCropListing(String.valueOf(myCropQueryString.count),
                            String.valueOf(myCropQueryString.page),
                            myCropQueryString.sortBy,
                            myCropQueryString.search,
                            myCropQueryString.seller,
                            myCropQueryString.list);

                case AppConstants.API_INDEX.DELETE_CROP:
                    return apiInterface.deleteCrop(object.toString());

                case AppConstants.API_INDEX.DELETE_EQUIPMENT:
                    return apiInterface.deleteEquipment(object.toString());

                case AppConstants.API_INDEX.CROP_DETAIL:
                    return apiInterface.cropDetail(object.toString());

                case AppConstants.API_INDEX.UPDATE_CROP:
                    Crop cropObj = (Crop) object;
                    return apiInterface.updateCrop(cropObj.getId(), cropObj);

                case AppConstants.API_INDEX.INPUT_LISTING:
                    ApiQueryStringItems inputQueryString = (ApiQueryStringItems) object;
                    return apiInterface.getInputListing(String.valueOf(inputQueryString.count),
                            String.valueOf(inputQueryString.page),
                            inputQueryString.sortBy,
                            inputQueryString.search,
                            inputQueryString.seller);

                case AppConstants.API_INDEX.UPLOAD_IMAGE:
                    ImagePath path = (ImagePath) object;
                    String base64 = AppUtils.getBase64ForRequestedFile(context, path.getPath());
                    if (!TextUtils.isEmpty(base64)) {
                        base64 = "data:image/"
                                + path.getPath().substring(path.getPath().lastIndexOf(".") + 1)
                                + ";base64,"
                                + base64;
                        RequestBody body = RequestBody.create(MediaType.parse("image/*"), base64);
                        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), path.getType());
                        return apiInterface.uploadImage(body, type);
                    } else {
                        return null;
                    }

                case AppConstants.API_INDEX.CROP_BID:
                    return apiInterface.bidCrop((HashMap<String, String>) object);

                case AppConstants.API_INDEX.UPDATE_CROP_BID:
                case AppConstants.API_INDEX.CROP_ACCEPT_BID:
                    HashMap<String, String> dataMap = (HashMap<String, String>) object;
                    return apiInterface.updateCropBid(dataMap.get("bidId"), (HashMap<String, String>) object);

                case AppConstants.API_INDEX.USER_DETAIL:
                    return apiInterface.getUserDetail(object.toString());

                case AppConstants.API_INDEX.USER_BIDS:
                    return apiInterface.getUserBids(object.toString());

                case AppConstants.API_INDEX.LANGUAGE:
                    return apiInterface.getLanguages();

                case AppConstants.API_INDEX.EQUIPMENT_LISTING:
                    ApiQueryStringItems equipmentQueryString = (ApiQueryStringItems) object;
                    return apiInterface.getEquipmentListing(String.valueOf(equipmentQueryString.count),
                            String.valueOf(equipmentQueryString.page),
                            equipmentQueryString.sortBy,
                            equipmentQueryString.search,
                            equipmentQueryString.seller);

                case AppConstants.API_INDEX.MANUFACTURER:
                    return apiInterface.getManufacturerListing();

                case AppConstants.API_INDEX.ADD_EQUIPMENT:
                    return apiInterface.addEquipment((Equipment) object);

                case AppConstants.API_INDEX.UPDATE_EQUIPMENT:
                    Equipment equipmentObj = (Equipment) object;
                    return apiInterface.updateEquipment(equipmentObj.getId(), equipmentObj);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}
