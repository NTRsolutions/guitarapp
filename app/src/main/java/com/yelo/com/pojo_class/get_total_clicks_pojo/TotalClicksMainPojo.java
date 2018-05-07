package com.yelo.com.pojo_class.get_total_clicks_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 22-Aug-17.
 */

public class TotalClicksMainPojo
{
    private String message;

    private ArrayList<TotalClicksDatas> data;

    private String code;

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public ArrayList<TotalClicksDatas> getData() {
        return data;
    }

    public void setData(ArrayList<TotalClicksDatas> data) {
        this.data = data;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [message = "+message+", data = "+data+", code = "+code+"]";
    }
}
