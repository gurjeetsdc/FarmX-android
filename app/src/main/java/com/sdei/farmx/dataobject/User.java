package com.sdei.farmx.dataobject;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdei.farmx.BR;

import java.io.Serializable;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends BaseObservable implements Serializable {

    @JsonProperty("password")
    private String password;

    private String confirmPassword;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("city")
    private String city;

    @JsonProperty("district")
    private String district;

    @JsonProperty("state")
    private String state;

    @JsonProperty("pincode")
    private String pincode;

    @JsonProperty("mobile")
    private String mobile;

    @JsonProperty("id")
    private String id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("lat")
    private float lat;

    @JsonProperty("lng")
    private float lng;

    @JsonProperty("domain")
    private String domain = "mobile";

    @JsonProperty("deviceType")
    private String deviceType = "ANDROID";

    @JsonProperty("deviceToken")
    private String deviceToken;

    @JsonProperty("otp")
    private long otp;

    @JsonProperty("access_token")
    private String access_token;

    @JsonProperty("fbId")
    private String fbId;

    @JsonProperty("gId")
    private String gId;

    @JsonProperty("providers")
    private String providers;

    @JsonProperty("mybids")
    private ArrayList<CropBid> mybids;

    @Bindable
    @JsonProperty("mybids")
    public ArrayList<CropBid> getMybids() {
        return mybids;
    }

    @JsonProperty("mybids")
    public void setMybids(ArrayList<CropBid> mybids) {
        this.mybids = mybids;
        notifyPropertyChanged(BR.mybids);
    }

    @JsonProperty("gId")
    public String getgId() {
        return gId;
    }

    @JsonProperty("gId")
    public void setgId(String gId) {
        this.gId = gId;
    }

    @JsonProperty("fbId")
    public String getFbId() {
        return fbId;
    }

    @JsonProperty("fbId")
    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    @JsonProperty("providers")
    public String getProviders() {
        return providers;
    }

    @JsonProperty("providers")
    public void setProviders(String providers) {
        this.providers = providers;
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

    @JsonIgnore
    @Bindable
    public long getOtp() {
        return otp;
    }

    @JsonProperty("access_token")
    public String getAccess_token() {
        return access_token;
    }

    @JsonProperty("access_token")
    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    @JsonProperty("otp")
    public void setOtp(long otp) {
        this.otp = otp;
        notifyPropertyChanged(BR.otp);
    }

    @JsonProperty("fullName")
    @Bindable
    public String getFullName() {
        return fullName;
    }

    @JsonProperty("fullName")
    public void setFullName(String fullName) {
        this.fullName = fullName;
        notifyPropertyChanged(BR.firstName);
    }

    @JsonProperty("firstName")
    @Bindable
    public String getFirstName() {
        return firstName;
    }

    @JsonProperty("firstName")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        notifyPropertyChanged(BR.firstName);
    }

    @JsonProperty("lastName")
    @Bindable
    public String getLastName() {
        return lastName;
    }

    @JsonProperty("lastName")
    public void setLastName(String lastName) {
        this.lastName = lastName;
        notifyPropertyChanged(BR.lastName);
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

    @JsonProperty("district")
    @Bindable
    public String getDistrict() {
        return district;
    }

    @JsonProperty("district")
    public void setDistrict(String district) {
        this.district = district;
        notifyPropertyChanged(BR.district);
    }

    @JsonProperty("state")
    @Bindable
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
        notifyPropertyChanged(BR.state);
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

    @JsonProperty("mobile")
    @Bindable
    public String getMobile() {
        return mobile;
    }

    @JsonProperty("mobile")
    public void setMobile(String mobile) {
        this.mobile = mobile;
        notifyPropertyChanged(BR.mobile);
    }

    @JsonProperty("lat")
    public float getLat() {
        return lat;
    }

    @JsonProperty("lat")
    public void setLat(float lat) {
        this.lat = lat;
    }

    @JsonProperty("lng")
    public float getLng() {
        return lng;
    }

    @JsonProperty("lng")
    public void setLng(float lng) {
        this.lng = lng;
    }

    @JsonProperty("domain")
    public String getDomain() {
        return domain;
    }

    @JsonProperty("domain")
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @JsonProperty("deviceType")
    public String getDeviceType() {
        return deviceType;
    }

    @JsonProperty("deviceType")
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    @JsonProperty("deviceToken")
    public String getDeviceToken() {
        return deviceToken;
    }

    @JsonProperty("deviceToken")
    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    @JsonProperty("username")
    @Bindable
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    @JsonProperty("password")
    @Bindable
    public String getPassword() {
        return password;
    }

    @JsonProperty("password")
    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @JsonProperty("id")
    @Bindable
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @JsonIgnore
    @Bindable
    public String getConfirmPassword() {
        return confirmPassword;
    }

    @JsonIgnore
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        notifyPropertyChanged(BR.confirmPassword);
    }

}
