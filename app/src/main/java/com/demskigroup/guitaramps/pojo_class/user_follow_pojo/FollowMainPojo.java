package com.demskigroup.guitaramps.pojo_class.user_follow_pojo;

import com.demskigroup.guitaramps.pojo_class.explore_following_pojo.FollowingResponseDatas;

import java.util.ArrayList;

/**
 * Created by hello on 4/21/2017.
 */

public class FollowMainPojo
{
    /*"code":200,
            "message":"success",
            "result":[]*/

    private String code="",message="";
    private ArrayList<FollowResponseDatas> result,followers,memberFollowers,following;

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

    public ArrayList<FollowResponseDatas> getResult() {
        return result;
    }

    public void setResult(ArrayList<FollowResponseDatas> result) {
        this.result = result;
    }

    public ArrayList<FollowResponseDatas> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<FollowResponseDatas> followers) {
        this.followers = followers;
    }

    public ArrayList<FollowResponseDatas> getMemberFollowers() {
        return memberFollowers;
    }

    public void setMemberFollowers(ArrayList<FollowResponseDatas> memberFollowers) {
        this.memberFollowers = memberFollowers;
    }

    public ArrayList<FollowResponseDatas> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<FollowResponseDatas> following) {
        this.following = following;
    }
}
