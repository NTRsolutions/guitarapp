package com.demskigroup.guitaramps.Uploader;

import java.io.Serializable;

/**
 * @since 8/19/2017.
 */
public class ProductImageDatas implements Serializable
{
    private String message;
    private String mainUrl;
    private String thumbnailUrl;
    private String  public_id;
    private int width;
    private int height;
    private boolean isImageUrl;
    private int rotationAngle=0;

    public String getPublic_id() {
        return public_id;
    }

    public void setPublic_id(String public_id) {
        this.public_id = public_id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getMainUrl()
    {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl)
    {
        this.mainUrl = mainUrl;
    }

    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl = thumbnailUrl;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isImageUrl() {
        return isImageUrl;
    }

    public void setImageUrl(boolean imageUrl) {
        isImageUrl = imageUrl;
    }

    public int getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(int rotationAngle) {
        this.rotationAngle = rotationAngle;
    }
}
