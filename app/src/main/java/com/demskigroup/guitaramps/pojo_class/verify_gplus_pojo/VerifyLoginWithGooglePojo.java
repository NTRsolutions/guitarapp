package com.demskigroup.guitaramps.pojo_class.verify_gplus_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 12-Jul-17.
 */
public class VerifyLoginWithGooglePojo
{
/*"code":200,
        "message":"success",
        "data":[]*/

    private String code="",message="";
    private ArrayList<VerifyLoginWithGplusData> data;

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

    public ArrayList<VerifyLoginWithGplusData> getData() {
        return data;
    }

    public void setData(ArrayList<VerifyLoginWithGplusData> data) {
        this.data = data;
    }
}
