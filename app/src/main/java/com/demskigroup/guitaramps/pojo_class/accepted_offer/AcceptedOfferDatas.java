package com.demskigroup.guitaramps.pojo_class.accepted_offer;

import java.io.Serializable;

/**
 * <h>AcceptedOfferDatas</h>
 * <p>
 *     This is main pojo class for accepted offer
 * </p>
 * @since 13-Jul-17
 */
public class AcceptedOfferDatas implements Serializable
{
    /*"postId":1499777774486,
            "mainuUrl":"https://res.cloudinary.com/dxaxmyifi/image/upload/fl_progressive:steep/v1499777772/i0ivsbmazzfp90dksy3h.jpg",
            "thumbnailImageUrl":"https://res.cloudinary.com/dxaxmyifi/image/upload/w_150,h_150/v1499777772/i0ivsbmazzfp90dksy3h.jpg",
            "price":25000,
            "currency":"INR",
            "category":"electronics",
            "categoryImageurl":"http://138.197.65.222/public/appAssets/1496832987097.png",
            "activeImageUrl":"http://138.197.65.222/public/appAssets/1496832987098.png",
            "offerPrice":25000,
            "buyername":"jayz",
            "buyerFullName":"jayz",
            "buyerProfilePicUrl":"https://res.cloudinary.com/dxaxmyifi/image/upload/v1499932565/rrwzn2a1pdbfmyslbwyq.png",
            "buyerId":1376,
            "username":"smita",
            "offerCreatedOn":1499928180225*/

    private String postId="",mainuUrl="",thumbnailImageUrl="",price="",currency="",category="",categoryImageurl="",activeImageUrl="",offerPrice="",buyername="",
            buyerFullName="",buyerProfilePicUrl="",buyerId="",username="",offerCreatedOn="";

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getMainuUrl() {
        return mainuUrl;
    }

    public void setMainuUrl(String mainuUrl) {
        this.mainuUrl = mainuUrl;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryImageurl() {
        return categoryImageurl;
    }

    public void setCategoryImageurl(String categoryImageurl) {
        this.categoryImageurl = categoryImageurl;
    }

    public String getActiveImageUrl() {
        return activeImageUrl;
    }

    public void setActiveImageUrl(String activeImageUrl) {
        this.activeImageUrl = activeImageUrl;
    }

    public String getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(String offerPrice) {
        this.offerPrice = offerPrice;
    }

    public String getBuyername() {
        return buyername;
    }

    public void setBuyername(String buyername) {
        this.buyername = buyername;
    }

    public String getBuyerFullName() {
        return buyerFullName;
    }

    public void setBuyerFullName(String buyerFullName) {
        this.buyerFullName = buyerFullName;
    }

    public String getBuyerProfilePicUrl() {
        return buyerProfilePicUrl;
    }

    public void setBuyerProfilePicUrl(String buyerProfilePicUrl) {
        this.buyerProfilePicUrl = buyerProfilePicUrl;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOfferCreatedOn() {
        return offerCreatedOn;
    }

    public void setOfferCreatedOn(String offerCreatedOn) {
        this.offerCreatedOn = offerCreatedOn;
    }
}
