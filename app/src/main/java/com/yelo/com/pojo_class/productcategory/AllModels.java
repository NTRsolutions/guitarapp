package com.yelo.com.pojo_class.productcategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AllModels implements Serializable {

    @SerializedName("ModelNodeId")
    @Expose
    private Integer modelNodeId;
    @SerializedName("Name_of_manufacturer")
    @Expose
    private String nameOfManufacturer;
    @SerializedName("image")
    @Expose
    private String image;

    public Integer getModelNodeId() {
        return modelNodeId;
    }

    public void setModelNodeId(Integer modelNodeId) {
        this.modelNodeId = modelNodeId;
    }

    public String getNameOfManufacturer() {
        return nameOfManufacturer;
    }

    public void setNameOfManufacturer(String nameOfManufacturer) {
        this.nameOfManufacturer = nameOfManufacturer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
