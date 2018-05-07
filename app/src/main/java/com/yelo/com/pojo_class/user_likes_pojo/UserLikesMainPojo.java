package com.yelo.com.pojo_class.user_likes_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 4/28/2017.
 */

public class UserLikesMainPojo
{
    /*"code":200,
            "message":"Success",
            "data":[]*/

    private String code="",message="";
    private ArrayList<UserLikesResponseDatas> data;

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

    public ArrayList<UserLikesResponseDatas> getData() {
        return data;
    }

    public void setData(ArrayList<UserLikesResponseDatas> data) {
        this.data = data;
    }
}
