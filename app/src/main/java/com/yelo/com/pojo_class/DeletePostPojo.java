package com.yelo.com.pojo_class;

/**
 * Created by hello on 28-Aug-17.
 */

public class DeletePostPojo
{
    /*"code":200,
            "message":"Post Deleted",
            "postId":1503390870006,
            "postCount":7*/

    private String code="",message="",postId="",postCount="";

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

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostCount() {
        return postCount;
    }

    public void setPostCount(String postCount) {
        this.postCount = postCount;
    }
}
