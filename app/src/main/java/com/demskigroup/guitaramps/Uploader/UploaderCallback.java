package com.demskigroup.guitaramps.Uploader;

/**
 * @since  8/19/2017.
 */
interface UploaderCallback
{
    void onSuccess(String resourseId,String mainUrl,int width,int height,String publicId);
    void onError(String resourseId,String msg);
}
