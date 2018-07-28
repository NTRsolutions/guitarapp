package com.demskigroup.guitaramps.pojo_class.user_follow_pojo;

import com.demskigroup.guitaramps.pojo_class.UserPostDataPojo;

import java.util.ArrayList;

/**
 * Created by hello on 4/21/2017.
 */
public class FollowResponseDatas
{
    /*"username":"kim",
"fullName":"Alice",
"profilePicUrl":"https://res.cloudinary.com/yelo/image/upload/v1492147455/m4zvf6nvbdbcv6f9nwun.png",
"memberPrivateFlag":null,
"userFollowRequestStatus":null,
"userStartedFollowingOn":null,
"FollowedBack":0*/
    private String username="",membername="",fullName="",fullname="",profilePicUrl="",followsFlag="",memberPrivateFlag="",userFollowRequestStatus="",userStartedFollowingOn="",FollowedBack;
    private ArrayList<UserPostDataPojo> postData;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getFollowsFlag() {
        return followsFlag;
    }

    public void setFollowsFlag(String followsFlag) {
        this.followsFlag = followsFlag;
    }

    public String getMemberPrivateFlag() {
        return memberPrivateFlag;
    }

    public void setMemberPrivateFlag(String memberPrivateFlag) {
        this.memberPrivateFlag = memberPrivateFlag;
    }

    public String getUserFollowRequestStatus() {
        return userFollowRequestStatus;
    }

    public void setUserFollowRequestStatus(String userFollowRequestStatus) {
        this.userFollowRequestStatus = userFollowRequestStatus;
    }

    public String getUserStartedFollowingOn() {
        return userStartedFollowingOn;
    }

    public void setUserStartedFollowingOn(String userStartedFollowingOn) {
        this.userStartedFollowingOn = userStartedFollowingOn;
    }

    public String getFollowedBack() {
        return FollowedBack;
    }

    public void setFollowedBack(String followedBack) {
        FollowedBack = followedBack;
    }

    public String getMembername() {
        return membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
    }

    public ArrayList<UserPostDataPojo> getPostData() {
        return postData;
    }

    public void setPostData(ArrayList<UserPostDataPojo> postData) {
        this.postData = postData;
    }
}
