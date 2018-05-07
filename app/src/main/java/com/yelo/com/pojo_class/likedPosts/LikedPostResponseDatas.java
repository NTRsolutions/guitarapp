package com.yelo.com.pojo_class.likedPosts;

import com.yelo.com.pojo_class.home_explore_pojo.ExploreLikedByUsersDatas;

import java.util.ArrayList;

/**
 * Created by hello on 4/10/2017.
 */
public class LikedPostResponseDatas
{
    /*"likeStatus":2,
"postNodeId":1461,
"label":[],
"likes":0,
"mainUrl":"https://res.cloudinary.com/dxaxmyifi/image/upload/fl_progressive:steep/v1499777772/i0ivsbmazzfp90dksy3h.jpg",
"place":"10th Cross Road, RT Nagar, Bengaluru, Karnataka 560024, India",
"thumbnailImageUrl":"https://res.cloudinary.com/dxaxmyifi/image/upload/w_150,h_150/v1499777772/i0ivsbmazzfp90dksy3h.jpg",
"postId":1499777774486,
"hashTags":"null",
"postCaption":null,
"imageCount":2,
"postedByUserName":"smita",
"postedByUserprofilePicUrl":"",
"postedByUserFullName":"smita",
"postsType":0,
"postedOn":1499777877415,
"latitude":13.028737,
"longitude":77.588929,
"city":"bengaluru",
"countrySname":"in",
"productsTaggedCoordinates":null,
"productsTagged":null,
"hasAudio":0,
"containerHeight":1280,
"containerWidth":720,
"thumbnailUrl1":null,
"imageUrl1":"https://res.cloudinary.com/dxaxmyifi/image/upload/fl_progressive:steep/v1499777773/vl9bdrlgav6qoqh7udzn.jpg",
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
"productUrl":null,
"description":"Ipod",
"negotiable":1,
"condition":"For Parts",
"price":25000,
"currency":"INR",
"productName":"iPod",
"sold":0,
"totalComments":0,
"commentData":[],
"categoryData":[],
"likedByUsers":[]*/

    private String likeStatus="",postNodeId="",likes="",mainUrl="",place="",thumbnailImageUrl="",postId="",hashTags="",postCaption="",imageCount="",postedByUserName="",
            postedByUserprofilePicUrl="",postedByUserFullName="",postsType="",postedOn="",latitude="",longitude="",city="",countrySname="",productsTaggedCoordinates="",productsTagged="",hasAudio="",imageUrl1="",containerHeight="",containerWidth="",containerWidth1="",
            imageUrl2="",thumbnailUrl2="",containerHeight2="",containerWidth2="",thumbnailUrl3="",imageUrl3="",containerHeight3="",thumbnailUrl4="",imageUrl4="",
            containerHeight4="",containerWidth4="",productUrl="",title="",description="",negotiable="",condition="",price="",currency="",productName="",totalComments="";
    private ArrayList<ExploreLikedByUsersDatas> likedByUsers;
    private ArrayList<LikedPostCategoryDatas> categoryData;
    private boolean isToAddLikedItem;

    public String getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
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

    public String getImageCount() {
        return imageCount;
    }

    public void setImageCount(String imageCount) {
        this.imageCount = imageCount;
    }

    public String getPostedByUserName() {
        return postedByUserName;
    }

    public void setPostedByUserName(String postedByUserName) {
        this.postedByUserName = postedByUserName;
    }

    public String getPostedByUserprofilePicUrl() {
        return postedByUserprofilePicUrl;
    }

    public void setPostedByUserprofilePicUrl(String postedByUserprofilePicUrl) {
        this.postedByUserprofilePicUrl = postedByUserprofilePicUrl;
    }

    public String getPostedByUserFullName() {
        return postedByUserFullName;
    }

    public void setPostedByUserFullName(String postedByUserFullName) {
        this.postedByUserFullName = postedByUserFullName;
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

    public String getProductsTaggedCoordinates() {
        return productsTaggedCoordinates;
    }

    public void setProductsTaggedCoordinates(String productsTaggedCoordinates) {
        this.productsTaggedCoordinates = productsTaggedCoordinates;
    }

    public String getProductsTagged() {
        return productsTagged;
    }

    public void setProductsTagged(String productsTagged) {
        this.productsTagged = productsTagged;
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

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountrySname() {
        return countrySname;
    }

    public void setCountrySname(String countrySname) {
        this.countrySname = countrySname;
    }

    public String getImageUrl1() {
        return imageUrl1;
    }

    public void setImageUrl1(String imageUrl1) {
        this.imageUrl1 = imageUrl1;
    }

    public ArrayList<ExploreLikedByUsersDatas> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(ArrayList<ExploreLikedByUsersDatas> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    public ArrayList<LikedPostCategoryDatas> getCategoryData() {
        return categoryData;
    }

    public void setCategoryData(ArrayList<LikedPostCategoryDatas> categoryData) {
        this.categoryData = categoryData;
    }

    public boolean isToAddLikedItem() {
        return isToAddLikedItem;
    }

    public void setToAddLikedItem(boolean toAddLikedItem) {
        isToAddLikedItem = toAddLikedItem;
    }

    public String getContainerWidth() {
        return containerWidth;
    }

    public void setContainerWidth(String containerWidth) {
        this.containerWidth = containerWidth;
    }
}
