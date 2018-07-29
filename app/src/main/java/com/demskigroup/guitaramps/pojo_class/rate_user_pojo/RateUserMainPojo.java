package com.demskigroup.guitaramps.pojo_class.rate_user_pojo;

/**
 * Created by hello on 11-Oct-17.
 */

public class RateUserMainPojo
{
    /*"code":200,
"message":"success, product marked as sold",
"data":{}*/

    private String code="",message="";
    private RateUserDatas data;

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

    public RateUserDatas getData() {
        return data;
    }

    public void setData(RateUserDatas data) {
        this.data = data;
    }
}
