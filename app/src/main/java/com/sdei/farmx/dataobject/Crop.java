package com.sdei.farmx.dataobject;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sdei.farmx.BR;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@JsonDeserialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class Crop extends BaseObservable implements Serializable {

    @JsonProperty("seller")
    private Object seller;

    @JsonProperty("category")
    private Object category;

    private User user;
    private Category categoryObj;
    private ItemDateObject availableFromObj;

    @JsonProperty("address")
    private String address;

    @JsonProperty("availableFrom")
    private Object availableFrom;

    @JsonProperty("availablePeriod")
    private String availablePeriod;
    @JsonProperty("availableUnit")
    private String availableUnit;
    @JsonProperty("city")
    private String city;
    @JsonProperty("description")
    private String description;
    @JsonProperty("district")
    private String district;
    @JsonProperty("grade")
    private String grade;
    @JsonProperty("name")
    private String name;
    @JsonProperty("paymentPreference")
    private String paymentPreference;
    @JsonProperty("pincode")
    private String pincode;
    @JsonProperty("price")
    private String price;
    @JsonProperty("quantity")
    private String quantity;
    @JsonProperty("quantityUnit")
    private String quantityUnit;
    @JsonProperty("state")
    private String state;
    @JsonProperty("supplyAbility")
    private String supplyAbility;
    @JsonProperty("supplyArea")
    private String supplyArea;
    @JsonProperty("supplyRange")
    private String supplyRange;
    @JsonProperty("variety")
    private String variety;
    @JsonProperty("verified")
    private String verified = "No";
    @JsonProperty("id")
    private String id;
    @JsonProperty("images")
    private ArrayList<String> images;
    @JsonProperty("endDate")
    private String endDate;
    @JsonProperty("bids")
    private ArrayList<CropBid> bids;
    @JsonIgnore
    private String type = "";
    @JsonIgnore
    private String highestBid;
    @JsonIgnore
    private int total;
    @JsonIgnore
    private String myBidPrice = "";
    @JsonIgnore
    private String myBidId;
    @JsonIgnore
    private boolean alreadyBid;
    @JsonIgnore
    private boolean hasBid;
    @JsonIgnore
    private String bidStatus;
    @JsonIgnore
    private ArrayList<ImagePath> imagesPath;

    @Bindable
    @JsonIgnore
    public ArrayList<ImagePath> getImagesPath() {
        return imagesPath;
    }

    @JsonIgnore
    public void setImagesPath(ArrayList<ImagePath> imagesPath) {
        this.imagesPath = imagesPath;
        notifyPropertyChanged(BR.imagesPath);
    }

    @JsonIgnore
    public String getMyBidId() {
        return myBidId;
    }

    @JsonIgnore
    public void setMyBidId(String myBidId) {
        this.myBidId = myBidId;
    }

    @Bindable
    @JsonIgnore
    public String getHighestBid() {
        return highestBid;
    }

    @JsonIgnore
    public void setHighestBid(String highestBid) {
        this.highestBid = highestBid;
        notifyPropertyChanged(BR.highestBid);
    }

    @JsonIgnore
    @Bindable
    public String getBidStatus() {
        return bidStatus;
    }

    @JsonIgnore
    public void setBidStatus(String bidStatus) {
        this.bidStatus = bidStatus;
        notifyPropertyChanged(BR.bidStatus);
    }

    @Bindable
    @JsonProperty("endDate")
    public String getEndDate() {
        return endDate;
    }

    @JsonProperty("endDate")
    public void setEndDate(String endDate) {
        this.endDate = endDate;
        notifyPropertyChanged(BR.endDate);
    }

    @Bindable
    public boolean isAlreadyBid() {
        return alreadyBid;
    }

    public void setAlreadyBid(boolean alreadyBid) {
        this.alreadyBid = alreadyBid;
        notifyPropertyChanged(BR.alreadyBid);
    }

    @Bindable
    @JsonProperty("bids")
    public ArrayList<CropBid> getBids() {
        return bids;
    }

    @JsonProperty("bids")
    public void setBids(ArrayList<CropBid> bids) {
        this.bids = bids;
        notifyPropertyChanged(BR.bids);
    }

    @JsonIgnore
    @Bindable
    public boolean isHasBid() {
        return hasBid;
    }

    @JsonIgnore
    public void setHasBid(boolean hasBid) {
        this.hasBid = hasBid;
        notifyPropertyChanged(BR.hasBid);
    }

    @JsonIgnore
    @Bindable
    public String getMyBidPrice() {
        return myBidPrice;
    }

    @JsonIgnore
    public void setMyBidPrice(String myBidPrice) {
        this.myBidPrice = myBidPrice;
        notifyPropertyChanged(BR.myBidPrice);
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
    public User getUser() {
        return user;
    }

    @JsonIgnore
    public void setUser(User user) {
        this.user = user;
        notifyPropertyChanged(BR.user);
    }

    @JsonIgnore
    @Bindable
    public ItemDateObject getAvailableFromObj() {
        return availableFromObj;
    }

    @JsonIgnore
    public void setAvailableFromObj(ItemDateObject availableFromObj) {
        this.availableFromObj = availableFromObj;
    }

    @JsonIgnore
    @Bindable
    public Category getCategoryObj() {
        return categoryObj;
    }

    @JsonIgnore
    public void setCategoryObj(Category categoryObj) {
        this.categoryObj = categoryObj;
        notifyPropertyChanged(BR.categoryObj);
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
    public Object getSeller() {
        return seller;
    }

    @JsonProperty("seller")
    public void setSeller(Object seller) {
        this.seller = seller;
    }

    @Bindable
    @JsonProperty("category")
    public Object getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(Object category) {
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

    @JsonProperty("availableFrom")
    @Bindable
    public Object getAvailableFrom() {
        return availableFrom;
    }

    @JsonProperty("availableFrom")
    public void setAvailableFrom(Object availableFrom) {
        this.availableFrom = availableFrom;
        notifyPropertyChanged(BR.availableFrom);
    }

    @JsonProperty("availablePeriod")
    @Bindable
    public String getAvailablePeriod() {
        return availablePeriod;
    }

    @JsonProperty("availablePeriod")
    public void setAvailablePeriod(String availablePeriod) {
        this.availablePeriod = availablePeriod;
        notifyPropertyChanged(BR.availablePeriod);
    }

    @JsonProperty("availableUnit")
    @Bindable
    public String getAvailableUnit() {
        return availableUnit;
    }

    @JsonProperty("availableUnit")
    public void setAvailableUnit(String availableUnit) {
        this.availableUnit = availableUnit;
        notifyPropertyChanged(BR.availableUnit);
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

    @JsonProperty("description")
    @Bindable
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
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

    @JsonProperty("grade")
    @Bindable
    public String getGrade() {
        return grade;
    }

    @JsonProperty("grade")
    public void setGrade(String grade) {
        this.grade = grade;
        notifyPropertyChanged(BR.grade);
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

    @JsonProperty("paymentPreference")
    public String getPaymentPreference() {
        return paymentPreference;
    }

    @JsonProperty("paymentPreference")
    public void setPaymentPreference(String paymentPreference) {
        this.paymentPreference = paymentPreference;
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

    @JsonProperty("quantity")
    @Bindable
    public String getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(String quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(BR.quantity);
    }

    @JsonProperty("quantityUnit")
    @Bindable
    public String getQuantityUnit() {
        return quantityUnit;
    }

    @JsonProperty("quantityUnit")
    public void setQuantityUnit(String quantityUnit) {
        this.quantityUnit = quantityUnit;
        notifyPropertyChanged(BR.quantityUnit);
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

    @Bindable
    @JsonProperty("supplyAbility")
    public String getSupplyAbility() {
        return supplyAbility;
    }

    @JsonProperty("supplyAbility")
    public void setSupplyAbility(String supplyAbility) {
        this.supplyAbility = supplyAbility;
        notifyPropertyChanged(BR.supplyAbility);
    }

    @JsonProperty("supplyArea")
    @Bindable
    public String getSupplyArea() {
        return supplyArea;
    }

    @JsonProperty("supplyArea")
    public void setSupplyArea(String supplyArea) {
        this.supplyArea = supplyArea;
        notifyPropertyChanged(BR.supplyArea);
    }

    @JsonProperty("supplyRange")
    @Bindable
    public String getSupplyRange() {
        return supplyRange;
    }

    @JsonProperty("supplyRange")
    public void setSupplyRange(String supplyRange) {
        this.supplyRange = supplyRange;
        notifyPropertyChanged(BR.supplyRange);
    }

    @Bindable
    @JsonProperty("variety")
    public String getVariety() {
        return variety;
    }

    @JsonProperty("variety")
    public void setVariety(String variety) {
        this.variety = variety;
        notifyPropertyChanged(BR.variety);
    }

    @Bindable
    @JsonProperty("verified")
    public String getVerified() {
        return verified;
    }

    @JsonProperty("verified")
    public void setVerified(String verified) {
        this.verified = verified;
        notifyPropertyChanged(BR.verified);
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

}
