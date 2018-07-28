package com.demskigroup.guitaramps.pojo_class.productcategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Searchamp implements Serializable {

    @SerializedName("SearchampNodeId")
    @Expose
    private Integer searchampNodeId;
    @SerializedName("Year")
    @Expose
    private String year;
    @SerializedName("Model")
    @Expose
    private String model;
    @SerializedName("Name_of_manufacturer")
    @Expose
    private String manufacturer;
    @SerializedName("Model_image")
    @Expose
    private String image;
    @SerializedName("category_name")
    @Expose
    private String category;

    public Integer getSearchampNodeId() {
        return searchampNodeId;
    }

    public void setSearchampNodeId(Integer searchampNodeId) {
        this.searchampNodeId = searchampNodeId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if(obj instanceof Searchamp)
        {
            Searchamp temp = (Searchamp) obj;
            if(this.manufacturer.equals(temp.manufacturer))
                return true;
        }
        return false;

    }
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub

        return (this.manufacturer.hashCode());
    }
}
