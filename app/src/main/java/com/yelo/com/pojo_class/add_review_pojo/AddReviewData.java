package com.yelo.com.pojo_class.add_review_pojo;

import java.util.ArrayList;

/**
 * Created by hello on 25-Jul-17.
 */

public class AddReviewData
{
    /*"postedByUserPushToken":null,
            "nodePostId":1428,
            "label":[],
            "postLikedBy":null,
            "containerWidth":512,
            "containerHeight":288,
            "mainUrl":"https://res.cloudinary.com/dxaxmyifi/image/upload/fl_progressive:steep/v1500984020/df14b9cev3q40maqaaeg.jpg",
            "postId":1500984022512,
            "taggedUserCoordinates":null,
            "hasAudio":0,
            "likes":null,
            "longitude":77.589553,
            "usersTagged":null,
            "latitude":13.028651,
            "place":"10th Cross Road, RT Nagar, Bengaluru, Karnataka 560024, India",
            "postCaption":null,
            "thumbnailImageUrl":"https://res.cloudinary.com/dxaxmyifi/image/upload/w_150,h_150/v1500984020/df14b9cev3q40maqaaeg.jpg",
            "hashTags":"null",
            "username":"shobhitkc",
            "fullName":"Shobhit Kumar",
            "profilePicUrl":"http://res.cloudinary.com/dxaxmyifi/image/upload/v1500878731/z19ejraqhh8zyy7mclru.jpg",
            "postedByUserName":"vabs",
            "commentData":[],
            "totalComments":3*/

    private String postedByUserPushToken="",nodePostId="",postLikedBy="",containerWidth="",containerHeight="",mainUrl="",postId="",taggedUserCoordinates="",
            hasAudio="",likes="",longitude="",usersTagged="",latitude="",place="",postCaption="",thumbnailImageUrl="",hashTags="",username="",fullName="",profilePicUrl="",
            postedByUserName="",totalComments="";
    private ArrayList<AddReviewCommentData> commentData;

    public String getPostedByUserPushToken() {
        return postedByUserPushToken;
    }

    public void setPostedByUserPushToken(String postedByUserPushToken) {
        this.postedByUserPushToken = postedByUserPushToken;
    }

    public String getNodePostId() {
        return nodePostId;
    }

    public void setNodePostId(String nodePostId) {
        this.nodePostId = nodePostId;
    }

    public String getPostLikedBy() {
        return postLikedBy;
    }

    public void setPostLikedBy(String postLikedBy) {
        this.postLikedBy = postLikedBy;
    }

    public String getContainerWidth() {
        return containerWidth;
    }

    public void setContainerWidth(String containerWidth) {
        this.containerWidth = containerWidth;
    }

    public String getContainerHeight() {
        return containerHeight;
    }

    public void setContainerHeight(String containerHeight) {
        this.containerHeight = containerHeight;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTaggedUserCoordinates() {
        return taggedUserCoordinates;
    }

    public void setTaggedUserCoordinates(String taggedUserCoordinates) {
        this.taggedUserCoordinates = taggedUserCoordinates;
    }

    public String getHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(String hasAudio) {
        this.hasAudio = hasAudio;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUsersTagged() {
        return usersTagged;
    }

    public void setUsersTagged(String usersTagged) {
        this.usersTagged = usersTagged;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPostCaption() {
        return postCaption;
    }

    public void setPostCaption(String postCaption) {
        this.postCaption = postCaption;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public String getHashTags() {
        return hashTags;
    }

    public void setHashTags(String hashTags) {
        this.hashTags = hashTags;
    }

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

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getPostedByUserName() {
        return postedByUserName;
    }

    public void setPostedByUserName(String postedByUserName) {
        this.postedByUserName = postedByUserName;
    }

    public String getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(String totalComments) {
        this.totalComments = totalComments;
    }

    public ArrayList<AddReviewCommentData> getCommentData() {
        return commentData;
    }

    public void setCommentData(ArrayList<AddReviewCommentData> commentData) {
        this.commentData = commentData;
    }
}
