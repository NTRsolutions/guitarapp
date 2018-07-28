package com.demskigroup.guitaramps.pojo_class.verify_paypal_link;

import java.util.ArrayList;

/**
 * Created by hello on 25-Jul-17.
 */

public class VerifyPaypalMainPojo
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code="",message="";
    private ArrayList<VerifyPaypalDatas> data;

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

    public ArrayList<VerifyPaypalDatas> getData() {
        return data;
    }

    public void setData(ArrayList<VerifyPaypalDatas> data) {
        this.data = data;
    }
}
