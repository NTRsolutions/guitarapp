package com.demskigroup.guitaramps.pojo_class.fcm_notification_pojo;

/**
 * Created by hello on 24-Aug-17.
 */

public class FcmNotificationBody
{

    /*"campaignId":1503568131483,
            "imageUrl":"",
            "title":"Push Title",
            "message":"Push message",
            "type":73,
            "userId":1519,
            "url":"",
            "username":"ravi"*/

    private String campaignId="",imageUrl="",title="",message="",type="",userId="",url="",username="";

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
