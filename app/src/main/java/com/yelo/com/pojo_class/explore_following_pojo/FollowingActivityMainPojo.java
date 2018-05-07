package com.yelo.com.pojo_class.explore_following_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 4/17/2017.
 */

public class FollowingActivityMainPojo
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code="",message="";
    private ArrayList<FollowingResponseDatas> data;

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

    public ArrayList<FollowingResponseDatas> getData() {
        return data;
    }

    public void setData(ArrayList<FollowingResponseDatas> data) {
        this.data = data;
    }
}
