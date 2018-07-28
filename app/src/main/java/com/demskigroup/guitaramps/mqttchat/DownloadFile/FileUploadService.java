package com.demskigroup.guitaramps.mqttchat.DownloadFile;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
/**
 * Service to upload the image/audio or video before sending the message
 * containing the link of the file uploadeds
 */
public interface FileUploadService
{
    @Multipart
    @POST("upload")
    Call<ResponseBody> upload(@Part("description") RequestBody description,
                              @Part MultipartBody.Part file,@Header("Authorization") String authHeader);
    @Multipart
    @POST("upload/profile")
    Call<ResponseBody> uploadProfilePic(@Part("description") RequestBody description,
                                        @Part MultipartBody.Part file,@Header("Authorization") String authHeader);
}