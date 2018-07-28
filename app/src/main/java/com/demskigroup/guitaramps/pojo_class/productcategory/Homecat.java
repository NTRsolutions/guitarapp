package com.demskigroup.guitaramps.pojo_class.productcategory;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Homecat implements Serializable {


    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("HomeCategories")
    @Expose
    private List<HomeCategory> homeCategories = null;
    @SerializedName("YearsForManufacturer")
    @Expose
    private List<YearsForManufacturer> yearsForManufacturer = null;

    @SerializedName("AllModels")
    @Expose
    private List<AllModels> allModels = null;

    @SerializedName("AllMakes")
    @Expose
    private List<AllMakes> allMakes = null;


    @SerializedName("Searchamp")
    @Expose
    private List<Searchamp> searchamp = null;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<HomeCategory> getHomeCategories() {
        return homeCategories;
    }

    public void setHomeCategories(List<HomeCategory> homeCategories) {
        this.homeCategories = homeCategories;
    }

    public List<YearsForManufacturer> getYearsForManufacturer() {
        return yearsForManufacturer;
    }

    public void setYearsForManufacturer(List<YearsForManufacturer> yearsForManufacturer) {
        this.yearsForManufacturer = yearsForManufacturer;
    }

    public List<AllModels> getAllModels() {
        return allModels;
    }

    public void setAllModels(List<AllModels> allModels) {
        this.allModels = allModels;
    }

    public List<Searchamp> getSearchamp() {
        return searchamp;
    }

    public void setSearchamp(List<Searchamp> searchamp) {
        this.searchamp = searchamp;
    }

    public List<AllMakes> getAllMakes() {
        return allMakes;
    }

    public void setAllMakes(List<AllMakes> allMakes) {
        this.allMakes = allMakes;
    }
}
