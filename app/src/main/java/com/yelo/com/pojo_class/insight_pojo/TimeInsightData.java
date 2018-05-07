package com.yelo.com.pojo_class.insight_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 22-Aug-17.
 */

public class TimeInsightData
{
    /*"count":[],
            "day":[]*/

    private ArrayList<Integer> count;
    private ArrayList<String> day;

    public ArrayList<Integer> getCount() {
        return count;
    }

    public void setCount(ArrayList<Integer> count) {
        this.count = count;
    }

    public ArrayList<String> getDay() {
        return day;
    }

    public void setDay(ArrayList<String> day) {
        this.day = day;
    }
}
