package com.demskigroup.guitaramps.pojo_class;

/**
 * Created by hello on 27-Jun-17.
 */

public class EmailCheckPojo
{
    /*"code":409,
            "message":"username already registered"*/

    private String code="",message="";

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
}
