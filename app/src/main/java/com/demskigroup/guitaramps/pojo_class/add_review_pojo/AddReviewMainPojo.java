package com.demskigroup.guitaramps.pojo_class.add_review_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 25-Jul-17.
 */

public class AddReviewMainPojo
{
    /*"code":200,
            "message":"Successfully posted users Comment",
            "data":[]*/

    private String code="",message="";
    private ArrayList<AddReviewData> data;

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

    public ArrayList<AddReviewData> getData() {
        return data;
    }

    public void setData(ArrayList<AddReviewData> data) {
        this.data = data;
    }
}
