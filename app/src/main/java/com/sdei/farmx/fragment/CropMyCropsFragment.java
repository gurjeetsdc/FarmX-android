package com.sdei.farmx.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sdei.farmx.R;
import com.sdei.farmx.apimanager.ApiManager;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.DialogCallback;
import com.sdei.farmx.callback.RecyclerMoreActionCallback;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.databinding.FragmentMyCropsBinding;
import com.sdei.farmx.dataobject.ApiQueryStringItems;
import com.sdei.farmx.dataobject.Crop;
import com.sdei.farmx.dataobject.CropListingResponse;
import com.sdei.farmx.dataobject.FilterIndex;
import com.sdei.farmx.dataobject.RecyclerBindingList;
import com.sdei.farmx.helper.AppInstance;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;

public class CropMyCropsFragment
        extends CropFragment
        implements View.OnClickListener, ApiServiceCallback, RecyclerMoreActionCallback {

    private FilterIndex filterIndex;
    private FragmentMyCropsBinding binding;

    private RecyclerBindingList<Crop> bindingList;

    private ArrayList<Crop> activeItems;

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
                = DataBindingUtil.inflate(inflater, R.layout.fragment_my_crops, container, false);
        filterIndex = new FilterIndex();
        binding.setFilterIndex(filterIndex);
        binding.setHandler(CropMyCropsFragment.this);

        bindingList = new RecyclerBindingList<>();
        bindingList.setItemsList(new ArrayList<Crop>());
        binding.setItem(bindingList);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        changeAppHeader(CropMyCropsFragment.this, getString(R.string.my_crops));
        fetchMyCrops();

    }

    private void fetchMyCrops() {

        ApiQueryStringItems apiQueryItem = new ApiQueryStringItems();
        apiQueryItem.count = 25;
        apiQueryItem.page = 1;
        apiQueryItem.search = "";
        apiQueryItem.sortBy = "createdAt desc";
        apiQueryItem.seller = AppInstance.appUser.getId();
        apiQueryItem.list = "";
        getMyCropsListing(apiQueryItem, CropMyCropsFragment.this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.active_rl:
                filterIndex.setFilterIndex(0);
                if (activeItems != null) {
                    ArrayList<Crop> items = bindingList.getItemsList();
                    items.clear();
                    items.addAll(activeItems);
                    bindingList.setItemsList(items);
                    bindingList.getAdapter().notifyDataSetChanged();
                }
                break;

            case R.id.expired_rl:
                filterIndex.setFilterIndex(1);
                ArrayList<Crop> items = bindingList.getItemsList();
                activeItems = new ArrayList<>(items);
                items.clear();
                bindingList.setItemsList(items);
                bindingList.getAdapter().notifyDataSetChanged();
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

                if (apiIndex == AppConstants.API_INDEX.MY_CROP_LISTING) {

                    CropListingResponse object = AppUtils.getParsedData(response.getData(), CropListingResponse.class);

                    if (object != null
                            && object.getCrops() != null
                            && object.getCrops().size() > 0) {

                        ArrayList<Crop> dataList = bindingList.getItemsList();
                        parseCropListData(dataList, object.getCrops());
                        bindingList.setItemsList(dataList);
                        binding.setItem(bindingList);

                    }

                } else if (apiIndex == AppConstants.API_INDEX.DELETE_CROP) {

                    Toast.makeText(
                            activityContext,
                            getString(R.string.message_crop_delete_successfully),
                            Toast.LENGTH_SHORT).show();
                    removeItemFromBindingObject();

                }

            } else if (response.getError() != null
                    && response.getError().getMessage().equals("Not Found")) {

                Toast.makeText(activityContext, getString(R.string.message_crop_not_available), Toast.LENGTH_SHORT).show();

            }

        }

        AppUtils.hideProgressDialog();

    }

    private void removeItemFromBindingObject() {

        final ArrayList<Crop> dataList = bindingList.getItemsList();
        dataList.remove(deletedItemIndex);
        bindingList.getAdapter().notifyItemRemoved(deletedItemIndex);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bindingList.setItemsList(dataList);
                bindingList.getAdapter().notifyDataSetChanged();
            }
        }, 500);

    }

    @Override
    public void onException(int apiIndex, Throwable t) {

        if (showErrorToast(CropMyCropsFragment.this, t.getMessage()))
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

        openCropDetailPage(bindingList.getItemsList().get(position).getId());

    }

    private void editCrop(Crop cropList) {

        openAddCropPage(true, cropList);

    }

    @Override
    public void onChildItemClick(int parentIndex, int childIndex) {

    }

    @Override
    public void itemAction(String type, final int position) {

        switch (type) {

            case AppConstants.RECYCLER_ACTION.EDIT:
                editCrop(bindingList.getItemsList().get(position));
                break;

            case AppConstants.RECYCLER_ACTION.DELETE:
                AppUtils.openPNAlertDialog(
                        activityContext,
                        getString(R.string.alert_delete_crop),
                        getString(R.string.ok),
                        new DialogCallback() {
                            @Override
                            public void onSuccess() {
                                deleteCrop(position);
                            }

                            @Override
                            public void onFailure() {

                            }
                        });
                break;

            case AppConstants.RECYCLER_ACTION.BIDS:
                Crop crop = bindingList.getItemsList().get(position);

                CropBidListFragment fragment = new CropBidListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("data", crop);
                fragment.setArguments(bundle);
                openFragment(fragment);
                break;

        }

    }

    private int deletedItemIndex;

    private void deleteCrop(int position) {

        if (isNetworkAvailable()) {

            String cropId = bindingList.getItemsList().get(position).getId();
            deletedItemIndex = position;
            ApiManager.callService(activityContext,
                    AppConstants.API_INDEX.DELETE_CROP,
                    CropMyCropsFragment.this,
                    true,
                    cropId,
                    getUserAccessToken());

        }

    }

    public void refreshItemsList() {

        ArrayList<Crop> dataList = bindingList.getItemsList();
        dataList.clear();
        bindingList.setItemsList(dataList);

        fetchMyCrops();

    }

}
