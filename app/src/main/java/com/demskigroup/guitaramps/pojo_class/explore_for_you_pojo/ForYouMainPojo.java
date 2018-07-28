package com.demskigroup.guitaramps.pojo_class.explore_for_you_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 4/17/2017.
 */

public class ForYouMainPojo
{
    /*"code":200,
            "message":"success",
            "data":[],
            "followRequestCount":[]*/

    private String code="",message="";
    private ArrayList<ForYouResposeDatas> data;

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

    public ArrayList<ForYouResposeDatas> getData() {
        return data;
    }

    public void setData(ArrayList<ForYouResposeDatas> data) {
        this.data = data;
    }
}
