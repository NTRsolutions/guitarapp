package com.demskigroup.guitaramps.pojo_class.explore_for_you_pojo;

import com.demskigroup.guitaramps.pojo_class.UserPostDataPojo;

import java.util.ArrayList;

/**
 * Created by hello on 4/17/2017.
 */

public class ForYouResposeDatas
{
    /*"username":null,
            "membername":null,
            "notificationId":null,
            "notificationMessage":null,
            "memberIsPrivate":null,
            "createdOn":null,
            "notificationType":null,
            "seenStatus":null,
            "postId":null,
            "thumbnailImageUrl":null,
            "followRequestStatus":null,
            "memberProfilePicUrl":null*/

    private String username="",membername="",notificationId="",notificationMessage="",memberIsPrivate="",createdOn="",notificationType="",seenStatus="",postId="",
            thumbnailImageUrl="",followRequestStatus="",memberProfilePicUrl="",productName="";
    private ArrayList<UserPostDataPojo> postData;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMembername() {
        return membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getMemberIsPrivate() {
        return memberIsPrivate;
    }

    public void setMemberIsPrivate(String memberIsPrivate) {
        this.memberIsPrivate = memberIsPrivate;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getSeenStatus() {
        return seenStatus;
    }

    public void setSeenStatus(String seenStatus) {
        this.seenStatus = seenStatus;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public String getFollowRequestStatus() {
        return followRequestStatus;
    }

    public void setFollowRequestStatus(String followRequestStatus) {
        this.followRequestStatus = followRequestStatus;
    }

    public String getMemberProfilePicUrl() {
        return memberProfilePicUrl;
    }

    public void setMemberProfilePicUrl(String memberProfilePicUrl) {
        this.memberProfilePicUrl = memberProfilePicUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ArrayList<UserPostDataPojo> getPostData() {
        return postData;
    }

    public void setPostData(ArrayList<UserPostDataPojo> postData) {
        this.postData = postData;
    }
}
