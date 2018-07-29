package com.demskigroup.guitaramps.utility;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.demskigroup.guitaramps.pojo_class.CloudData;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * @author 3Embed.
 * @since 5/16/16
 * <h1>Upload to cloudinary</h1>
 * <p>this class are used to upload file to cloudinary server</p>
 * <p>This class containing abstract method @callback which will give result to called class</p>
 */
public abstract class UploadToCloudinary
{
    private Bundle sessionBundle;
    private UploadToCloudinary(Bundle sessionBundle)
    {
        this.sessionBundle = sessionBundle;
    }

    protected UploadToCloudinary(Bundle sessionBundle,CloudData cloudData)
    {
        this(sessionBundle);
        new UploadFileToCloudinary().execute(cloudData);
    }
    /**
     * <H1>Upload file to cloudinary server</H1>
     * <p>This class will run in helper thread</p>
     */
    private class UploadFileToCloudinary  extends AsyncTask<CloudData, Void, Map>
    {
        boolean isError=false;
        @Override
        protected Map doInBackground(CloudData... params)
        {
            Map uploaded_details=null;
            Map config = new HashMap();
            config.put("cloud_name",sessionBundle.getString("cloudName"));
            Cloudinary cloudinary = new Cloudinary(config);
            CloudData mCloudData = params[0];

            try
            {
                Map data;
                if (mCloudData.isVideo())
                {
                    Log.d("Yes_video","Yes data");
                    data = ObjectUtils.asMap("signature", sessionBundle.getString("signature"),"timestamp", sessionBundle.getString("timestamp"),"api_key", sessionBundle.getString("apiKey"),"resource_type","video");
                    uploaded_details=cloudinary.uploader().upload(new File(mCloudData.getPath()), data);
                }
                else
                {
                    Log.d("Yes_IMage","Yes data");
                    data = ObjectUtils.asMap("signature", sessionBundle.getString("signature"), "timestamp",sessionBundle.getString("timestamp"),"api_key", sessionBundle.getString("apiKey"));
                    uploaded_details= cloudinary.uploader().upload(new File(mCloudData.getPath()), data);
                }
                isError=false;
            } catch (IOException e)
            {
                isError=true;
                e.printStackTrace();
            }
            return uploaded_details;
        }

        @Override
        protected void onPostExecute(Map details)
        {
            super.onPostExecute(details);
            if(!isError)
            {
                if (details!= null)
                {
                    callBack(details);
                }
            }else
            {
                errorCallBack("error while upLoading to cloudinary");
            }
        }
    }
    /**
     * <h1>callback</h1>
     * <p>This metohd need to implement on called class this metohd containg result data</p>
     * @param resultData Map resultData
     */
    public abstract void callBack(Map resultData);
    /**
     * If any error happen the this service called.*/
    public abstract void errorCallBack(String error);

}
