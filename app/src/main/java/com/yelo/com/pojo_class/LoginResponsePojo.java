package com.yelo.com.pojo_class;

/**
 * Created by hello on 4/4/2017.
 */

public class LoginResponsePojo
{
            /*"code":200,
            "message":"Success!!",
            "token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoicmF2aSIsImVtYWlsIjoiaGlAZ21haWwuY29tIiwiaWF0IjoxNDkxMzE2Njc0LCJleHAiOjE0OTY1MDA2NzR9.t1J0lUPV9dq-qHIkdh3q8_Et_qFb8L1gFc5_0JPI5-4",
            "userId":201,
            "pushToken":"1234",
            "createdOn":1490955709710,
            "username":"ravi",
            "followers":0,
            "following":0,
            "posts":0,
            "deviceId":null,
            "isPrivate":null,
            "businessProfile":1,
            "profilePicUrl":"http://52.89.162.162//apps/Redbags/CustomerImages/i8vapldoo0j1489223562461.jpg",
            "website":null,
            "phoneNumber":"12345",
            "email":"hi@gmail.com",
            "aboutBusiness":null,
            "businessName":null,
            "place":null,
            "latitude":null,
            "longitude":null*/

    private String code="",message="",token="",userId="",pushToken="",createdOn="",username="",followers="",posts="",deviceId="",isPrivate="",businessProfile="",
            profilePicUrl="",website="",phoneNumber="",email="",aboutBusiness="",businessName="",place="",latitude="",longitude="";

    public String getMqttId() {
        return mqttId;
    }

    public void setMqttId(String mqttId) {
        this.mqttId = mqttId;
    }

    private String mqttId;
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(String isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getBusinessProfile() {
        return businessProfile;
    }

    public void setBusinessProfile(String businessProfile) {
        this.businessProfile = businessProfile;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAboutBusiness() {
        return aboutBusiness;
    }

    public void setAboutBusiness(String aboutBusiness) {
        this.aboutBusiness = aboutBusiness;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
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
}
