package com.demskigroup.guitaramps.pojo_class.social_frag_pojo;

import com.demskigroup.guitaramps.pojo_class.ProductCategoryDatas;
import com.demskigroup.guitaramps.pojo_class.home_explore_pojo.ExploreLikedByUsersDatas;
import java.util.ArrayList;

/**
 * Created by hello on 26-Oct-17.
 */

public class SocialDatas
{
    /*"postNodeId":1926,  "mainUrl":"http://res.cloudinary.com/dxaxmyifi/image/upload/v1509006358/lassdnhso0c5wcv3kw46.jpg",
            "imageUrl1":null,  "imageUrl2":null,  "imageUrl3":null,  "imageUrl4":null,  "usersTaggedInPosts":null,
            "place":"19, 1st Main Road, RBI Colony, Hebbal, Bengaluru, Karnataka 560024, India",
            "latitude":13.0285975,  "longitude":77.5894218,
            "thumbnailImageUrl":"http://res.cloudinary.com/dxaxmyifi/image/upload/q_60,w_150,h_150,c_thumb/v1509006358/lassdnhso0c5wcv3kw46.jpg",
            "postId":1509006363328,  "hashTags":"null",  "postCaption":null,  "postedByUserNodeId":1582,
            "membername":"sachinchat01", "profilePicUrl":"https://lh5.googleusercontent.com/-yGleOfUh0O0/AAAAAAAAAAI/AAAAAAAAU9Y/xkE3iS6DVOw/photo.jpg",
            "memberProfilePicUrl":"http://res.cloudinary.com/dxaxmyifi/image/upload/v1506092959/n14zjdcenayeuit8cvh0.jpg",
            "postsType":0,  "postedOn":1509006362185,  "postedByUserEmail":"chhc@fyyf.guug",  "containerWidth":456,
            "containerHeight":322,  "memberFullName":"Sachinchat01",  "businessProfile":null,  "hasAudio":0, "condition":"New(never used)",
            "negotiable":1, "likeStatus":0,  "taggedUserCoordinates":null,  "categoryData":[],  "productUrl":null,
            "price":999,  "currency":"INR",  "productName":"Sling bag",  "likes":0,  "likedByUsers":[],  "clickCount":11,
            "totalComments":0,  "commentData":[]*/

    private String postNodeId="",mainUrl="", imageUrl1="", imageUrl2="", imageUrl3="", imageUrl4="", usersTaggedInPosts="", place="",
            latitude="",longitude="",thumbnailImageUrl="",postId="",hashTags="",postCaption="",postedByUserNodeId="",membername="",
            profilePicUrl="",memberProfilePicUrl="",postsType="",postedOn="",postedByUserEmail="",containerWidth="",containerHeight="",
            memberFullName="",businessProfile="",hasAudio="",condition="",negotiable="",likeStatus="",taggedUserCoordinates="",productUrl="",
            price="",currency="",productName="",likes="",clickCount="",totalComments="";
    private ArrayList<ProductCategoryDatas> categoryData;
    private ArrayList<ExploreLikedByUsersDatas> likedByUsers;
    private boolean isToAddSocialData=false;

    public String getPostNodeId() {
        return postNodeId;
    }

    public void setPostNodeId(String postNodeId) {
        this.postNodeId = postNodeId;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public String getImageUrl1() {
        return imageUrl1;
    }

    public void setImageUrl1(String imageUrl1) {
        this.imageUrl1 = imageUrl1;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }

    public String getImageUrl3() {
        return imageUrl3;
    }

    public void setImageUrl3(String imageUrl3) {
        this.imageUrl3 = imageUrl3;
    }

    public String getImageUrl4() {
        return imageUrl4;
    }

    public void setImageUrl4(String imageUrl4) {
        this.imageUrl4 = imageUrl4;
    }

    public String getUsersTaggedInPosts() {
        return usersTaggedInPosts;
    }

    public void setUsersTaggedInPosts(String usersTaggedInPosts) {
        this.usersTaggedInPosts = usersTaggedInPosts;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getHashTags() {
        return hashTags;
    }

    public void setHashTags(String hashTags) {
        this.hashTags = hashTags;
    }

    public String getPostCaption() {
        return postCaption;
    }

    public void setPostCaption(String postCaption) {
        this.postCaption = postCaption;
    }

    public String getPostedByUserNodeId() {
        return postedByUserNodeId;
    }

    public void setPostedByUserNodeId(String postedByUserNodeId) {
        this.postedByUserNodeId = postedByUserNodeId;
    }

    public String getMembername() {
        return membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getMemberProfilePicUrl() {
        return memberProfilePicUrl;
    }

    public void setMemberProfilePicUrl(String memberProfilePicUrl) {
        this.memberProfilePicUrl = memberProfilePicUrl;
    }

    public String getPostsType() {
        return postsType;
    }

    public void setPostsType(String postsType) {
        this.postsType = postsType;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    public String getPostedByUserEmail() {
        return postedByUserEmail;
    }

    public void setPostedByUserEmail(String postedByUserEmail) {
        this.postedByUserEmail = postedByUserEmail;
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

    public String getMemberFullName() {
        return memberFullName;
    }

    public void setMemberFullName(String memberFullName) {
        this.memberFullName = memberFullName;
    }

    public String getBusinessProfile() {
        return businessProfile;
    }

    public void setBusinessProfile(String businessProfile) {
        this.businessProfile = businessProfile;
    }

    public String getHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(String hasAudio) {
        this.hasAudio = hasAudio;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getNegotiable() {
        return negotiable;
    }

    public void setNegotiable(String negotiable) {
        this.negotiable = negotiable;
    }

    public String getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
    }

    public String getTaggedUserCoordinates() {
        return taggedUserCoordinates;
    }

    public void setTaggedUserCoordinates(String taggedUserCoordinates) {
        this.taggedUserCoordinates = taggedUserCoordinates;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getClickCount() {
        return clickCount;
    }

    public void setClickCount(String clickCount) {
        this.clickCount = clickCount;
    }

    public String getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(String totalComments) {
        this.totalComments = totalComments;
    }


    public ArrayList<ProductCategoryDatas> getCategoryData() {
        return categoryData;
    }

    public void setCategoryData(ArrayList<ProductCategoryDatas> categoryData) {
        this.categoryData = categoryData;
    }

    public ArrayList<ExploreLikedByUsersDatas> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(ArrayList<ExploreLikedByUsersDatas> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    public boolean isToAddSocialData() {
        return isToAddSocialData;
    }

    public void setToAddSocialData(boolean toAddSocialData) {
        isToAddSocialData = toAddSocialData;
    }
}
