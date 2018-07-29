package com.demskigroup.guitaramps.pojo_class.home_explore_pojo;

import java.io.Serializable;

/**
 * Created by hello on 4/6/2017.
 */
public class ExploreLikedByUsersDatas implements Serializable
{
    /*"profilePicUrl":null,
            "likedByUsers":null*/

    private String profilePicUrl="",likedByUsers="",name="";

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(String likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
