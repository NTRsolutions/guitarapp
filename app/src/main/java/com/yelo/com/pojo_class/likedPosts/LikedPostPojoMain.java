package com.yelo.com.pojo_class.likedPosts;

import java.util.ArrayList;

/**
 * Created by hello on 4/10/2017.
 */

public class LikedPostPojoMain
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code,message;
    private ArrayList<LikedPostResponseDatas> data;

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

    public ArrayList<LikedPostResponseDatas> getData() {
        return data;
    }

    public void setData(ArrayList<LikedPostResponseDatas> data) {
        this.data = data;
    }
}
