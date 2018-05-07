package com.yelo.com.mqttchat.mqttEventManger;
import org.json.JSONObject;

public class UpdateProductDetails
{
    private JSONObject data;
    UpdateProductDetails(JSONObject jsonObject)
    {
        this.data=jsonObject;
    }

    public JSONObject getData()
    {
        return this.data;
    }
}
