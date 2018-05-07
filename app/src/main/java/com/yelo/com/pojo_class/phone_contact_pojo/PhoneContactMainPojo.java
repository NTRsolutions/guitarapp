package com.yelo.com.pojo_class.phone_contact_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 04-Jul-17.
 */

public class PhoneContactMainPojo
{
        /*"code":200,
        "message":"Success",
        "data":[]*/

    private String code="",message="";
    private ArrayList<PhoneContactData> data;

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

    public ArrayList<PhoneContactData> getData() {
        return data;
    }

    public void setData(ArrayList<PhoneContactData> data) {
        this.data = data;
    }
}
