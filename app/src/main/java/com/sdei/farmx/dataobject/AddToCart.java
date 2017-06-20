package com.sdei.farmx.dataobject;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AddToCart {

    @JsonProperty("product")
    private String product;
    @JsonProperty("user")
    private String user;
    @JsonProperty("productType")
    private String productType;
    @JsonProperty("currentPrice")
    private String currentPrice;
    @JsonProperty("quantity")
    private String quantity;

    @JsonProperty("product")
    public String getProduct() {
        return product;
    }

    @JsonProperty("product")
    public void setProduct(String product) {
        this.product = product;
    }

    @JsonProperty("user")
    public String getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(String user) {
        this.user = user;
    }

    @JsonProperty("productType")
    public String getProductType() {
        return productType;
    }

    @JsonProperty("productType")
    public void setProductType(String productType) {
        this.productType = productType;
    }

    @JsonProperty("currentPrice")
    public String getCurrentPrice() {
        return currentPrice;
    }

    @JsonProperty("currentPrice")
    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    @JsonProperty("quantity")
    public String getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

}
