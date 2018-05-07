package com.yelo.com.pojo_class.accepted_offer;

import java.util.ArrayList;

/**
 * Created by hello on 13-Jul-17.
 */

public class AcceptedOfferMainPojo
{
    /*"code":200,
            "message":"success",
            "data":[]*/

    private String code="",message="",messgae="";
    private ArrayList<AcceptedOfferDatas> data;

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

    public ArrayList<AcceptedOfferDatas> getData() {
        return data;
    }

    public void setData(ArrayList<AcceptedOfferDatas> data) {
        this.data = data;
    }

    public String getMessgae() {
        return messgae;
    }

    public void setMessgae(String messgae) {
        this.messgae = messgae;
    }
}
