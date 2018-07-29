package com.demskigroup.guitaramps.pojo_class.mark_selling;

/**
 * Created by hello on 17-Jul-17.
 */

public class MarkSellingMainPojo
{
    /*"code":200,
            "message":"success",
            "data":{}*/

    private String code="",message="";
    private MarkSellingResData data;

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

    public MarkSellingResData getData() {
        return data;
    }

    public void setData(MarkSellingResData data) {
        this.data = data;
    }
}
