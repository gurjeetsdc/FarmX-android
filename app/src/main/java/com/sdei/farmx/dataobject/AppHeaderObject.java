package com.sdei.farmx.dataobject;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.sdei.farmx.BR;

public class AppHeaderObject extends BaseObservable {

    private String title;
    private int backgroundDrawable;
    private int backgroundColor;
    private boolean showSearch;
    private boolean hasCenteredImage;

    private boolean showMarquee;

    @Bindable
    public boolean isShowMarquee() {
        return showMarquee;
    }

    public void setShowMarquee(boolean showMarquee) {
        this.showMarquee = showMarquee;
        notifyPropertyChanged(BR.showMarquee);
    }

    @Bindable
    public int getBackgroundDrawable() {
        return backgroundDrawable;
    }

    public void setBackgroundDrawable(int backgroundDrawable) {
        this.backgroundDrawable = backgroundDrawable;
        notifyPropertyChanged(BR.backgroundDrawable);
    }

    @Bindable
    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        notifyPropertyChanged(BR.backgroundColor);
    }

    @Bindable
    public boolean isShowSearch() {
        return showSearch;
    }

    public void setShowSearch(boolean showSearch) {
        this.showSearch = showSearch;
        notifyPropertyChanged(BR.showSearch);
    }

    @Bindable
    public boolean isHasCenteredImage() {
        return hasCenteredImage;
    }

    public void setHasCenteredImage(boolean hasCenteredImage) {
        this.hasCenteredImage = hasCenteredImage;
        notifyPropertyChanged(BR.hasCenteredImage);
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

}
