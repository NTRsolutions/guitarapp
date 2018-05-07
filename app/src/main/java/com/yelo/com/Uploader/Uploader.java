package com.yelo.com.Uploader;

import android.os.AsyncTask;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
/**
 * <h2>Uploader</h2>
 * Uploading a file to cloudinary in android.
 * @since 8/19/2017.
 */
class Uploader extends AsyncTask<UploadDetail,Void,Map>
{
   private boolean isError=false;
   private UploaderCallback callback;
   private String resource_id;

    @Override
    protected Map doInBackground(UploadDetail... params)
    {
        Map uploaded_details=null;
        Map config = new HashMap();
        UploadDetail uploadDetail=params[0];
        callback=uploadDetail.callback;
        resource_id=uploadDetail.file_path;
        config.put("cloud_name",uploadDetail.cloudName);
        Cloudinary cloudinary = new Cloudinary(config);
        try
        {
            Map data = ObjectUtils.asMap("signature",uploadDetail.signature,"timestamp",uploadDetail.timestamp,"api_key",uploadDetail.apiKey);
            uploaded_details= cloudinary.uploader().uploadLarge(new File(uploadDetail.file_path), data);
            isError=false;
        } catch (Exception e)
        {
            isError=true;
            e.printStackTrace();
        }
        return uploaded_details;
    }
    @Override
    protected void onPostExecute(Map data)
    {
        super.onPostExecute(data);
        if(!isError&&data!=null)
        {
            //String main_url=(String)data.get("url");
            String main_url;
            if(data.containsKey("secure_url"))
            {
                main_url=(String)data.get("secure_url");
            }else {
                main_url=(String)data.get("url");
            }
            int width= (int) data.get("width");
            int height=(int)data.get("height");
            String publicId=(String)data.get("public_id");
            callback.onSuccess(resource_id,main_url,width,height,publicId);
        }else
        {
            if(callback!=null)
                callback.onError(resource_id,"Upload error");

        }
    }
}
