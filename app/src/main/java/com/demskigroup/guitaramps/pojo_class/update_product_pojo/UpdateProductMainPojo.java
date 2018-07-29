package com.demskigroup.guitaramps.pojo_class.update_product_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 08-Sep-17.
 */

public class UpdateProductMainPojo
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code="",message="";
    private ArrayList<UpdateProductData> data;

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

    public ArrayList<UpdateProductData> getData() {
        return data;
    }

    public void setData(ArrayList<UpdateProductData> data) {
        this.data = data;
    }
}
