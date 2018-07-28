package com.demskigroup.guitaramps.pojo_class;

import java.io.Serializable;
/**
 * <h2>CloudData</h2>
 * <P>
 *     Coudinary data holder.
 * </P>
 * C@since 6/28/16.
 */
public class CloudData implements Serializable
{
    private String path;
    private boolean isVideo;

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public boolean isVideo()
    {
        return isVideo;
    }

    public void setVideo(boolean video)
    {
        isVideo = video;
    }
}