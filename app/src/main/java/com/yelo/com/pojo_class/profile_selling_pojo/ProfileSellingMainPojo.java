package com.yelo.com.pojo_class.profile_selling_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 26-Oct-17.
 */

public class ProfileSellingMainPojo
{
    /*"code":200,
            "message":"success",
            "data":[]*/
    private String code="",message="";
    private ArrayList<ProfileSellingData> data;

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

    public ArrayList<ProfileSellingData> getData() {
        return data;
    }

    public void setData(ArrayList<ProfileSellingData> data) {
        this.data = data;
    }
}
