package com.sdei.farmx.dataobject;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.sdei.farmx.BR;

public class SingleSelectionItem extends BaseObservable{

    @Bindable
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        notifyPropertyChanged(BR.index);
    }

    private int index;

    @Bindable
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
        notifyPropertyChanged(BR.key);
    }

    private String key;
    private String itemName;
    private int rightDrawable;

    @Bindable
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
        notifyPropertyChanged(BR.itemName);
    }

    @Bindable
    public int getRightDrawable() {
        return rightDrawable;
    }

    public void setRightDrawable(int rightDrawable) {
        this.rightDrawable = rightDrawable;
        notifyPropertyChanged(BR.rightDrawable);
    }

}
