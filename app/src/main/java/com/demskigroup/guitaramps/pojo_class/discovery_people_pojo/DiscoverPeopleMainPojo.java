package com.demskigroup.guitaramps.pojo_class.discovery_people_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 4/27/2017.
 */

public class DiscoverPeopleMainPojo
{

    /*"code":200,
"message":"Success",
"discoverData":[]*/

    private String code="",message="";
    private ArrayList<DiscoverPeopleResponse> discoverData;

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

    public ArrayList<DiscoverPeopleResponse> getDiscoverData() {
        return discoverData;
    }

    public void setDiscoverData(ArrayList<DiscoverPeopleResponse> discoverData) {
        this.discoverData = discoverData;
    }
}
