package com.demskigroup.guitaramps.pojo_class.report_product_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 13-Jun-17.
 */

public class ReportProductMain
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code="",message="";
    private ArrayList<ReportProductDatas> data;

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

    public ArrayList<ReportProductDatas> getData() {
        return data;
    }

    public void setData(ArrayList<ReportProductDatas> data) {
        this.data = data;
    }
}
