package com.demskigroup.guitaramps.pojo_class.insight_pojo;

/**
 * Created by hello on 22-Aug-17.
 */

public class TimeInsight
{
    /*"code":200,
            "message":"success",
            "type":2,
            "typeMessage":"time frame",
            "data":{}*/

    private String code="",message="",type="",typeMessage="";
    private TimeInsightData data;

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

    public TimeInsightData getData() {
        return data;
    }

    public void setData(TimeInsightData data) {
        this.data = data;
    }
}
