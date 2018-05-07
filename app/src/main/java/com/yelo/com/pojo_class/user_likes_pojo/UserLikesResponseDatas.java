package com.yelo.com.pojo_class.user_likes_pojo;

import com.yelo.com.pojo_class.UserPostDataPojo;

import java.util.ArrayList;

/**
 * Created by hello on 4/28/2017.
 */
public class UserLikesResponseDatas
{
    /*"username":"jayz",
            "followStatus":1,
            "memberPrivateFlag":null,
            "userFollowRequestStatus":1,
            "userStartedFollowingOn":1487592149367,
            "fullname":"jay Rathor",
            "profilePicUrl":"https://res.cloudinary.com/yelo/image/upload/v1491806167/zdmagazzoinsalkstfxa.png",
            "likedOn":1493200524664*/

    private String username="",followStatus="",memberPrivateFlag="",userFollowRequestStatus="",userStartedFollowingOn="",fullname="",profilePicUrl="",likedOn="";
    private ArrayList<UserPostDataPojo> postData;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFollowStatus() {
        return followStatus;
    }

    public void setFollowStatus(String followStatus) {
        this.followStatus = followStatus;
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

    public String getLikedOn() {
        return likedOn;
    }

    public void setLikedOn(String likedOn) {
        this.likedOn = likedOn;
    }

    public ArrayList<UserPostDataPojo> getPostData() {
        return postData;
    }

    public void setPostData(ArrayList<UserPostDataPojo> postData) {
        this.postData = postData;
    }
}
