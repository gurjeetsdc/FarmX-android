package com.sdei.farmx.dataobject;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CropBidUpdateResponse {

    @JsonProperty("bid")
    private CropBid bid;
    @JsonProperty("message")
    private String message;

    @JsonProperty("bid")
    public CropBid getBid() {
        return bid;
    }

    @JsonProperty("bid")
    public void setBid(CropBid bid) {
        this.bid = bid;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

}
