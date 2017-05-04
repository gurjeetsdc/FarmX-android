package com.sdei.farmx.adapter;

import com.sdei.farmx.R;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.dataobject.NavigationDrawerItem;

import java.util.ArrayList;

public class NavigationItemsAdapter extends MyBindingBaseAdapter {

    private ArrayList<NavigationDrawerItem> navItemsList;
    private RecyclerCallback recyclerCallback;

    public NavigationItemsAdapter(ArrayList<NavigationDrawerItem> navItemsList,
                                  RecyclerCallback recyclerCallback) {
        this.navItemsList = navItemsList;
        this.recyclerCallback = recyclerCallback;
    }

    @Override
    public int getItemCount() {
        return navItemsList.size();
    }

    @Override
    protected Object getObjForPosition(int position) {
        return navItemsList.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.row_nav_item;
    }

    @Override
    protected RecyclerCallback getRecyclerCallback() {
        return recyclerCallback;
    }

}
