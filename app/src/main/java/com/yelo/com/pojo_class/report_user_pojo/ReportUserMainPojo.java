package com.yelo.com.pojo_class.report_user_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 28-Jul-17.
 */

public class ReportUserMainPojo
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code="",message="";
    private ArrayList<ReportUserData> data;

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

    public ArrayList<ReportUserData> getData() {
        return data;
    }

    public void setData(ArrayList<ReportUserData> data) {
        this.data = data;
    }
}
