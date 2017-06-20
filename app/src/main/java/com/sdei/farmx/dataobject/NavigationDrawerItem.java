package com.sdei.farmx.dataobject;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.sdei.farmx.BR;

import java.util.ArrayList;

public class NavigationDrawerItem extends BaseObservable{

    private String itemName;
    private String key;

    private int index;
    private int parentPosition;

    private int drawable;
    private boolean checked;
    private boolean showDivider;
    private ArrayList<NavigationDrawerItem> childItems;

    @Bindable
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
        notifyPropertyChanged(BR.key);
    }

    @Bindable
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
        notifyPropertyChanged(BR.itemName);
    }

    @Bindable
    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
        notifyPropertyChanged(BR.drawable);
    }

    @Bindable
    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
        notifyPropertyChanged(BR.checked);
    }

    @Bindable
    public boolean isShowDivider() {
        return showDivider;
    }

    public void setShowDivider(boolean showDivider) {
        this.showDivider = showDivider;
        notifyPropertyChanged(BR.showDivider);
    }

    @Bindable
    public ArrayList<NavigationDrawerItem> getChildItems() {
        return childItems;
    }

    public void setChildItems(ArrayList<NavigationDrawerItem> childItems) {
        this.childItems = childItems;
        notifyPropertyChanged(BR.childItems);
    }

    @Bindable
    public int getParentPosition() {
        return parentPosition;
    }

    public void setParentPosition(int parentPosition) {
        this.parentPosition = parentPosition;
        notifyPropertyChanged(BR.parentPosition);
    }

    @Bindable
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        notifyPropertyChanged(BR.index);
    }
}
