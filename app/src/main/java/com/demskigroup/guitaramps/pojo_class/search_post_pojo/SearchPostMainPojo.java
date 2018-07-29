package com.demskigroup.guitaramps.pojo_class.search_post_pojo;

import com.demskigroup.guitaramps.pojo_class.search_people_pojo.SearchPeopleMainPojo;

import java.util.ArrayList;

/**
 * Created by hello on 29-Jun-17.
 */

public class SearchPostMainPojo
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code="",message="";
    private ArrayList<SearchPostDatas> data;

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

    public ArrayList<SearchPostDatas> getData() {
        return data;
    }

    public void setData(ArrayList<SearchPostDatas> data) {
        this.data = data;
    }
}
