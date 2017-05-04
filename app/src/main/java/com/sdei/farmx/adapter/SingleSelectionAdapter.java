package com.sdei.farmx.adapter;

import com.sdei.farmx.R;
import com.sdei.farmx.callback.RecyclerCallback;
import com.sdei.farmx.dataobject.SingleSelectionItem;

import java.util.ArrayList;

public class SingleSelectionAdapter extends MyBindingBaseAdapter {

    private ArrayList<SingleSelectionItem> arrayList;

    private RecyclerCallback callback;

    public SingleSelectionAdapter(ArrayList<SingleSelectionItem> arrayList, RecyclerCallback callback) {
        this.arrayList = arrayList;
        this.callback = callback;
    }

    @Override
    protected Object getObjForPosition(int position) {
        return arrayList.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return R.layout.row_single_selection;
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
