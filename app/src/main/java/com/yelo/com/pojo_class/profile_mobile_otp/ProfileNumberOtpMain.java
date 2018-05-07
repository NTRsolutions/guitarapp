package com.yelo.com.pojo_class.profile_mobile_otp;

import com.yelo.com.main.activity.ProfileNumberVerifyActivity;

/**
 * Created by hello on 11-Jul-17.
 */

public class ProfileNumberOtpMain
{
    /* "code":200,
             "message":"Success, OTP Sent!",
             "data":{}*/
    private String code="",message="";
    private ProfileNumberOtpData data;

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

    public ProfileNumberOtpData getData() {
        return data;
    }

    public void setData(ProfileNumberOtpData data) {
        this.data = data;
    }
}
