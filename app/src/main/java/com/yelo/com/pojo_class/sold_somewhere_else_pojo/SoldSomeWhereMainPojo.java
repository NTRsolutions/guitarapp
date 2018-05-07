package com.yelo.com.pojo_class.sold_somewhere_else_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 24-Jul-17.
 */

public class SoldSomeWhereMainPojo
{
    /*"code":200,
            "message":"success, product marked as sold",
            "data":{}*/

    private String code="",message="";
    private SoldSomeWhereData data;

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

    public SoldSomeWhereData getData() {
        return data;
    }

    public void setData(SoldSomeWhereData data) {
        this.data = data;
    }
}
