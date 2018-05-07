package com.yelo.com.pojo_class.phone_contact_pojo;

import com.yelo.com.main.activity.PhoneContactsActivity;

import java.util.ArrayList;

/**
 * Created by hello on 04-Jul-17.
 */

public class PhoneContactData
{
    /*"Following":0,
            "membername":"jef",
            "userId":1276,
            "profilePicUrl":"https://graph.facebook.com/1893859380903000/picture?type=large&return_ssl_resources=1",
            "fullName":"Jeffrey",
            "phoneNumber":"+919582429675",
            "memberPrivate":null,
            "followRequestStatus":null,
            "userPrivate":null,
            "postData":[]*/

    private String Following="",membername="",userId="",profilePicUrl="",fullName="",phoneNumber="",memberPrivate="",followRequestStatus="",userPrivate="";
    private ArrayList<PhoneContactPostData> postData;

    public String getFollowing() {
        return Following;
    }

    public void setFollowing(String following) {
        Following = following;
    }

    public String getMembername() {
        return membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getUserPrivate() {
        return userPrivate;
    }

    public void setUserPrivate(String userPrivate) {
        this.userPrivate = userPrivate;
    }

    public ArrayList<PhoneContactPostData> getPostData() {
        return postData;
    }

    public void setPostData(ArrayList<PhoneContactPostData> postData) {
        this.postData = postData;
    }
}
