package com.demskigroup.guitaramps.pojo_class.product_review;

import java.util.ArrayList;

/**
 * Created by hello on 09-Jun-17.
 */

public class ProductReviewMainPojo
{
    /*"code":200,
            "message":"Success",
            "result":[]*/

    private String code="",message="";
    private ArrayList<ProductReviewResult> result;

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

    public ArrayList<ProductReviewResult> getResult() {
        return result;
    }

    public void setResult(ArrayList<ProductReviewResult> result) {
        this.result = result;
    }
}
