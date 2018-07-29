package com.demskigroup.guitaramps.mqttchat.ModelClasses;

/*
 * Created by moda on 15/04/16.
 */


/*
*
*
*
* POJO  class for items in chat media history.Message can be either of the image or the video
* */


public class Media_History_Item {


    private String id, ts, imagepath, videoPath, messageType, thumbnailpath;


    private boolean isSelf;
    private int downloadStatus;


    public String getMessageId() {
        return id;
    }

    public  void setMessageId(String id) {
        this.id = id;
    }


    public String getTS() {
        return ts;
    }

    public  void setTS(String ts) {
        this.ts = ts;
    }


    public  boolean isSelf() {
        return isSelf;
    }

    public  void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }


    public void setVideoPath(String VideoPath) {
        this.videoPath = VideoPath;
    }

    public String getVideoPath() {
        return videoPath;
    }


    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String MessageType) {
        this.messageType = MessageType;
    }


    public void setImagePath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getImagePath() {
        return imagepath;
    }


    public void setThumbnailPath(String Thumbnailpath) {
        this.thumbnailpath = Thumbnailpath;
    }

    public String getThumbnailPath() {
        return thumbnailpath;
    }


    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }


}
