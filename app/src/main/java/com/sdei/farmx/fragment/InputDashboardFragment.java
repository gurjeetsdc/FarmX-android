package com.sdei.farmx.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sdei.farmx.R;
import com.sdei.farmx.apimanager.ApiResponse;
import com.sdei.farmx.callback.ApiServiceCallback;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.callback.RecyclerMoreActionCallback;
import com.sdei.farmx.databinding.FragmentBuyInputBinding;
import com.sdei.farmx.dataobject.AddToCart;
import com.sdei.farmx.dataobject.FarmInput;
import com.sdei.farmx.dataobject.InputListingResponse;
import com.sdei.farmx.dataobject.RecyclerBindingList;
import com.sdei.farmx.helper.AppInstance;
import com.sdei.farmx.helper.utils.AppConstants;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;

public class InputDashboardFragment
        extends InputFragment
        implements RecyclerMoreActionCallback, View.OnClickListener, ApiServiceCallback {

    private int API_ITEM_COUNT = 3;

    private RecyclerBindingList<FarmInput> bindingList;
    private InputListingResponse object;

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

        FragmentBuyInputBinding binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_buy_input, container, false);
        binding.setHandler(InputDashboardFragment.this);
        bindingList = new RecyclerBindingList<>();
        bindingList.setItemsList(new ArrayList<FarmInput>());
        binding.setItems(bindingList);
        return binding.getRoot();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        changeAppHeader(this, getString(R.string.farm_inputs));
        fetchInputsList();

    }

    public void fetchInputsList() {

        fetchInputListing(API_ITEM_COUNT, InputDashboardFragment.this);

    }

    @Override
    public void onItemClick(int position) {

        if (bindingList.getItemsList().get(position).getType().equals("header")) {

            InputMoreItemsFragment fragment = new InputMoreItemsFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("total", bindingList.getItemsList().get(position).getTotal());
            fragment.setArguments(bundle);

            openFragment(fragment);

        } else {

            openFarmInputDetailPage(bindingList.getItemsList().get(position));

        }

    }

    @Override
    public void onChildItemClick(int position, int childIndex) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.action_first_ll:
                Toast.makeText(activityContext, "In Progress", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_second_ll:
                Toast.makeText(activityContext, "In Progress", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    @Override
    public void onSuccess(int apiIndex, ApiResponse response) {

        if (!AppUtils.isSessionExpired(activityContext, response) && response.isSuccess()) {

            if (apiIndex == AppConstants.API_INDEX.INPUT_LISTING) {

                object = AppUtils.getParsedData(response.getData(), InputListingResponse.class);

                if (object != null
                        && object.getInputs() != null
                        && object.getInputs().size() > 0) {

                    ArrayList<FarmInput> dataList = bindingList.getItemsList();

                    if (object.getTotal() > API_ITEM_COUNT) {
                        FarmInput headerObject = new FarmInput();
                        headerObject.setId(null);
                        headerObject.setName(getString(R.string.more_inputs));
                        headerObject.setType("header");
                        headerObject.setTotal(object.getTotal() - API_ITEM_COUNT);
                        dataList.add(0, headerObject);
                    }

                    dataList.addAll(object.getInputs());
                    bindingList.setItemsList(dataList);

                }

            } else if (apiIndex == AppConstants.API_INDEX.ADD_TO_CART) {

                Toast.makeText(activityContext, getString(R.string.item_added_to_cart), Toast.LENGTH_SHORT).show();

            }

        }

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onException(int apiIndex, Throwable t) {

        String errorMessage = t.getMessage();

        if (showErrorToast(InputDashboardFragment.this, errorMessage))
            Toast.makeText(activityContext, t.getMessage(), Toast.LENGTH_SHORT).show();

        AppUtils.hideProgressDialog();

    }

    @Override
    public void onError(int apiIndex, String message) {
        Toast.makeText(activityContext, "" + message, Toast.LENGTH_SHORT).show();
        AppUtils.hideProgressDialog();
    }

    @Override
    public void itemAction(String type, int position) {

        switch (type) {

            case AppConstants.RECYCLER_ACTION.ADD_TO_CART:
                AddToCart object = new AddToCart();
                object.setProduct(bindingList.getItemsList().get(position).getId());
                object.setUser(AppInstance.appUser.getId());
                object.setQuantity("1");
                object.setProductType(AppConstants.CART_PRODUCT.INPUT);
                object.setCurrentPrice(bindingList.getItemsList().get(position).getPrice());
                addItemToCart(object, InputDashboardFragment.this);
                break;

        }

    }

}
