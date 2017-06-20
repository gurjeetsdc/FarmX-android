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
public class Equipment extends BaseObservable implements Serializable {

    @JsonProperty("user")
    private Object user;
    @JsonProperty("companyManufacturer")
    private Object companyManufacturer;
    @JsonProperty("category")
    private Object category;
    @JsonProperty("name")
    private String name;
    @JsonProperty("model")
    private String model;
    @JsonProperty("modelyear")
    private String modelyear;
    @JsonProperty("rentSell")
    private String rentSell;
    @JsonProperty("rate")
    private String rate;
    @JsonProperty("description")
    private String description;
    @JsonProperty("quantity")
    private String quantity;
    @JsonProperty("price_unit")
    private String priceUnit;
    @JsonProperty("city")
    private String city;
    @JsonProperty("district")
    private String district;
    @JsonProperty("state")
    private String state;
    @JsonProperty("pincode")
    private String pincode;
    @JsonProperty("verified")
    private String verified = "No";
    @JsonProperty("avalibilityperiodUnits")
    private String avalibilityperiodUnits;
    @JsonProperty("variety")
    private String variety;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("availableFrom")
    private Object availableFrom;
    @JsonProperty("avalibilityperiod")
    private String avalibilityperiod;
    @JsonProperty("address")
    private String address;
    @JsonProperty("id")
    private String id;
    @JsonProperty("images")
    private ArrayList<String> images;
    @JsonIgnore
    private ArrayList<ImagePath> imagesPath;

    private String type;
    private int total;
    private User userObj;
    private Manufacturer manufacrurerObj;
    private Category categoryObj;
    private ItemDateObject itemDateObj;

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
    @Bindable
    public ItemDateObject getItemDateObj() {
        return itemDateObj;
    }

    @JsonIgnore
    public void setItemDateObj(ItemDateObject itemDateObj) {
        this.itemDateObj = itemDateObj;
        notifyPropertyChanged(BR.itemDateObj);
    }

    @Bindable
    @JsonIgnore
    public User getUserObj() {
        return userObj;
    }

    public void setUserObj(User userObj) {
        this.userObj = userObj;
        notifyPropertyChanged(BR.userObj);
    }

    @Bindable
    @JsonIgnore
    public Manufacturer getManufacrurerObj() {
        return manufacrurerObj;
    }

    public void setManufacrurerObj(Manufacturer manufacrurerObj) {
        this.manufacrurerObj = manufacrurerObj;
        notifyPropertyChanged(BR.manufacrurerObj);
    }

    @Bindable
    @JsonIgnore
    public Category getCategoryObj() {
        return categoryObj;
    }

    public void setCategoryObj(Category categoryObj) {
        this.categoryObj = categoryObj;
        notifyPropertyChanged(BR.categoryObj);
    }

    @Bindable
    @JsonIgnore
    public int getTotal() {
        return total;
    }

    @JsonIgnore
    public void setTotal(int total) {
        this.total = total;
        notifyPropertyChanged(BR.total);
    }

    @JsonIgnore
    @Bindable
    public String getType() {
        return type;
    }

    @JsonIgnore
    public void setType(String type) {
        this.type = type;
        notifyPropertyChanged(BR.type);
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
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("user")
    public Object getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(Object user) {
        this.user = user;
    }

    @JsonProperty("companyManufacturer")
    public Object getCompanyManufacturer() {
        return companyManufacturer;
    }

    @JsonProperty("companyManufacturer")
    public void setCompanyManufacturer(Object companyManufacturer) {
        this.companyManufacturer = companyManufacturer;
    }

    @JsonProperty("category")
    public Object getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(Object category) {
        this.category = category;
    }

    @Bindable
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @JsonProperty("model")
    public String getModel() {
        return model;
    }

    @JsonProperty("model")
    public void setModel(String model) {
        this.model = model;
    }

    @JsonProperty("modelyear")
    public String getModelyear() {
        return modelyear;
    }

    @JsonProperty("modelyear")
    public void setModelyear(String modelyear) {
        this.modelyear = modelyear;
    }

    @Bindable
    @JsonProperty("rentSell")
    public String getRentSell() {
        return rentSell;
    }

    @JsonProperty("rentSell")
    public void setRentSell(String rentSell) {
        this.rentSell = rentSell;
        notifyPropertyChanged(BR.rentSell);
    }

    @Bindable
    @JsonProperty("rate")
    public String getRate() {
        return rate;
    }

    @JsonProperty("rate")
    public void setRate(String rate) {
        this.rate = rate;
        notifyPropertyChanged(BR.rate);
    }

    @Bindable
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    @JsonProperty("quantity")
    public String getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(String quantity) {
        this.quantity = quantity;
        notifyPropertyChanged(BR.quantity);
    }

    @Bindable
    @JsonProperty("price_unit")
    public String getPriceUnit() {
        return priceUnit;
    }

    @JsonProperty("price_unit")
    public void setPriceUnit(String priceUnit) {
        this.priceUnit = priceUnit;
        notifyPropertyChanged(BR.priceUnit);
    }

    @Bindable
    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
        notifyPropertyChanged(BR.city);
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
    @JsonProperty("pincode")
    public String getPincode() {
        return pincode;
    }

    @JsonProperty("pincode")
    public void setPincode(String pincode) {
        this.pincode = pincode;
        notifyPropertyChanged(BR.pincode);
    }

    @JsonProperty("verified")
    public String getVerified() {
        return verified;
    }

    @JsonProperty("verified")
    public void setVerified(String verified) {
        this.verified = verified;
    }

    @Bindable
    @JsonProperty("avalibilityperiodUnits")
    public String getAvalibilityperiodUnits() {
        return avalibilityperiodUnits;
    }

    @JsonProperty("avalibilityperiodUnits")
    public void setAvalibilityperiodUnits(String avalibilityperiodUnits) {
        this.avalibilityperiodUnits = avalibilityperiodUnits;
        notifyPropertyChanged(BR.avalibilityperiodUnits);
    }

    @JsonProperty("variety")
    public String getVariety() {
        return variety;
    }

    @JsonProperty("variety")
    public void setVariety(String variety) {
        this.variety = variety;
    }

    @JsonProperty("payment_method")
    public String getPaymentMethod() {
        return paymentMethod;
    }

    @JsonProperty("payment_method")
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @JsonProperty("availableFrom")
    public Object getAvailableFrom() {
        return availableFrom;
    }

    @JsonProperty("availableFrom")
    public void setAvailableFrom(Object availableFrom) {
        this.availableFrom = availableFrom;
    }

    @Bindable
    @JsonProperty("avalibilityperiod")
    public String getAvalibilityperiod() {
        return avalibilityperiod;
    }

    @JsonProperty("avalibilityperiod")
    public void setAvalibilityperiod(String avalibilityperiod) {
        this.avalibilityperiod = avalibilityperiod;
        notifyPropertyChanged(BR.avalibilityperiod);
    }

    @Bindable
    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
