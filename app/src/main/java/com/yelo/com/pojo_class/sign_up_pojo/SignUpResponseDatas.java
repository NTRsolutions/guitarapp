package com.yelo.com.pojo_class.sign_up_pojo;

/**
 * Created by hello on 4/4/2017.
 */
public class SignUpResponseDatas
{
    /*"username":"dfgrtrxyut",
"userId":1080,
"authToken":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6MTA4MCwibmFtZSI6ImRmZ3J0cnh5dXQiLCJlbWFpbCI6ImhqamhqaGtoamhqQGdtYWlsLmNvbW1oIiwiaWF0IjoxNDk3MDAzMTAwLCJleHAiOjE1MDIxODcxMDB9.EXqQHqGN1zgciCi1O_v7XnbXDbsF8yJ7WViLEGPvdFc",
"logStatus":{},
"mongoId":[],
"email":"hjjhjhkhjhj@gmail.commh",
"profilePicUrl":"https://c402277.ssl.cf1.rackcdn.com/photos/2330/images/hero_small/polar-bear-hero.jpg?1345901694"*/

    private String username="",userId="",authToken="",email="",profilePicUrl="";

    public String getMqttId()
    {
        return mqttId;
    }

    public void setMqttId(String mqttId)
    {
        this.mqttId = mqttId;
    }

    private String mqttId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }
}
