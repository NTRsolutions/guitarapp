package com.demskigroup.guitaramps.pojo_class.profile_sold_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 26-Oct-17.
 */

public class ProfileSoldMainPojo
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code="",message="";
    private ArrayList<ProfileSoldDatas> data;

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

    public ArrayList<ProfileSoldDatas> getData() {
        return data;
    }

    public void setData(ArrayList<ProfileSoldDatas> data) {
        this.data = data;
    }
}
