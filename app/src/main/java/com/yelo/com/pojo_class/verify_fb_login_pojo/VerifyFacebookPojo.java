package com.yelo.com.pojo_class.verify_fb_login_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 11-Jul-17.
 */

public class VerifyFacebookPojo
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code="",message="";
    private ArrayList<VerifyFacebookDatas> data;

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

    public ArrayList<VerifyFacebookDatas> getData() {
        return data;
    }

    public void setData(ArrayList<VerifyFacebookDatas> data) {
        this.data = data;
    }
}
