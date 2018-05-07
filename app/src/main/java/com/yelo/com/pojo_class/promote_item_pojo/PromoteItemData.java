package com.yelo.com.pojo_class.promote_item_pojo;

/**
 * Created by hello on 30-Aug-17.
 */

public class PromoteItemData
{
    /*"price":1,
            "uniqueViews":100,
            "name":"100 Unique Clicks",
            "planId":1503151128429*/

    private String price="",uniqueViews="",name="",planId="";
    private boolean isItemSelected;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUniqueViews() {
        return uniqueViews;
    }

    public void setUniqueViews(String uniqueViews) {
        this.uniqueViews = uniqueViews;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public boolean isItemSelected() {
        return isItemSelected;
    }

    public void setItemSelected(boolean itemSelected) {
        isItemSelected = itemSelected;
    }
}
