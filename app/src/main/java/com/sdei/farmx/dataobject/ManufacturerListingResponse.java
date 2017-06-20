package com.sdei.farmx.dataobject;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ManufacturerListingResponse {

    @JsonProperty("manufacturers")
    private ArrayList<Manufacturer> manufacturers = null;

    @JsonProperty("total")
    private Integer total;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("manufacturers")
    public ArrayList<Manufacturer> getManufacturer() {
        return manufacturers;
    }

    @JsonProperty("manufacturers")
    public void setManufacturer(ArrayList<Manufacturer> inputs) {
        this.manufacturers = inputs;
    }

    @JsonProperty("total")
    public Integer getTotal() {
        return total;
    }

    @JsonProperty("total")
    public void setTotal(Integer total) {
        this.total = total;
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
