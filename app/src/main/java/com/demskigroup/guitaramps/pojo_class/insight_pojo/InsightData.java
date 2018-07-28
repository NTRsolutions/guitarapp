package com.demskigroup.guitaramps.pojo_class.insight_pojo;

/**
 * Created by hello on 22-Aug-17.
 */

public class InsightData
{
    /*"code":200,
            "basicInsight":{}*/
    // timeInsight{}, locationInsight{}

    private String code="";
    private BasicInsight basicInsight;
    private TimeInsight timeInsight;
    private LocationInsight locationInsight;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BasicInsight getBasicInsight() {
        return basicInsight;
    }

    public void setBasicInsight(BasicInsight basicInsight) {
        this.basicInsight = basicInsight;
    }

    public TimeInsight getTimeInsight() {
        return timeInsight;
    }

    public void setTimeInsight(TimeInsight timeInsight) {
        this.timeInsight = timeInsight;
    }

    public LocationInsight getLocationInsight() {
        return locationInsight;
    }

    public void setLocationInsight(LocationInsight locationInsight) {
        this.locationInsight = locationInsight;
    }
}
