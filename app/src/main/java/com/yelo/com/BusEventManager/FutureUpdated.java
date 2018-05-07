package com.yelo.com.BusEventManager;

/**
 * @since /9/2017.
 * @version 1.0.
 */
public class FutureUpdated
{
    private String postId;

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isUpgraded() {
        return isUpgraded;
    }

    public void setUpgraded(boolean upgraded) {
        isUpgraded = upgraded;
    }

    private boolean isUpgraded;

    public FutureUpdated(String postId,boolean isUpgraded)
    {
        this.postId=postId;
        this.isUpgraded=isUpgraded;
    }

}
