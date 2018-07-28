package com.demskigroup.guitaramps.Uploader;

import android.content.Context;
import android.os.AsyncTask;
import com.google.gson.Gson;
import com.demskigroup.guitaramps.utility.ApiUrl;
import com.demskigroup.guitaramps.utility.OkHttp3Connection;
import com.demskigroup.guitaramps.utility.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
/**
0 * @since  8/19/2017.
 */
public class FileUploader
{
    private static FileUploader fileUploader;
    private List<ProductImageDatas> success_list;
    private List<ProductImageDatas> failed_list;
    private List<String> list;
    private UploaderCallback uploaderCallback;
    private UploadedCallback callback;
    private SessionManager sessionManager;
    private FileUploader(Context context)
    {
        sessionManager=new SessionManager(context);
        list=new ArrayList<>();
        success_list=new ArrayList<>();
        failed_list=new ArrayList<>();
        initUploaderCallback();
    }

    public static FileUploader getFileUploader(Context context)
    {
        if(fileUploader==null)
        {
            fileUploader=new FileUploader(context);
        }
        return fileUploader;
    }


    public void UploadMultiple(List<String> fileList,UploadedCallback tempcallback)
    {
        callback=tempcallback;
        list.clear();
        success_list.clear();
        failed_list.clear();
        list.addAll(fileList);
        if(list.size()>0)
        {
            collectClodudDetails();
        }else
        {
            if(callback!=null)
                callback.onError("No file to upload!");
        }
    }


    /*
     *getting all the cloudinary details.*/
    private void collectClodudDetails()
    {
        JSONObject request_data=new JSONObject();
        try {
            request_data.put("deviceId",sessionManager.getDeviceId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttp3Connection.doOkHttp3Connection("", ApiUrl.GET_CLOUDINARY_DETAILS, OkHttp3Connection.Request_type.POST, request_data, new OkHttp3Connection.OkHttp3RequestCallback() {
            @Override
            public void onSuccess(String result, String user_tag)
            {
                try
                {
                    Cloudinary_Details_reponse response=new Gson().fromJson(result,Cloudinary_Details_reponse.class);
                    if(response.getCode().equals("200"))
                    {
                        createMultiUploader(response.getResponse().getCloudName(),response.getResponse().getApiKey(),response.getResponse().getSignature(),response.getResponse().getTimestamp());
                    }else
                    {
                        if(callback!=null)
                            callback.onError(response.getMessage());
                    }

                }catch (Exception e)
                {
                    e.printStackTrace();
                    if(callback!=null)
                        callback.onError("Error message!");
                }

            }
            @Override
            public void onError(String error, String user_tag)
            {
                if(callback!=null)
                    callback.onError(error);
            }
        });
    }



    private void createMultiUploader(String name, String key, String signeture, String timestamp)
    {
        for(int index=0;index<list.size();index++)
        {
            UploadDetail uploadDetail=createCloudData(name,key,signeture,timestamp);
            uploadDetail.file_path=list.get(index);
            new Uploader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,uploadDetail);
        }
    }


    private void initUploaderCallback()
    {
        uploaderCallback=new UploaderCallback()
        {
            @Override
            public void onSuccess(String resourceId, String mainUrl,int width,int height,String publicId)
            {
                ProductImageDatas temp_data=new ProductImageDatas();
                temp_data.setMainUrl(mainUrl);
                temp_data.setThumbnailUrl(create_Image_thumbnail(mainUrl));
                temp_data.setWidth(width);
                temp_data.setHeight(height);
                temp_data.setPublic_id(publicId);
                temp_data.setMessage("Sucess");
                success_list.add(temp_data);
                list.remove(resourceId);
                if(list.size()<1)
                {
                    if(callback!=null)
                        callback.onSuccess(success_list,failed_list);
                }
            }

            @Override
            public void onError(String resourceId, String msg)
            {
                ProductImageDatas temp_data=new ProductImageDatas();
                temp_data.setMainUrl(resourceId);
                temp_data.setMainUrl(null);
                temp_data.setMessage("Failed");
                failed_list.add(temp_data);
                list.remove(resourceId);
                if(list.size()<1)
                {
                    if(callback!=null)
                        callback.onSuccess(success_list,failed_list);
                }
            }
        };

    }


    /*
     * Parsing the success response*/
    private UploadDetail createCloudData(String name, String key, String signeture, String timestamp)
    {
        UploadDetail uploadDetail=new UploadDetail();
        uploadDetail.apiKey=key;
        uploadDetail.cloudName=name;
        uploadDetail.timestamp=timestamp;
        uploadDetail.signature=signeture;
        uploadDetail.timestamp=timestamp;
        uploadDetail.callback= uploaderCallback;
        return uploadDetail;
    }

    private String create_Image_thumbnail(String image_url)
    {
        String key_word="upload";
        int length_key=key_word.length();
        int index=image_url.indexOf("upload");
        if(index>0)
        {
            String firs_sub_String=image_url.substring(0,index+length_key);
            String last_sub_String=image_url.substring(index+length_key);
            return firs_sub_String+ConfigFile.IMAGE_THUMBNAIL_SIZE+last_sub_String;

        }else
        {
            return image_url;
        }
    }

}
