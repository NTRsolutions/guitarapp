package com.demskigroup.guitaramps.pojo_class.insight_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 22-Aug-17.
 */

public class InsightMainPojo
{
    /*"code":200,
            "data":[]*/

    private String code="";
    private ArrayList<InsightData> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<InsightData> getData() {
        return data;
    }

    public void setData(ArrayList<InsightData> data) {
        this.data = data;
    }
}
