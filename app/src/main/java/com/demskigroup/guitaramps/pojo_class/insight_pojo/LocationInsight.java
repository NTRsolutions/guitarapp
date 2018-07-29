package com.demskigroup.guitaramps.pojo_class.insight_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 22-Aug-17.
 */

public class LocationInsight
{
    /*"code":200,
            "message":"success",
            "type":3,
            "typeMessage":"location insights",
            "data":[]*/

    private String code="",message="",type="",typeMessage="";
    private ArrayList<LocationInsightData> data;

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

    public ArrayList<LocationInsightData> getData() {
        return data;
    }

    public void setData(ArrayList<LocationInsightData> data) {
        this.data = data;
    }
}
