package com.sdei.farmx.adapter;

import android.databinding.BaseObservable;

import com.sdei.farmx.callback.RecyclerCallback;

import java.util.ArrayList;

public class MyRecyclerAdapter extends MyBindingBaseAdapter {

    private int resource;
    private ArrayList<?> objects;
    private RecyclerCallback recyclerCallback;

    public MyRecyclerAdapter(int resource,
                             ArrayList<?> objects,
                             RecyclerCallback recyclerCallback) {
        this.resource = resource;
        this.objects = objects;
        this.recyclerCallback = recyclerCallback;
    }

    @Override
    public int getItemCount() {
        if(objects != null){
            return objects.size();
        } else {
            return 0;
        }
    }

    @Override
    protected Object getObjForPosition(int position) {
        return objects.get(position);
    }

    @Override
    protected int getLayoutIdForPosition(int position) {
        return resource;
    }

    @Override
    protected RecyclerCallback getRecyclerCallback() {
        return recyclerCallback;
    }

}
