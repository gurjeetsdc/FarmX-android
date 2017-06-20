package com.sdei.farmx.dataobject;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class UserBidsResponse {

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("data")
    private ArrayList<CropBid> data;

    @JsonProperty("status")
    public boolean isStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(boolean status) {
        this.status = status;
    }

    @JsonProperty("data")
    public ArrayList<CropBid> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(ArrayList<CropBid> data) {
        this.data = data;
    }
}
