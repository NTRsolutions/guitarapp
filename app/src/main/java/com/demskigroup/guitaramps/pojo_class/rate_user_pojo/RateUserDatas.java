package com.demskigroup.guitaramps.pojo_class.rate_user_pojo;

import com.demskigroup.guitaramps.pojo_class.ProductCategoryDatas;

import java.util.ArrayList;

/**
 * Created by hello on 11-Oct-17.
 */

public class RateUserDatas
{
/*"username":"shubham",
"profilePicUrl":"https://lh3.googleusercontent.com/-6StF6_hkNFA/AAAAAAAAAAI/AAAAAAAAAAs/NwPWTiObXHc/photo.jpg",
"fullName":"Shubham Santuwala",
"pushToken":"fATfTFbIezg:APA91bFgBJfPF2WV0IWFEdfVAUuzGAYyjOgi3Sx2V3fd9Mz2uH_cws5k5oKKZnKm3JqZNCwYgtuK4mwuHYd3jzkgjS7bk2MaZ8j4Q4pYtINxJlbzK6eZsv5XYXAQDhkPdkswqA74fjTY",
"postedOn":1511273727974,
"type":0,
"postNodeId":2370,
"postId":1511271513025,
"productsTagged":null,
"place":"19, 1st Main Road, RBI Colony, Hebbal, Bengaluru, Karnataka 560024, India",
"latitude":13.0285988,
"longitude":77.5894102,
"imageCount":2,
"mainUrl":"http://res.cloudinary.com/dxaxmyifi/image/upload/v1511271511/yllv6igmmkre2jjj9osy.jpg",
"thumbnailImageUrl":"http://res.cloudinary.com/dxaxmyifi/image/upload/q_60,w_150,h_150,c_thumb/v1511271511/yllv6igmmkre2jjj9osy.jpg",
"postCaption":null,
"hashtags":"null",
"tagProduct":null,
"tagProductCoordinates":null,
"containerHeight":"415",
"containerWidth":"355",
"thumbnailUrl1":null,
"imageUrl1":"http://res.cloudinary.com/dxaxmyifi/image/upload/q_60,w_150,h_150,c_thumb/v1511271511/leincswoivnrzvjcrs7k.jpg",
"containerHeight1":null,
"containerWidth1":null,
"imageUrl2":"http://res.cloudinary.com/dxaxmyifi/image/upload/v1511271511/ox79g2vsaecxqvmodguf.jpg",
"thumbnailUrl2":null,
"containerHeight2":null,
"containerWidth2":null,
"imageUrl3":null,
"thumbnailUrl3":null,
"containerHeight3":null,
"containerWidth3":null,
"imageUrl4":null,
"thumbnailUrl4":null,
"containerHeight4":null,
"containerWidth4":null,
"productUrl":null,
"productName":"Tiffin",
"description":"tiffinhhh",
"rating":4,
"price":300,
"currency":"INR",
"sold":1,
"condition":"Used(normal wear)",
"buyername":"andlive01",
"categoryData":[],
"buyerPushToken":"fDKybGc_HL4:APA91bHZWx7zXiRrWCbgqthIJ41uUAp1BheK-RIlSzIVrECGBFGNEPk49MUwqYWJd3X4Ty2Bod-binJIZuoszDyVWQeeAVTQYy6fT5bV2U-1h2os5niP9KMnjql0Y-gp7nh8aV4hijPX",
"buyerProfilePicUrl":""*/

    private String username="",profilePicUrl="",fullName="",pushToken="",postedOn="",type="",postNodeId="",postId="",productsTagged="",place="",
            latitude="",longitude="",imageCount="",mainUrl="",thumbnailImageUrl="",postCaption="",hashtags="",tagProduct="",tagProductCoordinates="",
            containerHeight="",containerWidth="",thumbnailUrl1="",imageUrl1="",containerHeight1="",containerWidth1="",imageUrl2="",thumbnailUrl2="",containerHeight2="",
            containerWidth2="",imageUrl3="",thumbnailUrl3="",containerHeight3="",containerWidth3="",imageUrl4="",thumbnailUrl4="",containerHeight4="",
            containerWidth4="",productUrl="",productName="",description="",rating="",price="",currency="",sold="",condition="",buyername="",buyerPushToken="",buyerProfilePicUrl="";

    private ArrayList<ProductCategoryDatas> categoryData;

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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostNodeId() {
        return postNodeId;
    }

    public void setPostNodeId(String postNodeId) {
        this.postNodeId = postNodeId;
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

    public String getImageCount() {
        return imageCount;
    }

    public void setImageCount(String imageCount) {
        this.imageCount = imageCount;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public String getPostCaption() {
        return postCaption;
    }

    public void setPostCaption(String postCaption) {
        this.postCaption = postCaption;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public String getTagProduct() {
        return tagProduct;
    }

    public void setTagProduct(String tagProduct) {
        this.tagProduct = tagProduct;
    }

    public String getTagProductCoordinates() {
        return tagProductCoordinates;
    }

    public void setTagProductCoordinates(String tagProductCoordinates) {
        this.tagProductCoordinates = tagProductCoordinates;
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

    public String getImageUrl3() {
        return imageUrl3;
    }

    public void setImageUrl3(String imageUrl3) {
        this.imageUrl3 = imageUrl3;
    }

    public String getThumbnailUrl3() {
        return thumbnailUrl3;
    }

    public void setThumbnailUrl3(String thumbnailUrl3) {
        this.thumbnailUrl3 = thumbnailUrl3;
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

    public String getImageUrl4() {
        return imageUrl4;
    }

    public void setImageUrl4(String imageUrl4) {
        this.imageUrl4 = imageUrl4;
    }

    public String getThumbnailUrl4() {
        return thumbnailUrl4;
    }

    public void setThumbnailUrl4(String thumbnailUrl4) {
        this.thumbnailUrl4 = thumbnailUrl4;
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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
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

    public String getSold() {
        return sold;
    }

    public void setSold(String sold) {
        this.sold = sold;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getBuyername() {
        return buyername;
    }

    public void setBuyername(String buyername) {
        this.buyername = buyername;
    }

    public String getBuyerPushToken() {
        return buyerPushToken;
    }

    public void setBuyerPushToken(String buyerPushToken) {
        this.buyerPushToken = buyerPushToken;
    }

    public String getBuyerProfilePicUrl() {
        return buyerProfilePicUrl;
    }

    public void setBuyerProfilePicUrl(String buyerProfilePicUrl) {
        this.buyerProfilePicUrl = buyerProfilePicUrl;
    }

    public ArrayList<ProductCategoryDatas> getCategoryData() {
        return categoryData;
    }

    public void setCategoryData(ArrayList<ProductCategoryDatas> categoryData) {
        this.categoryData = categoryData;
    }
}
