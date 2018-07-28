package com.demskigroup.guitaramps.pojo_class.product_details_pojo;

import com.demskigroup.guitaramps.pojo_class.ProductCategoryDatas;
import com.demskigroup.guitaramps.pojo_class.home_explore_pojo.ExploreLikedByUsersDatas;

import java.util.ArrayList;

/**
 * Created by hello on 4/20/2017.
 */
public class ProductResponseDatas
{
    /*"userId":46,
            "memberId":57,
            "username":"jayz",
            "profilePicUrl":"https://res.cloudinary.com/yelo/image/upload/v1491806167/zdmagazzoinsalkstfxa.png",
            "userFullName":"jay Rathor",
            "membername":"mahadev",
            "memberProfilePicUrl":"https://res.cloudinary.com/yelo/image/upload/v1492582549/apcnk3jajudzibcss38u.png",
            "memberFullName":"mahadev",
            "followRequestStatus":null,
            "postType":0,
            "postedOn":1492683468941,
            "containerWidth":960,
            "containerHeight":960,
            "mainUrl":"http://res.cloudinary.com/yelo/image/upload/v1492683468/dcifxyf4f5sdozrexhsv.jpg",
            "postId":1492683469668,
            "productsTaggedCoordinates":null,
            "hasAudio":0,
            "productsTagged":null,
            "longitude":77.58929,
            "latitude":13.028318,
            "place":"3Embed Software Technologies",
            "thumbnailImageUrl":"http://res.cloudinary.com/yelo/image/upload/w_150,h_150/v1492683468/dcifxyf4f5sdozrexhsv.jpg",
            "hashTags":"null",
            "title":null,
            "description":null,
            "negotiable":1,
            "condition":"Used(normal wear)",
            "productUrl":null,
            "price":8000,
            "currency":"INR",
            "productName":"iphone 4s",
            "likes":0,
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
            "imageCount":1,
            "commentData":[],
            "categoryData":[],
            "likedByUsers":[],
            "likeStatus":0*/

    private String userId="",memberId="",username="",profilePicUrl="",userFullName="",membername="",memberProfilePicUrl="",memberFullName,followRequestStatus="",postType="",
            postedOn="",containerWidth="",containerHeight="",mainUrl="",postId="",productsTaggedCoordinates="",hasAudio="",productsTagged="",longitude="",latitude="",place="",thumbnailImageUrl="",
            hashTags="",title="",description="",negotiable="",condition="",price="",currency="",productName="",likes="",thumbnailUrl1="",imageUrl1="",containerHeight1="",containerWidth1="",
            imageUrl2="",thumbnailUrl2="",containerHeight2="",containerWidth2="",thumbnailUrl3="",imageUrl3="",imageUrl4="",containerHeight3="",containerWidth3="",thumbnailUrl4="",containerHeight4="",
            containerWidth4="",imageCount="",likeStatus="",clickCount="",city="",countrySname="",productUrl="",
            cloudinaryPublicId="",cloudinaryPublicId1="",cloudinaryPublicId2="",cloudinaryPublicId3="",cloudinaryPublicId4="";

    private String userMqttId="";
    String make,year,model;
    private int offerAccepted;

    public int getOfferAccepted()
    {
        return offerAccepted;
    }
    public void setOfferAccepted(int offerAccepted)
    {
        this.offerAccepted = offerAccepted;
    }
    public double getOfferPrice() {
        return offerPrice;
    }
    public void setOfferPrice(double offerPrice) {
        this.offerPrice = offerPrice;
    }
    private double offerPrice;

    public int getIsSold() {
        return sold;
    }

    public void setIsSold(int sold) {
        this.sold = sold;
    }

    private int sold;
    private String memberMqttId="";
    private ArrayList<ProductCategoryDatas> categoryData;
    private ArrayList<ExploreLikedByUsersDatas> likedByUsers;

    public String getMemberMqttId() {
        return memberMqttId;
    }
    public void setMemberMqttId(String memberMqttId) {
        this.memberMqttId = memberMqttId;
    }
    public String getUserMqttId() {
        return userMqttId;
    }
    public void setUserMqttId(String userMqttId) {
        this.userMqttId = userMqttId;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getMembername() {
        return membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
    }

    public String getMemberProfilePicUrl() {
        return memberProfilePicUrl;
    }

    public void setMemberProfilePicUrl(String memberProfilePicUrl) {
        this.memberProfilePicUrl = memberProfilePicUrl;
    }

    public String getMemberFullName() {
        return memberFullName;
    }

    public void setMemberFullName(String memberFullName) {
        this.memberFullName = memberFullName;
    }

    public String getFollowRequestStatus() {
        return followRequestStatus;
    }

    public void setFollowRequestStatus(String followRequestStatus) {
        this.followRequestStatus = followRequestStatus;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
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

    public String getProductsTagged() {
        return productsTagged;
    }

    public void setProductsTagged(String productsTagged) {
        this.productsTagged = productsTagged;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
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

    public String getImageUrl4() {
        return imageUrl4;
    }

    public void setImageUrl4(String imageUrl4) {
        this.imageUrl4 = imageUrl4;
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

    public String getImageCount() {
        return imageCount;
    }

    public void setImageCount(String imageCount) {
        this.imageCount = imageCount;
    }

    public String getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
    }

    public int getSold() {
        return sold;
    }

    public void setSold(int sold) {
        this.sold = sold;
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

    public String getClickCount() {
        return clickCount;
    }

    public void setClickCount(String clickCount) {
        this.clickCount = clickCount;
    }

    public String getContainerWidth3() {
        return containerWidth3;
    }

    public void setContainerWidth3(String containerWidth3) {
        this.containerWidth3 = containerWidth3;
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

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
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

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
