package com.demskigroup.guitaramps.pojo_class.search_people_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 28-Jun-17.
 */

public class SearchPeopleMainPojo
{

    /*"code":200,
"message":"document updated",
"data":[]*/

    private String code="",message="";
    private ArrayList<SearchPeopleUsers> data;

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

    public ArrayList<SearchPeopleUsers> getData() {
        return data;
    }

    public void setData(ArrayList<SearchPeopleUsers> data) {
        this.data = data;
    }
}
