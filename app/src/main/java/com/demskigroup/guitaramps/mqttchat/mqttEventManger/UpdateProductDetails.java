package com.demskigroup.guitaramps.mqttchat.mqttEventManger;
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
