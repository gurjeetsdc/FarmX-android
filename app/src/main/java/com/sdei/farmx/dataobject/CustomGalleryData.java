package com.sdei.farmx.dataobject;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.sdei.farmx.BR;


/**
 * This is the class for storing images data like their ids, path, and selection status
 */
public class CustomGalleryData extends BaseObservable {

    private int id;
    private String path;
    private boolean checked;

    @Bindable
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        notifyPropertyChanged(BR.path);
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
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

}
