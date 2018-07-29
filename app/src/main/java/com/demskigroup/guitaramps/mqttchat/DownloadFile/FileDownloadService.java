package com.demskigroup.guitaramps.mqttchat.DownloadFile;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/*
 * Created by moda on 01/07/16.
 */


/**
 * Service to download the image/audio or video received in the chat
 */
public interface FileDownloadService
{
    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlAsync(@Url String fileUrl);
}
