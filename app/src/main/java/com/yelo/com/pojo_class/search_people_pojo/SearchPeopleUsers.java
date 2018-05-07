package com.yelo.com.pojo_class.search_people_pojo;

/**
 * Created by hello on 29-Jun-17.
 */

public class SearchPeopleUsers
{
    /*"userId":1021,
"username":"smitaaaaa",
"profilePicUrl":"defaultUrl",
"fullName":"smita"*/

    private String userId="",username="",profilePicUrl="",fullName="";

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
