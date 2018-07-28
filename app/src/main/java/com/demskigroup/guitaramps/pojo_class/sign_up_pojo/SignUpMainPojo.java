package com.demskigroup.guitaramps.pojo_class.sign_up_pojo;

/**
 * Created by hello on 4/4/2017.
 */

public class SignUpMainPojo
{
    private String code="",message="";
    private SignUpResponseDatas response;

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

    public SignUpResponseDatas getResponse() {
        return response;
    }

    public void setResponse(SignUpResponseDatas response) {
        this.response = response;
    }
}
