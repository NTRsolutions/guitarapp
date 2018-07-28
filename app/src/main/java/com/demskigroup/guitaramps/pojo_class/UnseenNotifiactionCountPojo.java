package com.demskigroup.guitaramps.pojo_class;

/**
 * Created by hello on 28-Aug-17.
 */

public class UnseenNotifiactionCountPojo
{
    /*"code":200,
            "message":"success",
            "data":1*/

    private String code="",message="";
    private int data;

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

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}
