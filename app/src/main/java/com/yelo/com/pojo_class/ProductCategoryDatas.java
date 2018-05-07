package com.yelo.com.pojo_class;

import java.io.Serializable;

/**
 * Created by hello on 11-Nov-17.
 */

public class ProductCategoryDatas implements Serializable
{
    /*"category":"baby and child",
            "mainUrl":"http://138.197.65.222/public/appAssets/1496832967645.png",
            "activeImageUrl":"http://138.197.65.222/public/appAssets/1496832967650.png"*/

    private String category="",mainUrl="",activeImageUrl="";

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public String getActiveImageUrl() {
        return activeImageUrl;
    }

    public void setActiveImageUrl(String activeImageUrl) {
        this.activeImageUrl = activeImageUrl;
    }
}
