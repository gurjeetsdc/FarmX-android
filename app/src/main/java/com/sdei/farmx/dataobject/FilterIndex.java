package com.sdei.farmx.dataobject;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.sdei.farmx.BR;

public class FilterIndex extends BaseObservable {

    private int filterIndex;

    @Bindable
    public int getFilterIndex() {
        return filterIndex;
    }

    public void setFilterIndex(int filterIndex) {
        this.filterIndex = filterIndex;
        notifyPropertyChanged(BR.filterIndex);
    }

}
