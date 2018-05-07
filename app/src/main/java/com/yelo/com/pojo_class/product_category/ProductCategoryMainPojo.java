package com.yelo.com.pojo_class.product_category;

import java.util.ArrayList;

/**
 * Created by hello on 2017-05-04.
 */

public class ProductCategoryMainPojo
{
    /*"code":200,
            "message":"Succcess",
            "data":[]*/

    private String code="",message="";
    private ArrayList<ProductCategoryResDatas> data;

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

    public ArrayList<ProductCategoryResDatas> getData() {
        return data;
    }

    public void setData(ArrayList<ProductCategoryResDatas> data) {
        this.data = data;
    }
}
