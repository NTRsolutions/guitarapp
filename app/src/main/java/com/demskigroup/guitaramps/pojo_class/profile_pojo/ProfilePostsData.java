package com.demskigroup.guitaramps.pojo_class.profile_pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by hello on 4/10/2017.
 */
public class ProfilePostsData implements Parcelable {
    /*"postNodeId":362,
            "label":[],
            "likes":0,
            "mainUrl":"http://res.cloudinary.com/yelo/image/upload/v1491805387/h0jbmanynxnah71rxwlf.jpg",
            "postLikedBy":null,
            "place":"3Embed Software Technologies",
            "thumbnailImageUrl":"http://res.cloudinary.com/yelo/image/upload/w_150,h_150/v1491805387/h0jbmanynxnah71rxwlf.jpg",
            "postId":1491805388368,
            "usersTaggedInPosts":null,
            "taggedUserCoordinates":null,
            "hasAudio":0,
            "containerHeight":960,
            "containerWidth":960,
            "hashTags":"null",
            "postCaption":null,
            "latitude":13.028318,
            "longitude":77.58929,
            "postsType":0,
            "postedOn":1491805387921,
            "likeStatus":0,
            "category":"electronics",
            "subCategory":"laptops",
            "productUrl":null,
            "price":25000,
            "currency":"INR",
            "productName":"mac mini",
            "totalComments":0,
            "commentData":[]*/

    private String postNodeId="",likes="",mainUrl="",postLikedBy="",place="",thumbnailImageUrl="",postId="",usersTaggedInPosts="",taggedUserCoordinates="",
            hasAudio="",containerHeight="",containerWidth="",hashTags="",postCaption="",latitude="",longitude="",postsType="",postedOn="",likeStatus="",
            category="",subCategory="",productUrl="",price="",currency="",productName="",totalComments="";

    private ProfilePostsData(Parcel in) {
        postNodeId = in.readString();
        likes = in.readString();
        mainUrl = in.readString();
        postLikedBy = in.readString();
        place = in.readString();
        thumbnailImageUrl = in.readString();
        postId = in.readString();
        usersTaggedInPosts = in.readString();
        taggedUserCoordinates = in.readString();
        hasAudio = in.readString();
        containerHeight = in.readString();
        containerWidth = in.readString();
        hashTags = in.readString();
        postCaption = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        postsType = in.readString();
        postedOn = in.readString();
        likeStatus = in.readString();
        category = in.readString();
        subCategory = in.readString();
        productUrl = in.readString();
        price = in.readString();
        currency = in.readString();
        productName = in.readString();
        totalComments = in.readString();
    }

    public static final Creator<ProfilePostsData> CREATOR = new Creator<ProfilePostsData>() {
        @Override
        public ProfilePostsData createFromParcel(Parcel in) {
            return new ProfilePostsData(in);
        }

        @Override
        public ProfilePostsData[] newArray(int size) {
            return new ProfilePostsData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postNodeId);
        dest.writeString(likes);
        dest.writeString(mainUrl);
        dest.writeString(postLikedBy);
        dest.writeString(place);
        dest.writeString(thumbnailImageUrl);
        dest.writeString(postId);
        dest.writeString(usersTaggedInPosts);
        dest.writeString(taggedUserCoordinates);
        dest.writeString(hasAudio);
        dest.writeString(containerHeight);
        dest.writeString(containerWidth);
        dest.writeString(hashTags);
        dest.writeString(postCaption);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(postsType);
        dest.writeString(postedOn);
        dest.writeString(likeStatus);
        dest.writeString(category);
        dest.writeString(subCategory);
        dest.writeString(productUrl);
        dest.writeString(price);
        dest.writeString(currency);
        dest.writeString(productName);
        dest.writeString(totalComments);
    }



    public String getPostNodeId() {
        return postNodeId;
    }

    public void setPostNodeId(String postNodeId) {
        this.postNodeId = postNodeId;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public String getPostLikedBy() {
        return postLikedBy;
    }

    public void setPostLikedBy(String postLikedBy) {
        this.postLikedBy = postLikedBy;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
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

    public String getUsersTaggedInPosts() {
        return usersTaggedInPosts;
    }

    public void setUsersTaggedInPosts(String usersTaggedInPosts) {
        this.usersTaggedInPosts = usersTaggedInPosts;
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

    public String getContainerHeight() {
        return containerHeight;
    }

    public void setContainerHeight(String containerHeight) {
        this.containerHeight = containerHeight;
    }

    public String getContainerWidth() {
        return containerWidth;
    }

    public void setContainerWidth(String containerWidth) {
        this.containerWidth = containerWidth;
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

    public String getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
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

    public String getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(String totalComments) {
        this.totalComments = totalComments;
    }
}
