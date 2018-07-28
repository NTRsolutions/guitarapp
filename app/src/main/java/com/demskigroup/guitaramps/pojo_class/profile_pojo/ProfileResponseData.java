package com.demskigroup.guitaramps.pojo_class.profile_pojo;

/**
 * Created by hello on 4/10/2017.
 */
public class ProfileResponseData
{
    /*"followers":3,
"following":5,
"totalPosts":20,
"fullName":"Jay",
"profilePicUrl":"https://res.cloudinary.com/yelo/image/upload/v1491806167/zdmagazzoinsalkstfxa.png",
"username":"jayz",
"bio":null,
"websiteUrl":null,
"gender":"Male",
"private":null,
"phoneNumber":"In +919023585008",
"email":"jayz@gmail.com",
"businessProfile":1,
"businessName":null,
"aboutBusiness":null*/

    private String followers="",following="",totalPosts="",fullName="",profilePicUrl="",username="",bio="",websiteUrl="",gender="",phoneNumber="",email="",
            businessProfile="",businessName="",aboutBusiness="";

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(String totalPosts) {
        this.totalPosts = totalPosts;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getBusinessProfile() {
        return businessProfile;
    }

    public void setBusinessProfile(String businessProfile) {
        this.businessProfile = businessProfile;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getAboutBusiness() {
        return aboutBusiness;
    }

    public void setAboutBusiness(String aboutBusiness) {
        this.aboutBusiness = aboutBusiness;
    }
}
