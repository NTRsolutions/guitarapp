package com.yelo.com.pojo_class.add_review_pojo;

/**
 * Created by hello on 25-Jul-17.
 */

public class AddReviewCommentData
{
    /*"commentBody":"fh",
            "commentedByUser":"shobhitkc",
            "commentedOn":1500993130230,
            "commentId":10597*/

    private String commentBody="",commentedByUser="",commentedOn="",commentId="";

    public String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(String commentBody) {
        this.commentBody = commentBody;
    }

    public String getCommentedByUser() {
        return commentedByUser;
    }

    public void setCommentedByUser(String commentedByUser) {
        this.commentedByUser = commentedByUser;
    }

    public String getCommentedOn() {
        return commentedOn;
    }

    public void setCommentedOn(String commentedOn) {
        this.commentedOn = commentedOn;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
