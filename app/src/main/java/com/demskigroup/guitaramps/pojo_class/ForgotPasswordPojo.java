package com.demskigroup.guitaramps.pojo_class;

/**
 * Created by hello on 4/5/2017.
 */

public class ForgotPasswordPojo {
    /*"code":200,
"message":"Success! Please check your mail for password reset link"*/

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
