package com.demskigroup.guitaramps.pojo_class.home_explore_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 4/5/2017.
 */

public class ExplorePojoMain
{
    /*"code":200,
            "message":"success",
            "data":[]*/
    private String code="",message="";
    private ArrayList<ExploreResponseDatas> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<ExploreResponseDatas> getData() {
        return data;
    }

    public void setData(ArrayList<ExploreResponseDatas> data) {
        this.data = data;
    }
}
