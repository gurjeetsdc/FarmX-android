package com.sdei.farmx.apimanager;

import com.sdei.farmx.dataobject.AddToCart;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.Equipment;
import com.sdei.farmx.dataobject.User;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface ApiInterface {

    @POST("users/signin")
    Call<ApiResponse> loginUser(@Body HashMap<String, String> body);

    @POST("users/signup")
    Call<ApiResponse> registerUser(@Body User json);

    @POST("users/signup")
    Call<ApiResponse> socialRegisterUser(@Body HashMap<String, String> body);

    @POST("crops/add")
    Call<ApiResponse> addCrop(@Body Crop object);

    @GET("/users/{user_id}")
    Call<ApiResponse> getUserDetail(@Path("user_id") String user_id);

    @GET("/mybids/{myBids}")
    Call<ApiResponse> getUserBids(@Path("myBids") String myBids);

    @GET("/states")
    Call<ApiResponse> getStates();

    @GET("/allmanufacturer")
    Call<ApiResponse> getManufacturerListing();

    @GET("/user/otp/{otp}")
    Call<ApiResponse> varifyOtp(@Path("otp") String otp);

    @GET("/category")
    Call<ApiResponse> getCategories(@Query("type") String categoryType, @Query("sort") String sort);

    @GET("/crops")
    Call<ApiResponse> getCropListing(@Query("count") String count,
                                     @Query("page") String pageIndex,
                                     @Query("sortBy") String sortBy,
                                     @Query("search") String search,
                                     @Query("seller") String seller,
                                     @Query("list") String list);

    @GET("/mycrops")
    Call<ApiResponse> getMyCropListing(@Query("count") String count,
                                       @Query("page") String pageIndex,
                                       @Query("sortBy") String sortBy,
                                       @Query("search") String search,
                                       @Query("seller") String seller,
                                       @Query("list") String list);

    @GET("/crops/{cropId}")
    Call<ApiResponse> cropDetail(@Path("cropId") String cropId);

    @DELETE("/crops/{cropId}")
    Call<ApiResponse> deleteCrop(@Path("cropId") String cropId);

    @PUT("/crops/edit/{cropId}")
    Call<ApiResponse> updateCrop(@Path("cropId") String cropId, @Body Crop object);

    @GET("/inputs")
    Call<ApiResponse> getInputListing(@Query("count") String count,
                                      @Query("page") String pageIndex,
                                      @Query("sortBy") String sortBy,
                                      @Query("search") String search,
                                      @Query("seller") String seller);

    @Multipart
    @POST("/upload")
    Call<ApiResponse> uploadImage(@Part("data") RequestBody file, @Part("type") RequestBody type);

    @POST("bids/place")
    Call<ApiResponse> bidCrop(@Body HashMap<String, String> body);

    @PUT("bids/place/{bidId}")
    Call<ApiResponse> updateCropBid(@Path("bidId") String bidId, @Body HashMap<String, String> body);

    @GET("/lang")
    Call<ApiResponse> getLanguages();

    @GET("/equipment")
    Call<ApiResponse> getEquipmentListing(@Query("count") String count,
                                          @Query("page") String pageIndex,
                                          @Query("sortBy") String sortBy,
                                          @Query("search") String search,
                                          @Query("seller") String seller);

    @POST("/equipment")
    Call<ApiResponse> addEquipment(@Body Equipment object);

    @DELETE("/equipment/{equipmentId}")
    Call<ApiResponse> deleteEquipment(@Path("equipmentId") String equipmentId);

    @PUT("/equipment/{equipmentId}")
    Call<ApiResponse> updateEquipment(@Path("equipmentId") String equipmentId, @Body Equipment object);

    @POST("/payment/cart/")
    Call<ApiResponse> addToCart(@Body AddToCart object);

}
