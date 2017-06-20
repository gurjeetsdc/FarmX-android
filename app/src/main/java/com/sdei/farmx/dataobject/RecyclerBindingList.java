package com.sdei.farmx.dataobject;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.sdei.farmx.BR;
import com.sdei.farmx.adapter.MyRecyclerAdapter;

import java.util.ArrayList;

public class RecyclerBindingList<T> extends BaseObservable {

    private MyRecyclerAdapter adapter;
    private ArrayList<T> itemsList;

    @Bindable
    public MyRecyclerAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(MyRecyclerAdapter adapter) {
        this.adapter = adapter;
        notifyPropertyChanged(BR.adapter);
    }

    @Bindable
    public ArrayList<T> getItemsList() {
        return itemsList;
    }

    public void setItemsList(ArrayList<T> itemsList) {
        this.itemsList = itemsList;
        notifyPropertyChanged(BR.itemsList);
    }

}
