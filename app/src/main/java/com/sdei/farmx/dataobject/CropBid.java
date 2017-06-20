package com.sdei.farmx.dataobject;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdei.farmx.BR;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CropBid extends BaseObservable implements Serializable {

    @JsonProperty("id")
    private String id;
    @JsonProperty("amount")
    private String amount;
    @JsonProperty("time")
    private String time;
    @JsonProperty("status")
    private String status;

    @JsonProperty("user")
    private Object user;
    @JsonProperty("crop")
    private Object crop;

    @JsonIgnore
    private User userObj;
    @JsonIgnore
    private Crop cropObj;

    @Bindable
    @JsonIgnore
    public User getUserObj() {
        return userObj;
    }

    @JsonIgnore
    public void setUserObj(User userObj) {
        this.userObj = userObj;
        notifyPropertyChanged(BR.userObj);
    }

    @Bindable
    @JsonIgnore
    public Crop getCropObj() {
        return cropObj;
    }

    @JsonIgnore
    public void setCropObj(Crop cropObj) {
        this.cropObj = cropObj;
        notifyPropertyChanged(BR.cropObj);
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("crop")
    public Object getCrop() {
        return crop;
    }

    @JsonProperty("crop")
    public void setCrop(Object crop) {
        this.crop = crop;
    }

    @Bindable
    @JsonProperty("user")
    public Object getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(Object user) {
        this.user = user;
        notifyPropertyChanged(BR.user);
    }

    @Bindable
    @JsonProperty("amount")
    public String getAmount() {
        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(String amount) {
        this.amount = amount;
        notifyPropertyChanged(BR.amount);
    }

    @Bindable
    @JsonProperty("time")
    public String getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(String time) {
        this.time = time;
        notifyPropertyChanged(BR.time);
    }

    @Bindable
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

}
