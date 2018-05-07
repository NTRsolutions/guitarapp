package com.yelo.com.pojo_class.verify_gplus_pojo;

/**
 * Created by hello on 12-Jul-17.
 */

class VerifyLoginWithGplusData
{
    /*"facebookVerified": 1,
            "facebookId": "1853784864870701",
            "googleVerified": 1,
            "googleId": "116437944936600396092",
            "username": "shobhitkc"*/
    private String facebookVerified="",facebookId="",googleVerified="",googleId="",username="";

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

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
