package com.demskigroup.guitaramps.pojo_class.phone_otp_pojo;

/**
 * Created by hello on 27-Jun-17.
 */

public class PhoneOtpMainPojo
{
    /*"code":500,
"message":"error sending otp",
"error":{}*/
    private String code="",message="",data="";
    private PhoneOtpData error;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public PhoneOtpData getError() {
        return error;
    }

    public void setError(PhoneOtpData error) {
        this.error = error;
    }
}
