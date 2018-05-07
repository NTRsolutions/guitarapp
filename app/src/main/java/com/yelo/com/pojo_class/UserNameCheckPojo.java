package com.yelo.com.pojo_class;

/**
 * Created by hello on 27-Jun-17.
 */

public class UserNameCheckPojo
{
    /*"code":200,
            "message":"you can register with this username"*/

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
