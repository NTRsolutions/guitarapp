package com.demskigroup.guitaramps.pojo_class.discovery_people_pojo;

import com.demskigroup.guitaramps.pojo_class.UserPostDataPojo;

import java.util.ArrayList;

/**
 * Created by hello on 4/27/2017.
 */
public class DiscoverPeopleResponse
{

    /*"followsFlag":0,
"postData":[],
"postedByUserName":"zhaoxc",
"postedByUserFullName":"Yueyue Zhao",
"profilePicUrl":"https://lh3.googleusercontent.com/-HN0bPUJMl3A/AAAAAAAAAAI/AAAAAAAAAAA/AMp5VUoDgphZBY9nYzDOGnZI8BdUalqxTg/s80/photo.jpg",
"privateProfile":null,
"postedByUserEmail":"zhaoxc1115@gmail.com"*/

    private String followsFlag="",postedByUserName="",postedByUserFullName="",profilePicUrl="",privateProfile="",postedByUserEmail="";
    private ArrayList<UserPostDataPojo> postData;

    public String getFollowsFlag() {
        return followsFlag;
    }

    public void setFollowsFlag(String followsFlag) {
        this.followsFlag = followsFlag;
    }

    public String getPostedByUserName() {
        return postedByUserName;
    }

    public void setPostedByUserName(String postedByUserName) {
        this.postedByUserName = postedByUserName;
    }

    public String getPostedByUserFullName() {
        return postedByUserFullName;
    }

    public void setPostedByUserFullName(String postedByUserFullName) {
        this.postedByUserFullName = postedByUserFullName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getPrivateProfile() {
        return privateProfile;
    }

    public void setPrivateProfile(String privateProfile) {
        this.privateProfile = privateProfile;
    }

    public String getPostedByUserEmail() {
        return postedByUserEmail;
    }

    public void setPostedByUserEmail(String postedByUserEmail) {
        this.postedByUserEmail = postedByUserEmail;
    }

    public ArrayList<UserPostDataPojo> getPostData() {
        return postData;
    }

    public void setPostData(ArrayList<UserPostDataPojo> postData) {
        this.postData = postData;
    }
}
