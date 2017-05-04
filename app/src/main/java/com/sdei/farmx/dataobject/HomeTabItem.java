package com.sdei.farmx.dataobject;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.sdei.farmx.BR;

public class HomeTabItem extends BaseObservable {

    @Bindable
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
        notifyPropertyChanged(BR.key);
    }

    private String key;

    @Bindable
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        notifyPropertyChanged(BR.index);
    }

    private int index;

    private String name;

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(com.sdei.farmx.BR.name);
    }

    @Bindable
    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
        notifyPropertyChanged(com.sdei.farmx.BR.icon);
    }

    private int icon;

}
