package com.yelo.com.pojo_class.product_details_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 4/20/2017.
 */

public class ProductDetailsMain
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code="",message="";
    private ArrayList<ProductResponseDatas> data;

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

    public ArrayList<ProductResponseDatas> getData() {
        return data;
    }

    public void setData(ArrayList<ProductResponseDatas> data) {
        this.data = data;
    }
}
