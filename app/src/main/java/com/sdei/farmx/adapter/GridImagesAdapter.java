package com.sdei.farmx.adapter;

import com.sdei.farmx.R;
import com.sdei.farmx.callback.RecyclerCallback;

import java.util.ArrayList;

public class GridImagesAdapter extends MyBindingBaseAdapter {

    private ArrayList<String> arrayList;
    private RecyclerCallback callback;

    public GridImagesAdapter(ArrayList<String> arrayList, RecyclerCallback callback) {
        this.arrayList = arrayList;
        this.callback = callback;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return arrayList.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.row_grid_image;
    }

    @Override
    protected RecyclerCallback getRecyclerCallback() {
        return callback;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}
