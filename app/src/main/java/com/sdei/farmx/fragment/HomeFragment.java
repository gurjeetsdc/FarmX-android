package com.sdei.farmx.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sdei.farmx.R;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.dataobject.HomeTabItem;
import com.sdei.farmx.dataobject.SingleSelectionItem;
import com.sdei.farmx.databinding.CustomTabBinding;
import com.sdei.farmx.databinding.FragmentHomeBinding;
import com.sdei.farmx.dialog.SingleSelectionDialog;
import com.sdei.farmx.helper.utils.AppUtils;

import java.util.ArrayList;

public class HomeFragment extends AppFragment implements View.OnClickListener {

    private TabLayout tabLayout;
    private SingleSelectionDialog dialog = null;

    /**
     * Called when a fragment is first attached to its context
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activityContext = context;
    }

    /**
     * Called to have the fragment instantiate its user interface view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentHomeBinding binding
                = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        tabLayout = binding.tabsTl;
        return binding.getRoot();
    }

    /**
     * Called when the fragment's activity has been created
     * and this fragment's view hierarchy instantiated
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        changeAppHeader(this, null);
        setTabsLayoutItems();
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * Create the Tab items
     */
    public void setTabsLayoutItems() {

        String tabItems[] = getResources().getStringArray(R.array.home_tab_items);
        String tabItemsKey[] = getResources().getStringArray(R.array.home_tab_items_key);
        TypedArray tabItemsDrawable = getResources().obtainTypedArray(R.array.home_tab_items_drawable);

        for (int i = 0; i < tabItems.length; i++) {

            CustomTabBinding binding
                    = DataBindingUtil.inflate(LayoutInflater.from(activityContext), R.layout.custom_tab, tabLayout, false);

            int id = tabItemsDrawable.getResourceId(i, -1);

            HomeTabItem obj = new HomeTabItem();
            obj.setIndex(i);
            obj.setName(tabItems[i]);
            obj.setIcon(id);
            obj.setKey(tabItemsKey[i]);

            binding.setItem(obj);
            binding.setClickHandler(HomeFragment.this);

            tabLayout.addTab(tabLayout.newTab().setCustomView(binding.getRoot()));

        }

        tabItemsDrawable.recycle();

    }

    /**
     * Handles the tab click action and perform required action
     *
     * @param object Tab item detail
     */
    public void onTabClicked(HomeTabItem object) {

//        if (object.getKey().equalsIgnoreCase(getString(R.string.key_crops))
//                || object.getKey().equalsIgnoreCase(getString(R.string.key_equipments))
//                || object.getKey().equalsIgnoreCase(getString(R.string.key_land))) {
//
//            openSingleSelectionDialog(getItemsArrayList(object.getKey()));
//
//        }
        if (object.getKey().equalsIgnoreCase(getString(R.string.key_crops))
                || object.getKey().equalsIgnoreCase(getString(R.string.key_equipments))) {

            openSingleSelectionDialog(getItemsArrayList(object.getKey()));

        } else if (object.getKey().equalsIgnoreCase(getString(R.string.key_services))) {

        } else if (object.getKey().equalsIgnoreCase(getString(R.string.key_call_us))) {

        } else if(object.getKey().equalsIgnoreCase(getString(R.string.key_farm_inputs))) {

            openFragment(new InputDashboardFragment());

        }

    }

    /**
     * open the list item dialog
     *
     * @param arrayList items to show over the dialog
     */
    private void openSingleSelectionDialog(final ArrayList<SingleSelectionItem> arrayList) {

        dialog = new SingleSelectionDialog(activityContext,
                arrayList, new RecyclerCallback() {
            @Override
            public void onItemClick(int position) {

                if (arrayList.get(position).getKey().equalsIgnoreCase(getString(R.string.key_sell_crop))) {

                    openAddCropPage(false, null);

                } else if (arrayList.get(position).getKey().equalsIgnoreCase(getString(R.string.key_buy_crop))) {

                    openFragment(new CropDashboardFragment());

                } else if (arrayList.get(position).getKey().equalsIgnoreCase(getString(R.string.key_buy_equipments))) {

                    openFragment(new EquipmentDashboardFragment());

                } else if (arrayList.get(position).getKey().equalsIgnoreCase(getString(R.string.key_sell_equipments))
                        || arrayList.get(position).getKey().equalsIgnoreCase(getString(R.string.key_rent_equipments))) {

                    openAddEquipmentFragment(arrayList.get(position).getKey(), null);

                }

                dialog.dismiss();
                dialog = null;
            }

            @Override
            public void onChildItemClick(int parentIndex, int childIndex) {

            }

        });
        dialog.show();

    }

    private ArrayList<SingleSelectionItem> getItemsArrayList(String key) {

        ArrayList<SingleSelectionItem> items = new ArrayList<>();

        int drawable = R.drawable.ic_popup_arrow;

        if (key.equalsIgnoreCase(getString(R.string.key_crops))) {

            items.add(AppUtils.getSingleSelectionItem(0,
                    getString(R.string.key_buy_crop),
                    getString(R.string.buy),
                    drawable));
            items.add(AppUtils.getSingleSelectionItem(1,
                    getString(R.string.key_sell_crop),
                    getString(R.string.sell),
                    drawable));

        } else if (key.equalsIgnoreCase(getString(R.string.key_equipments))) {

            items.add(AppUtils.getSingleSelectionItem(0,
                    getString(R.string.key_buy_equipments),
                    getString(R.string.buy),
                    drawable));
            items.add(AppUtils.getSingleSelectionItem(1,
                    getString(R.string.key_sell_equipments),
                    getString(R.string.sell_rent),
                    drawable));

        } else if (key.equalsIgnoreCase(getString(R.string.key_land))) {

            items.add(AppUtils.getSingleSelectionItem(0,
                    getString(R.string.key_buy_land),
                    getString(R.string.buy),
                    drawable));
            items.add(AppUtils.getSingleSelectionItem(1,
                    getString(R.string.key_sell_land),
                    getString(R.string.sell_lease),
                    drawable));

        } else if (key.equalsIgnoreCase(getString(R.string.key_farm_inputs))) {

            items.add(AppUtils.getSingleSelectionItem(0,
                    getString(R.string.key_buy_input),
                    getString(R.string.buy_input),
                    drawable));
            items.add(AppUtils.getSingleSelectionItem(1,
                    getString(R.string.key_sell_input),
                    getString(R.string.sell_input),
                    drawable));

        }

        return items;

    }

}
