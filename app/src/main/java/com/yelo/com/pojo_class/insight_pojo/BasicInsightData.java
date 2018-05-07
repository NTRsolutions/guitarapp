package com.yelo.com.pojo_class.insight_pojo;

/**
 * Created by hello on 22-Aug-17.
 */

public class BasicInsightData
{
    /*"totalViews":5,
            "distinctViews":5,
            "commented":0,
            "likes":1,
            "postId":1502176128831,
            "postedOn":1503144495843*/

    private String totalViews="",distinctViews="",commented="",likes="",postId="",postedOn="";

    public String getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(String totalViews) {
        this.totalViews = totalViews;
    }

    public String getDistinctViews() {
        return distinctViews;
    }

    public void setDistinctViews(String distinctViews) {
        this.distinctViews = distinctViews;
    }

    public String getCommented() {
        return commented;
    }

    public void setCommented(String commented) {
        this.commented = commented;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }
}
