package com.demskigroup.guitaramps.Face_book_manger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.internal.ShareConstants;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.ShareMedia;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.demskigroup.guitaramps.mqttchat.AppController;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * <h2>Facebook_share_mamager</h2>
 * <P>
 *
 * </P>
 * @since 6/3/17.
 */
public class Facebook_share_mamager
{
    private Share_callback share_callback;
    private static Facebook_share_mamager FACEOOK_MANAGER=null;

    private Facebook_share_mamager() {
    }

    public static Facebook_share_mamager getInstance()
    {
        if(FACEOOK_MANAGER==null)
        {
            FACEOOK_MANAGER=new Facebook_share_mamager();
        }
        return FACEOOK_MANAGER;
    }

    public void fb_share_image(String image_local_path,String caption,Share_callback callback)
    {
        Image_data_holder data=new Image_data_holder();
        data.image_local_path=image_local_path;
        data.caption=caption;
        data.callback=callback;
        new Image_upload().execute(data);
    }


    /**
     * Creating the async task to upload the image in facebook.*/
    private class Image_upload extends AsyncTask<Image_data_holder,Void,Void>
    {
        @Override
        protected Void doInBackground(Image_data_holder... params)
        {
            Image_data_holder item_data=params[0];
            share_Image(item_data.image_local_path,item_data.caption,item_data.callback);
            return null;
        }
    }

    private void share_Image(String image_local_path,String caption,Share_callback callback)
    {
        this.share_callback=callback;
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(BitmapFactory.decodeFile(image_local_path))
                .setCaption(caption)
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
         /*
          * Finally sharing the data.*/
        share_Data(content);
    }


    public void fb_share_video(String local_video_path,String thumbnail_url,String caption,Share_callback callback)
    {
        Video_data_holder data=new Video_data_holder();
        data.local_video_path=local_video_path;
        data.thumbnail_url=thumbnail_url;
        data.caption=caption;
        data.callback=callback;
        new Video_upload().execute(data);
    }


    /**
     * Creating the async task to upload the image in facebook.*/
    private class Video_upload extends AsyncTask<Video_data_holder,Void,Void>
    {
        @Override
        protected Void doInBackground(Video_data_holder... params)
        {
            Video_data_holder item_data=params[0];
            share_Video(item_data.local_video_path, item_data.caption,item_data.callback);
            return null;
        }
    }


    private void share_Video(String local_video_path,String caption, Share_callback callback)
    {
        this.share_callback=callback;
        ShareVideo video= new ShareVideo.Builder()
                .setLocalUrl(getUri_Path(new File(local_video_path)))
                .build();
        ShareVideoContent content = new ShareVideoContent.Builder()
                .setVideo(video)
                .setContentTitle(caption)
                .build();
        /*
         * Finally sharing the data.*/
        share_Data(content);
    }

    public void shareImage_Link(String link,String image_url,String caption,Share_callback callback)
    {
        this.share_callback=callback;
        Data_holder data_holder=new Data_holder();
        data_holder.caption=caption;
        data_holder.image_path=image_url;
        data_holder.link=link;
        new DownloadImgTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,data_holder);
    }


    private class DownloadImgTask extends AsyncTask<Data_holder, Void, Bitmap>
    {
        Data_holder holder=null;
        protected Bitmap doInBackground(Data_holder... data)
        {
            holder=data[0];
            String urldisplay = holder.image_path;
            Bitmap bm = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bm = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bm;
        }

        protected void onPostExecute(Bitmap result)
        {
            post_In_Fb(result,holder.caption,holder.link);
        }
    }
    /*
     *Sharing the bitmap */
    private void post_In_Fb(Bitmap result,String caption,String link)
    {
        Log.d("dgta2",""+link);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(result)
                .setCaption(caption)
                .setImageUrl(Uri.parse(link))
                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .setContentUrl(Uri.parse(link))
                .build();
        /*
         * Finally -sharing the data.*/
        share_Data(content);
    }

    private void postData(String link,String imageUrl,String title,String decryption)
    {
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "enguru_app:badge")
                .putString("og:title", title)
                .putString("og:url",link)
                .putString("og:image",imageUrl)
                .putString("og:description",decryption).build();
        // Create an action
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("enguru_app:unlocked")
                .putObject("badge", object).build();
        // Create the content
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("badge").setAction(action)
                .build();
    }

    /*
     *Downloading the video and sharing the video to facebook */
    public void shareVideo_link(String video_url,String thumbnail_url,String caption,Share_callback callback)
    {
        this.share_callback=callback;
        Data_holder data_holder=new Data_holder();
        data_holder.caption=caption;
        data_holder.image_path=video_url;
        new DownloadVidTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,data_holder);
    }

    /*
     *Downloading the video file */
    private class DownloadVidTask extends AsyncTask<Data_holder, Void,String>
    {
        Data_holder holder=null;
        private boolean isError=false;
        private String caption="";
        protected String doInBackground(Data_holder... param)
        {
            isError=false;
            InputStream inputStream = null;
            OutputStream outputStream = null;
            File file = null;
            holder=param[0];
            String url= holder.image_path;
            caption=holder.caption;
            String details="";
            try
            {
                file=getFile("1");
                URL url_path=new URL(url);
                URLConnection connection=url_path.openConnection();
                connection.connect();
                connection.getContentType();
                inputStream=new BufferedInputStream(url_path.openStream());
                outputStream=new FileOutputStream(file);
                byte data[]=new byte[1024];
                int count;
                while((count=inputStream.read(data))!=-1)
                {
                    outputStream.write(data,0,count);
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                isError=true;
                details="Error on loading file.";
            }finally
            {
                try {
                    assert outputStream != null;
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
                    isError=true;
                    details="Error on loading file.";
                }
                try {
                    inputStream.close();
                } catch (Exception e)
                {
                    e.printStackTrace();
                    isError=true;
                    details="Error on loading file.";
                }
            }
            if(file.length()>12582912)
            {
                isError=true;
                details="File size is more then 12MB.";
            }
            return isError?details:file.getPath();
        }

        protected void onPostExecute(String result)
        {
          if(isError)
          {
              if(share_callback!=null)
              share_callback.onError(result);
          }else
          {
              share_Video(result,caption,share_callback);
          }
        }
    }

    /*
     *Finally posting the data to the facebook. */
    private void share_Data(ShareContent shareContent)
    {
        new ShareApi(shareContent)
                .share(new FacebookCallback<Sharer.Result>()
                {
                    @Override
                    public void onSuccess(Sharer.Result result)
                    {
                        if(share_callback!=null)
                        {
                            share_callback.onSucess_share();
                        }
                    }
                    @Override
                    public void onCancel()
                    {
                        if(share_callback!=null)
                        {
                            share_callback.onError("User canceled.");
                        }
                    }
                    @Override
                    public void onError(FacebookException error)
                    {
                        if(share_callback!=null)
                        {
                            share_callback.onError(""+error);
                        }
                    }
                });
    }



    private class Data_holder
    {
        String image_path;
        String caption;
        String link;

    }

    private class Image_data_holder
    {
        String image_local_path;
        String caption;
        Share_callback callback;
    }


    private class Video_data_holder
    {
        String local_video_path;
        String thumbnail_url;
        String caption;
        Share_callback callback;
    }

    public interface Share_callback
    {
        void onSucess_share();

        void onError(String error);
    }

    /*
     * Get the file of the required type ie. Image or video.*/
    private File getFile(String isVideo) throws IOException
    {
        File file;
        long time= System.currentTimeMillis();
        String temp_video="temp"+time;
        String suffix;
        if(isVideo.equalsIgnoreCase("1"))
        {
            suffix=".mp4";
        }else
        {
            suffix=".jpeg";
        }
        file = File.createTempFile(temp_video,suffix, AppController.getInstance().getCacheDir());
        return file;
    }

    /**
     * <h2>getUri_Path</h2>
     * <P>
     *
     * </P>*/
    public static Uri getUri_Path(File file)
    {
        Context context=AppController.getInstance();
        Uri uri;
        /*
         * Checking if the build version is greater then 25 then no need ask for runtime permission.*/
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
        {
            uri= FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName() + ".provider",file);
        }else
        {
            uri=Uri.fromFile(file);
        }
        return uri;
    }

    public void shareLinkOnFacebook(String link,String thumbnail,String name,String description,Share_callback callback)
    {
        LinkShareDataHolder linkShareDataHolder =new LinkShareDataHolder();
        linkShareDataHolder.link = link;
        linkShareDataHolder.thumbnail = thumbnail;
        linkShareDataHolder.name = name;
        linkShareDataHolder.description = description;
        linkShareDataHolder.callback = callback;
        new ShareLink().execute(linkShareDataHolder);
    }

    private class ShareLink extends AsyncTask<LinkShareDataHolder,Void,Void>
    {

        @Override
        protected Void doInBackground(LinkShareDataHolder... params) {
            LinkShareDataHolder linkShareDataHolder = params[0];
            share_Link(linkShareDataHolder.link,linkShareDataHolder.thumbnail,linkShareDataHolder.name,linkShareDataHolder.description,linkShareDataHolder.callback);
            return null;
        }
    }

    private class LinkShareDataHolder
    {
        public String link,thumbnail,name, description;
        public Share_callback callback;
    }

    private void share_Link(String link,String thumbnail,String name,String description,Share_callback callback)
    {
        this.share_callback=callback;
        ShareLinkContent content=new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(link))
                .setContentTitle(name)
                .setContentDescription(description)
                .build();
        /*
         * Finally sharing the data.*/
        share_Data(content);
    }
}
