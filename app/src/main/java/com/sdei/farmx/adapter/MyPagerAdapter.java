package com.sdei.farmx.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;

public class MyPagerAdapter extends PagerAdapter {

    private Context context;
    private int resource;
    private ArrayList<?> objects;

    private String type;

    public MyPagerAdapter(Context context,
                          int resource,
                          ArrayList<?> objects,
                          String type) {
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.type = type;
    }

    @Override
    public int getCount() {

        if (objects != null)
            return objects.size();
        else
            return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                resource,
                container,
                false);
        binding.setVariable(BR.data, objects.get(position));
        binding.setVariable(BR.type, type);

        View view = binding.getRoot();
        container.addView(view);
        return view;

    }

}
