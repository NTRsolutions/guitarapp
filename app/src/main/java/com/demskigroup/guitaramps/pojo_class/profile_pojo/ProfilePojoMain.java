package com.demskigroup.guitaramps.pojo_class.profile_pojo;

import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Created by hello on 4/10/2017.
 */

public class ProfilePojoMain
{
    /*
"code":200,
"message":"success",
"data":[]*/

    private String code="",message="";
    private ArrayList<ProfileResultDatas> data;


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

    public ArrayList<ProfileResultDatas> getData() {
        return data;
    }

    public void setData(ArrayList<ProfileResultDatas> data) {
        this.data = data;
    }
}
