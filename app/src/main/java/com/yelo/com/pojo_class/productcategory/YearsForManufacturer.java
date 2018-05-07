package com.yelo.com.pojo_class.productcategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class YearsForManufacturer implements Serializable {

    @SerializedName("YearNodeId")
    @Expose
    private Integer yearNodeId;
    @SerializedName("Year")
    @Expose
    private Integer year;

    public Integer getYearNodeId() {
        return yearNodeId;
    }

    public void setYearNodeId(Integer yearNodeId) {
        this.yearNodeId = yearNodeId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

}
