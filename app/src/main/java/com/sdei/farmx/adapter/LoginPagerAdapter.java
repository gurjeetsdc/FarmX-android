package com.sdei.farmx.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sdei.farmx.R;
import com.sdei.farmx.databinding.RowLoginPagerBinding;

import java.util.ArrayList;

public class LoginPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> objects;

    public LoginPagerAdapter(Context context, ArrayList<String> objects) {
        this.context = context;
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
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

        RowLoginPagerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.row_login_pager,
                container,
                false);
        binding.setStatement(objects.get(position));

        View view = binding.getRoot();
        container.addView(view);
        return view;

    }

}
