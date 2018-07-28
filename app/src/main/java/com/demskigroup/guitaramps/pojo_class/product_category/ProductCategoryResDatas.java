package com.demskigroup.guitaramps.pojo_class.product_category;

import java.io.Serializable;

/**
 * Created by hello on 2017-05-04.
 */
public class ProductCategoryResDatas implements Serializable
{
    private String categoryNodeId="",name="",deactiveimage="",activeimage="";
    private boolean isSelected;

    public String getCategoryNodeId() {
        return categoryNodeId;
    }

    public void setCategoryNodeId(String categoryNodeId) {
        this.categoryNodeId = categoryNodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeactiveimage() {
        return deactiveimage;
    }

    public void setDeactiveimage(String deactiveimage) {
        this.deactiveimage = deactiveimage;
    }

    public String getActiveimage() {
        return activeimage;
    }

    public void setActiveimage(String activeimage) {
        this.activeimage = activeimage;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
