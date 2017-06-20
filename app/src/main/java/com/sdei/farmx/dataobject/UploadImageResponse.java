package com.sdei.farmx.dataobject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UploadImageResponse {

    @JsonProperty("fullPath")
    private String fullPath;

    @JsonProperty("imagePath")
    private String imagePath;

    @JsonProperty("imagePath")
    public String getImagePath() {
        return imagePath;
    }

    @JsonProperty("imagePath")
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @JsonProperty("fullPath")
    public String getFullPath() {
        return fullPath;
    }

    @JsonProperty("fullPath")
    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

}
