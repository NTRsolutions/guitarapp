package com.yelo.com.pojo_class;

import java.util.ArrayList;

/**
 * Created by hello on 21-Nov-17.
 */

public class UserPostDataPojo
{
    /*"commenTs":null,
            "latitude":13.02863,
            "price":1000,
            "hashTags":"null",
            "containerHeight":384,
            "thumbnailImageUrl":"https://res.cloudinary.com/dxaxmyifi/image/upload/w_150,h_150/v1511259853/trsut7ufjcz1utqdkdz1.jpg",
            "postedOn":null,
            "postedByUserNodeId":2360,
            "longitude":77.589489,
            "likes":null,
            "postCaption":null,
            "containerWidth":384,
            "postId":1511259858724,
            "postNodeId":null,
            "productName":"bag",
            "taggedUserCoordinates":null,
            "currency":"INR",
            "place":"10th Cross Road, RT Nagar, Bengaluru, Karnataka 560024, India",
            "type":null,
            "usersTagged":null,
            "postLikedBy":null,
            "postLikedBy":0*/

    /*"latitude":5.340946132297079,
"likeStatus":1,
"price":8900,
"hashTags":"null",
"containerHeight":2220,
"thumbnailImageUrl":"http://res.cloudinary.com/dxaxmyifi/image/upload/q_60,w_150,h_150,c_thumb/v1511247463/u2ltopotyfjhtyanmt6c.jpg",
"postedOn":1511247464361,
"postedByUserNodeId":2348,
"longitude":-3.974937535822391,
"likes":null,
"postsType":0,
"postCaption":null,
"clickCount":0,
"containerWidth":1080,
"likedByUser":null,
"postId":1511247464932,
"postNodeId":null,
"usersTaggedInPosts":null,
"productName":"torches",
"taggedUserCoordinates":null,
"currency":"XOF",
"category":[],
"place":"D 33, Abidjan, Côte d'Ivoire",
"hasAudio":0,
"comments":null*/

    private String commenTs="",latitude="",price="",hashTags="",containerHeight="",thumbnailImageUrl="",postedOn="",postedByUserNodeId="",longitude="",
            likes="",postCaption="",containerWidth="",postId="",postNodeId="",productName="",taggedUserCoordinates="",currency="",place="",type="",usersTagged="",
            postLikedBy="",likeStatus="",postsType="",clickCount="",likedByUser="",usersTaggedInPosts="",hasAudio="",mainUrl="";

    private ArrayList<ProductCategoryDatas> category;

    public String getCommenTs() {
        return commenTs;
    }

    public void setCommenTs(String commenTs) {
        this.commenTs = commenTs;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getHashTags() {
        return hashTags;
    }

    public void setHashTags(String hashTags) {
        this.hashTags = hashTags;
    }

    public String getContainerHeight() {
        return containerHeight;
    }

    public void setContainerHeight(String containerHeight) {
        this.containerHeight = containerHeight;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    public String getPostedByUserNodeId() {
        return postedByUserNodeId;
    }

    public void setPostedByUserNodeId(String postedByUserNodeId) {
        this.postedByUserNodeId = postedByUserNodeId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getPostCaption() {
        return postCaption;
    }

    public void setPostCaption(String postCaption) {
        this.postCaption = postCaption;
    }

    public String getContainerWidth() {
        return containerWidth;
    }

    public void setContainerWidth(String containerWidth) {
        this.containerWidth = containerWidth;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostNodeId() {
        return postNodeId;
    }

    public void setPostNodeId(String postNodeId) {
        this.postNodeId = postNodeId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTaggedUserCoordinates() {
        return taggedUserCoordinates;
    }

    public void setTaggedUserCoordinates(String taggedUserCoordinates) {
        this.taggedUserCoordinates = taggedUserCoordinates;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsersTagged() {
        return usersTagged;
    }

    public void setUsersTagged(String usersTagged) {
        this.usersTagged = usersTagged;
    }

    public String getPostLikedBy() {
        return postLikedBy;
    }

    public void setPostLikedBy(String postLikedBy) {
        this.postLikedBy = postLikedBy;
    }

    public ArrayList<ProductCategoryDatas> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<ProductCategoryDatas> category) {
        this.category = category;
    }

    public String getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
    }

    public String getPostsType() {
        return postsType;
    }

    public void setPostsType(String postsType) {
        this.postsType = postsType;
    }

    public String getClickCount() {
        return clickCount;
    }

    public void setClickCount(String clickCount) {
        this.clickCount = clickCount;
    }

    public String getLikedByUser() {
        return likedByUser;
    }

    public void setLikedByUser(String likedByUser) {
        this.likedByUser = likedByUser;
    }

    public String getUsersTaggedInPosts() {
        return usersTaggedInPosts;
    }

    public void setUsersTaggedInPosts(String usersTaggedInPosts) {
        this.usersTaggedInPosts = usersTaggedInPosts;
    }

    public String getHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(String hasAudio) {
        this.hasAudio = hasAudio;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }
}
