package com.yelo.com.pojo_class.search_post_pojo;

import com.yelo.com.pojo_class.home_explore_pojo.ExploreLikedByUsersDatas;

import java.util.ArrayList;

/**
 * Created by hello on 29-Jun-17.
 */

public class SearchPostDatas
{
    /*"username":"jony",
            "fullName":"Jon",
            "profilePicUrl":"https://graph.facebook.com/123975791528857/picture?type=large&return_ssl_resources=1",
            "postedOn":1498643097208,
            "postsType":0,
            "postId":1498643097663,
            "productsTagged":null,
            "place":"10th Cross Road, RT Nagar, Bengaluru, Karnataka 560024, India",
            "latitude":13.028614,
            "longitude":77.589491,
            "city":"bengaluru",
            "countrySname":"in",
            "mainUrl":"https://res.cloudinary.com/dxaxmyifi/image/upload/fl_progressive:steep/v1498643096/jgb4nhtwz3luyfnlac87.jpg",
            "thumbnailImageUrl":"https://res.cloudinary.com/dxaxmyifi/image/upload/w_150,h_150/v1498643096/jgb4nhtwz3luyfnlac87.jpg",
            "postCaption":null,
            "hashtags":"null",
            "imageCount":1,
            "containerHeight":1000,
            "containerWidth":1000,
            "productsTaggedCoordinates":null,
            "hasAudio":0,
            "category":"fashion and accessories",
            "categoryMainUrl":"http://138.197.65.222/public/appAssets/1496833004327.png",
            "cateoryActiveImageUrl":"http://138.197.65.222/public/appAssets/1496833004331.png",
            "price":100,
            "currency":"INR",
            "productName":"band",
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
            "commenData":[],
            "likes":1,
            "likedByUsers":[]*/

    private String username="",fullName="",profilePicUrl="",postedOn="",postsType="",postId="",productsTagged="",place="",latitude="",longitude="",city="",
            countrySname="",mainUrl="",thumbnailImageUrl="",postCaption="",hashtags="",imageCount="",containerHeight="",containerWidth="",productsTaggedCoordinates="",
            hasAudio="",category="",price="",currency="",productName="",thumbnailUrl1="",imageUrl1="",containerHeight1="",containerWidth1="",imageUrl2="",thumbnailUrl2="",
            containerHeight2="",containerWidth2="",thumbnailUrl3="",imageUrl3="",containerHeight3="",containerWidth3="",thumbnailUrl4="",imageUrl4="",containerHeight4="",
            containerWidth4="",likes="";
    private ArrayList<ExploreLikedByUsersDatas> likedByUsers;

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

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    public String getPostsType() {
        return postsType;
    }

    public void setPostsType(String postsType) {
        this.postsType = postsType;
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

    public String getImageCount() {
        return imageCount;
    }

    public void setImageCount(String imageCount) {
        this.imageCount = imageCount;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public ArrayList<ExploreLikedByUsersDatas> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(ArrayList<ExploreLikedByUsersDatas> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }
}
