package com.demskigroup.guitaramps.mqttchat.DownloadFile;

import com.demskigroup.guitaramps.mqttchat.Utilities.ApiOnServer;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
/*
* Service containing the list of the multipart port and link used to
* upload image/audio or video shared on a chat
*
* */
public class ServiceGenerator
{
    private static final String ChatMulterUpload = ApiOnServer.CHAT_MULTER_UPLOAD_URL;
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder().readTimeout(300, TimeUnit.SECONDS).writeTimeout(300, TimeUnit.SECONDS)
            .connectTimeout(300, TimeUnit.SECONDS);
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(ChatMulterUpload)
                    .addConverterFactory(GsonConverterFactory.create());
    public static <S> S createService(Class<S> serviceClass)
    {
        Retrofit retrofit = builder.client(httpClient.build())
                .build();
        return retrofit.create(serviceClass);
    }

}