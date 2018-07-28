package com.demskigroup.guitaramps.pojo_class.facebook_friends_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 04-Jul-17.
 */

public class FacebookFriendsMain
{
    /*"code":200,
            "message":"Success!",
            "facebookUsers":[]*/
    private String code="",message="";
    private ArrayList<FacebookFriendsData> facebookUsers;

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

    public ArrayList<FacebookFriendsData> getFacebookUsers() {
        return facebookUsers;
    }

    public void setFacebookUsers(ArrayList<FacebookFriendsData> facebookUsers) {
        this.facebookUsers = facebookUsers;
    }
}
