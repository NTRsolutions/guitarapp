package com.demskigroup.guitaramps.pojo_class;

import java.util.ArrayList;

/**
 * Created by hello on 21-Jun-17.
 */

public class PostProductMainPojo
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code="",message="";
    private ArrayList<PostProductDatas> data;

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

    public ArrayList<PostProductDatas> getData() {
        return data;
    }

    public void setData(ArrayList<PostProductDatas> data) {
        this.data = data;
    }
}
