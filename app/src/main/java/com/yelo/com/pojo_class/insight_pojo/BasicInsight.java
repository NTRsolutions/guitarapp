package com.yelo.com.pojo_class.insight_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 22-Aug-17.
 */

public class BasicInsight
{
/*"code":200,
        "type":1,
        "typeMessage":"basic insights",
        "data":[]*/

    private String code="",type="",typeMessage="";
    private ArrayList<BasicInsightData> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeMessage() {
        return typeMessage;
    }

    public void setTypeMessage(String typeMessage) {
        this.typeMessage = typeMessage;
    }

    public ArrayList<BasicInsightData> getData() {
        return data;
    }

    public void setData(ArrayList<BasicInsightData> data) {
        this.data = data;
    }
}
