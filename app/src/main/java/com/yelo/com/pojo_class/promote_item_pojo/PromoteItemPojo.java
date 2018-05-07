package com.yelo.com.pojo_class.promote_item_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 30-Aug-17.
 */

public class PromoteItemPojo
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code="",message="";
    private ArrayList<PromoteItemData> data;

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

    public ArrayList<PromoteItemData> getData() {
        return data;
    }

    public void setData(ArrayList<PromoteItemData> data) {
        this.data = data;
    }
}
