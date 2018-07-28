package com.demskigroup.guitaramps.pojo_class.rate_seller;

import java.util.ArrayList;

/**
 * Created by hello on 20-Oct-17.
 */

public class RateSellerPojo
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code="",message="";
    private ArrayList<RateSellerDatas> data;

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

    public ArrayList<RateSellerDatas> getData() {
        return data;
    }

    public void setData(ArrayList<RateSellerDatas> data) {
        this.data = data;
    }
}
