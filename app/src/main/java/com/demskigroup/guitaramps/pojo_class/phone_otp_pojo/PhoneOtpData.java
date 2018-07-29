package com.demskigroup.guitaramps.pojo_class.phone_otp_pojo;

/**
 * Created by hello on 27-Jun-17.
 */

public class PhoneOtpData
{
    /*"status":400,
            "message":"The 'To' number d is not a valid phone number.",
            "code":21211,
            "moreInfo":"https://www.twilio.com/docs/errors/21211"*/

    private String status="",message="",code="",moreInfo="";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }
}
