package com.demskigroup.guitaramps.pojo_class.productcategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AllMakes implements Serializable {


    @SerializedName("NodeId")
    @Expose
    private Integer NodeId;
    @SerializedName("Year")
    @Expose
    private String year;
    @SerializedName("Model")
    @Expose
    private String model;
    @SerializedName("Manufacturer_id")
    @Expose
    private String Manufacturer_id;
    @SerializedName("image")
    @Expose
    private String image;

    public Integer getNodeId() {
        return NodeId;
    }

    public void setNodeId(Integer nodeId) {
        NodeId = nodeId;
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

    public String getManufacturer_id() {
        return Manufacturer_id;
    }

    public void setManufacturer_id(String manufacturer_id) {
        Manufacturer_id = manufacturer_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }



    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if(obj instanceof AllMakes)
        {
            AllMakes temp = (AllMakes) obj;
            if(this.year.equals(temp.year))
                return true;
        }
        return false;

    }
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub

        return (this.year.hashCode());
    }
}
