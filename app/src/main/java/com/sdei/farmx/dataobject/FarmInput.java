package com.sdei.farmx.dataobject;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdei.farmx.BR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FarmInput extends BaseObservable implements Serializable {

    @JsonProperty("user")
    private User user;
    @JsonProperty("category")
    private Category category;
    @JsonProperty("manufacturer")
    private Manufacturer manufacturer;
    @JsonProperty("variety")
    private String variety;
    @JsonProperty("terms")
    private String terms;
    @JsonProperty("address")
    private String address;
    @JsonProperty("city")
    private String city;
    @JsonProperty("additionalInformation")
    private String additionalInformation;
    @JsonProperty("district")
    private String district;
    @JsonProperty("name")
    private String name;
    @JsonProperty("pincode")
    private String pincode;
    @JsonProperty("price")
    private String price;
    @JsonProperty("priceUnit")
    private String priceUnit;
    @JsonProperty("state")
    private String state;
    @JsonProperty("id")
    private String id;
    @JsonProperty("images")
    private ArrayList<String> images;
    @JsonProperty("quantity")
    private String quantity;
    @JsonProperty("quantityUnit")
    private String quantityUnit;
    @JsonIgnore
    private String type = "";
    @JsonIgnore
    private int total;
    @JsonIgnore
    private String totalPrice = "0";
    @JsonIgnore
    private String requiredQuantity;
    @JsonIgnore
    private String selectedQuantityUnit;

    @JsonIgnore
    @Bindable
    public String getSelectedQuantityUnit() {
        return selectedQuantityUnit;
    }

    @JsonIgnore
    public void setSelectedQuantityUnit(String selectedQuantityUnit) {
        this.selectedQuantityUnit = selectedQuantityUnit;
        notifyPropertyChanged(BR.selectedQuantityUnit);
    }

    @JsonIgnore
    @Bindable
    public String getRequiredQuantity() {
        return requiredQuantity;
    }

    @JsonIgnore
    public void setRequiredQuantity(String requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
        notifyPropertyChanged(BR.requiredQuantity);
    }

    @JsonIgnore
    @Bindable
    public String getTotalPrice() {
        return totalPrice;
    }

    @JsonIgnore
    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
        notifyPropertyChanged(BR.totalPrice);
    }

    @Bindable
    @JsonProperty("quantityUnit")
    public String getQuantityUnit() {
        return quantityUnit;
    }

    @JsonProperty("quantityUnit")
    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
        notifyPropertyChanged(BR.quantityUnit);
    }

    @JsonProperty("quantity")
    public String getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @JsonProperty("priceUnit")
    public String getPriceUnit() {
        return priceUnit;
    }

    @JsonProperty("priceUnit")
    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
    }

    @JsonProperty("terms")
    public String getTerms() {
        return terms;
    }

    @JsonProperty("terms")
    public void setTerms(String terms) {
        this.terms = terms;
    }

    @JsonProperty("manufacturer")
    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    @JsonProperty("manufacturer")
    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Bindable
    @JsonProperty("images")
    public ArrayList<String> getImages() {
        return images;
    }

    @JsonProperty("images")
    public void setImages(ArrayList<String> images) {
        this.images = images;
        notifyPropertyChanged(BR.images);
    }

    @JsonIgnore
    @Bindable
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
        notifyPropertyChanged(BR.total);
    }

    @JsonIgnore
    @Bindable
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
        notifyPropertyChanged(BR.type);
    }

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("seller")
    public User getUser() {
        return user;
    }

    @JsonProperty("seller")
    public void setUser(User seller) {
        this.user = seller;
    }

    @Bindable
    @JsonProperty("category")
    public Category getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(Category category) {
        this.category = category;
        notifyPropertyChanged(BR.category);
    }

    @JsonProperty("address")
    @Bindable
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    @JsonProperty("city")
    @Bindable
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
        notifyPropertyChanged(BR.city);
    }

    @Bindable
    @JsonProperty("additionalInformation")
    public String getAdditionalInformation() {
        return additionalInformation;
    }

    @JsonProperty("additionalInformation")
    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
        notifyPropertyChanged(BR.additionalInformation);
    }

    @Bindable
    @JsonProperty("district")
    public String getDistrict() {
        return district;
    }

    @JsonProperty("district")
    public void setDistrict(String district) {
        this.district = district;
        notifyPropertyChanged(BR.district);
    }

    @JsonProperty("name")
    @Bindable
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @JsonProperty("pincode")
    @Bindable
    public String getPincode() {
        return pincode;
    }

    @JsonProperty("pincode")
    public void setPincode(String pincode) {
        this.pincode = pincode;
        notifyPropertyChanged(BR.pincode);
    }

    @JsonProperty("price")
    @Bindable
    public String getPrice() {
        return price;
    }

    @JsonProperty("price")
    public void setPrice(String price) {
        this.price = price;
        notifyPropertyChanged(BR.price);
    }

    @Bindable
    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
        notifyPropertyChanged(BR.state);
    }

    @JsonProperty("variety")
    public String getVariety() {
        return variety;
    }

    @JsonProperty("variety")
    public void setVariety(String variety) {
        this.variety = variety;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
