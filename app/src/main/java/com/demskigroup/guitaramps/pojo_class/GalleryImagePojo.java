package com.demskigroup.guitaramps.pojo_class;

/**
 * Created by hello on 20-Jun-17.
 */

public class GalleryImagePojo
{
    private String galleryImagePath="";
    private boolean isSelected=false;
    private int rotationAngle=0;

    public String getGalleryImagePath() {
        return galleryImagePath;
    }

    public void setGalleryImagePath(String galleryImagePath) {
        this.galleryImagePath = galleryImagePath;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(int rotationAngle) {
        this.rotationAngle = rotationAngle;
    }
}
