package com.yelo.com.pojo_class.social_frag_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 26-Oct-17.
 */

public class SocialMainPojo
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code="",message="";
    private ArrayList<SocialDatas> data;

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

    public ArrayList<SocialDatas> getData() {
        return data;
    }

    public void setData(ArrayList<SocialDatas> data) {
        this.data = data;
    }
}
