package com.demskigroup.guitaramps.pojo_class.verify_email_pojo;

/**
 * Created by hello on 12-Jul-17.
 */

public class VerifyEmailMain
{
    /*"code":200,
            "message":"verification link sent",
            "result":{}*/

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
