package com.demskigroup.guitaramps.pojo_class.profile_selling_pojo;

import com.demskigroup.guitaramps.pojo_class.ProductCategoryDatas;

import java.util.ArrayList;

/**
 * Created by hello on 26-Oct-17.
 */

public class ProfileSellingData
{
    /*"postNodeId":1924,
            "label":[],
            "isPromoted":0,
            "planId":null,
            "likes":null,
            "mainUrl":"http://res.cloudinary.com/dxaxmyifi/image/upload/v1509005518/vf7ldpd5hwy98e52tcbv.jpg",
            "postLikedBy":null,
            "place":"19, 1st Main Road, RBI Colony, Hebbal, Bengaluru, Karnataka 560024, India",
            "thumbnailImageUrl":"http://res.cloudinary.com/dxaxmyifi/image/upload/q_60,w_150,h_150,c_thumb/v1509005518/vf7ldpd5hwy98e52tcbv.jpg",
            "postId":1509005520480,
            "productsTagged":null,
            "productsTaggedCoordinates":null,
            "hasAudio":0,
            "containerHeight":4160,
            "containerWidth":3120,
            "hashTags":"null",
            "postCaption":null,
            "latitude":13.0285743,
            "longitude":77.5893994,
            "thumbnailUrl1":null,
            "imageUrl1":null,
            "containerHeight1":null,
            "containerWidth1":null,
            "imageUrl2":null,
            "thumbnailUrl2":null,
            "containerHeight2":null,
            "containerWidth2":null,
            "thumbnailUrl3":null,
            "imageUrl3":null,
            "containerHeight3":null,
            "containerWidth3":null,
            "thumbnailUrl4":null,
            "imageUrl4":null,
            "containerHeight4":null,
            "containerWidth4":null,
            "postsType":0,
            "postedOn":1509005520162,
            "likeStatus":0,
            "sold":0,
            "productUrl":null,
            "description":"black color",
            "negotiable":1,
            "condition":"For Parts",
            "price":1000,
            "currency":"USD",
            "productName":"keyboard & mouse",
            "totalComments":0,
            "commentData":[],
            "categoryData":[]*/

    private String postNodeId= "",isPromoted="",planId="",likes="",mainUrl="",postLikedBy="",place="",thumbnailImageUrl="",postId="",productsTagged="",productsTaggedCoordinates="",
            hasAudio="",containerHeight="",containerWidth="",hashTags="",postCaption="",latitude="",longitude="",thumbnailUrl1="",imageUrl1="",containerHeight1="",containerWidth1="",imageUrl2="",
            thumbnailUrl2="",containerHeight2="",containerWidth2="",thumbnailUrl3="",imageUrl3="",containerHeight3="",containerWidth3="",thumbnailUrl4="",imageUrl4="",containerHeight4="",
            containerWidth4="",postsType="",postedOn="",likeStatus="",sold="",productUrl="",description="",negotiable="",condition="",price="",currency="",productName="",totalComments="",
            cloudinaryPublicId="",cloudinaryPublicId1="",cloudinaryPublicId2="",cloudinaryPublicId3="",cloudinaryPublicId4="";

    private boolean isToRemoveSellingItem=false;

    private ArrayList<ProductCategoryDatas> categoryData;

    public String getPostNodeId() {
        return postNodeId;
    }

    public void setPostNodeId(String postNodeId) {
        this.postNodeId = postNodeId;
    }

    public String getIsPromoted() {
        return isPromoted;
    }

    public void setIsPromoted(String isPromoted) {
        this.isPromoted = isPromoted;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
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

    public String getProductsTagged() {
        return productsTagged;
    }

    public void setProductsTagged(String productsTagged) {
        this.productsTagged = productsTagged;
    }

    public String getProductsTaggedCoordinates() {
        return productsTaggedCoordinates;
    }

    public void setProductsTaggedCoordinates(String productsTaggedCoordinates) {
        this.productsTaggedCoordinates = productsTaggedCoordinates;
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

    public String getThumbnailUrl1() {
        return thumbnailUrl1;
    }

    public void setThumbnailUrl1(String thumbnailUrl1) {
        this.thumbnailUrl1 = thumbnailUrl1;
    }

    public String getImageUrl1() {
        return imageUrl1;
    }

    public void setImageUrl1(String imageUrl1) {
        this.imageUrl1 = imageUrl1;
    }

    public String getContainerHeight1() {
        return containerHeight1;
    }

    public void setContainerHeight1(String containerHeight1) {
        this.containerHeight1 = containerHeight1;
    }

    public String getContainerWidth1() {
        return containerWidth1;
    }

    public void setContainerWidth1(String containerWidth1) {
        this.containerWidth1 = containerWidth1;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }

    public String getThumbnailUrl2() {
        return thumbnailUrl2;
    }

    public void setThumbnailUrl2(String thumbnailUrl2) {
        this.thumbnailUrl2 = thumbnailUrl2;
    }

    public String getContainerHeight2() {
        return containerHeight2;
    }

    public void setContainerHeight2(String containerHeight2) {
        this.containerHeight2 = containerHeight2;
    }

    public String getContainerWidth2() {
        return containerWidth2;
    }

    public void setContainerWidth2(String containerWidth2) {
        this.containerWidth2 = containerWidth2;
    }

    public String getThumbnailUrl3() {
        return thumbnailUrl3;
    }

    public void setThumbnailUrl3(String thumbnailUrl3) {
        this.thumbnailUrl3 = thumbnailUrl3;
    }

    public String getImageUrl3() {
        return imageUrl3;
    }

    public void setImageUrl3(String imageUrl3) {
        this.imageUrl3 = imageUrl3;
    }

    public String getContainerHeight3() {
        return containerHeight3;
    }

    public void setContainerHeight3(String containerHeight3) {
        this.containerHeight3 = containerHeight3;
    }

    public String getContainerWidth3() {
        return containerWidth3;
    }

    public void setContainerWidth3(String containerWidth3) {
        this.containerWidth3 = containerWidth3;
    }

    public String getThumbnailUrl4() {
        return thumbnailUrl4;
    }

    public void setThumbnailUrl4(String thumbnailUrl4) {
        this.thumbnailUrl4 = thumbnailUrl4;
    }

    public String getImageUrl4() {
        return imageUrl4;
    }

    public void setImageUrl4(String imageUrl4) {
        this.imageUrl4 = imageUrl4;
    }

    public String getContainerHeight4() {
        return containerHeight4;
    }

    public void setContainerHeight4(String containerHeight4) {
        this.containerHeight4 = containerHeight4;
    }

    public String getContainerWidth4() {
        return containerWidth4;
    }

    public void setContainerWidth4(String containerWidth4) {
        this.containerWidth4 = containerWidth4;
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

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNegotiable() {
        return negotiable;
    }

    public void setNegotiable(String negotiable) {
        this.negotiable = negotiable;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
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

    public ArrayList<ProductCategoryDatas> getCategoryData() {
        return categoryData;
    }

    public void setCategoryData(ArrayList<ProductCategoryDatas> categoryData) {
        this.categoryData = categoryData;
    }

    public boolean isToRemoveSellingItem() {
        return isToRemoveSellingItem;
    }

    public void setToRemoveSellingItem(boolean toRemoveSellingItem) {
        isToRemoveSellingItem = toRemoveSellingItem;
    }

    public String getCloudinaryPublicId() {
        return cloudinaryPublicId;
    }

    public void setCloudinaryPublicId(String cloudinaryPublicId) {
        this.cloudinaryPublicId = cloudinaryPublicId;
    }

    public String getCloudinaryPublicId1() {
        return cloudinaryPublicId1;
    }

    public void setCloudinaryPublicId1(String cloudinaryPublicId1) {
        this.cloudinaryPublicId1 = cloudinaryPublicId1;
    }

    public String getCloudinaryPublicId2() {
        return cloudinaryPublicId2;
    }

    public void setCloudinaryPublicId2(String cloudinaryPublicId2) {
        this.cloudinaryPublicId2 = cloudinaryPublicId2;
    }

    public String getCloudinaryPublicId3() {
        return cloudinaryPublicId3;
    }

    public void setCloudinaryPublicId3(String cloudinaryPublicId3) {
        this.cloudinaryPublicId3 = cloudinaryPublicId3;
    }

    public String getCloudinaryPublicId4() {
        return cloudinaryPublicId4;
    }

    public void setCloudinaryPublicId4(String cloudinaryPublicId4) {
        this.cloudinaryPublicId4 = cloudinaryPublicId4;
    }
}
