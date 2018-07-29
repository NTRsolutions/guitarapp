package com.demskigroup.guitaramps.device_camera;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import com.demskigroup.guitaramps.utility.VariableConstants;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import co.simplecrop.android.simplecropimage.CropImage;

/**
 * <h>HandleCameraEvent</h>
 * <p>
 *     In this class we used to do all operation of cameras like capture
 *     image, select image from gallery. we have one method chooseImage()
 *     from that we open a dialog popup to select image from camera or
 *     gallery.
 * </p>
 * @since 18-Aug-17
 * @author 3Embed
 * @version 1.0
 */
public class HandleCameraEvents
{
    private Activity mActivity;
    private static final String TAG= HandleCameraEvents.class.getSimpleName();
    private File mFile;

    public HandleCameraEvents(Activity mActivity, File mFile) {
        this.mActivity = mActivity;
        this.mFile=mFile;
    }

    /**
     * <h>TakePicFromCamera</h>
     * <p>
     *     This method is called from selectImage method.
     *     This method is helps us to capture image.afetr
     *     capturing image we save that image as a file
     *     in newFile variable.
     * </p>
     */
    public void takePicture()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            Uri mImageCaptureUri;
            String state = Environment.getExternalStorageState();

            if (Environment.MEDIA_MOUNTED.equals(state))
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    mImageCaptureUri = FileProvider.getUriForFile(mActivity, mActivity.getPackageName() + ".provider",mFile);
                }else {
                    mImageCaptureUri = Uri.fromFile(mFile);
                }
            }
            else {
	        	/*
	        	 * The solution is taken from here: http://stackoverflow.com/questions/10042695/how-to-get-camera-result-as-a-uri-in-data-folder
	        	 */
                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            mActivity.startActivityForResult(intent, VariableConstants.CAMERA_CAPTURE);
        } catch (ActivityNotFoundException e) {

            Log.d(TAG, "cannot take picture", e);
        }
    }

    public void openGallery() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        mActivity.startActivityForResult(photoPickerIntent, VariableConstants.SELECT_GALLERY_IMG_REQ_CODE);
    }

    public void startCropImage()
    {
        Intent intent = new Intent(mActivity, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFile.getPath());
        intent.putExtra(CropImage.SCALE, true);
        intent.putExtra(CropImage.ASPECT_X, 3);
        intent.putExtra(CropImage.ASPECT_Y, 2);
        mActivity.startActivityForResult(intent, VariableConstants.PIC_CROP);
    }

    public void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
}
