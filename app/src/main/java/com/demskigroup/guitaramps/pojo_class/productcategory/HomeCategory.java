package com.demskigroup.guitaramps.pojo_class.productcategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class HomeCategory implements Serializable {

    @SerializedName("HomecategoryNodeId")
    @Expose
    private Integer homecategoryNodeId;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    public Integer getHomecategoryNodeId() {
        return homecategoryNodeId;
    }

    public void setHomecategoryNodeId(Integer homecategoryNodeId) {
        this.homecategoryNodeId = homecategoryNodeId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
