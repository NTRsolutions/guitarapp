package com.demskigroup.guitaramps.pojo_class.facebook_friends_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 04-Jul-17.
 */

public class FacebookFriendsData
{
    /*"membername":"moni",
            "memberPrivate":null,
            "followRequestStatus":null,
            "pushToken":null,
            "phoneNumber":"+91985698569856",
            "deviceType":"1",
            "fullname":"Monika",
            "profilePicUrl":"https://graph.facebook.com/1849809298607268/picture?type=large&return_ssl_resources=1",
            "facebookId":"1849809298607268",
            "memberPosts":[]*/

    private String membername="",memberPrivate="",followRequestStatus="",pushToken="",phoneNumber="",deviceType="",fullname="",profilePicUrl="",facebookId="";
    private ArrayList<FacebookFriendsPosts> memberPosts;

    public String getMembername() {
        return membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
    }

    public String getMemberPrivate() {
        return memberPrivate;
    }

    public void setMemberPrivate(String memberPrivate) {
        this.memberPrivate = memberPrivate;
    }

    public String getFollowRequestStatus() {
        return followRequestStatus;
    }

    public void setFollowRequestStatus(String followRequestStatus) {
        this.followRequestStatus = followRequestStatus;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
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

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public ArrayList<FacebookFriendsPosts> getMemberPosts() {
        return memberPosts;
    }

    public void setMemberPosts(ArrayList<FacebookFriendsPosts> memberPosts) {
        this.memberPosts = memberPosts;
    }
}
