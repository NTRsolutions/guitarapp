package com.yelo.com.pojo_class.verify_fb_login_pojo;

/**
 * Created by hello on 12-Jul-17.
 */

public class VerifyFacebookDatas
{
    /*"facebookVerified":1,
            "facebookId":"1849809298607268",
            "googleVerified":1,
            "username":"shobhitkc"*/

    private String facebookVerified="",facebookId="",googleVerified="",username="";

    public String getFacebookVerified() {
        return facebookVerified;
    }

    public void setFacebookVerified(String facebookVerified) {
        this.facebookVerified = facebookVerified;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public String getGoogleVerified() {
        return googleVerified;
    }

    public void setGoogleVerified(String googleVerified) {
        this.googleVerified = googleVerified;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
