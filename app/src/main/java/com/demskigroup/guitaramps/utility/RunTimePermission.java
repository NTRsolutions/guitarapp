package com.demskigroup.guitaramps.utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.demskigroup.guitaramps.R;

/**
 * <h>RunTimePermission</h>
 * <p>
 *     In this class we wrote the code for Android Runtime permissions like
 *     firstlly to check whether Given permission is granted or not. it is
 *     not then grant the permission.
 * </p>
 */
public class RunTimePermission
{
    private Activity mActivity;
    private String[] permissionList;
    private boolean isToFinish;

    /**
     * <h>RunTimePermission</h>
     * <p>
     *     Simple contructor to initialize values like current like context, permission list etc
     * </p>
     * @param mActivity The current class context
     * @param permissionList The permission list like locations, camera etc.
     * @param isToFinish The boolean flag whether the activity is to finish or not if all permissions are not granted
     */
    public RunTimePermission(Activity mActivity,String[] permissionList,boolean isToFinish)
    {
        this.mActivity = mActivity;
        this.permissionList=permissionList;
        this.isToFinish=isToFinish;
    }

    /**
     * <h>checkPermissions</h>
     * <p>
     *     In this method we used to check whether given permissions are granted or not.
     * </p>
     * @param permissions The permissions array
     * @return The boolean flag
     */
    public boolean checkPermissions(String[] permissions)
    {
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(mActivity, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * <h>RequestPermission</h>
     * <p>
     *     In this method we used to set runtime permissions.
     * </p>
     */
    public void requestPermission()
    {
        ActivityCompat.requestPermissions(mActivity, permissionList, VariableConstants.PERMISSION_REQUEST_CODE);
    }

    /**
     * <h>ShowErrorMessage</h>
     * <p>
     *     In this method we used to show dialog for error message if
     *     the user denies any permissions.
     * </p>
     * @param permissionName the permission name
     */
    public void allowPermissionAlert(final String permissionName)
    {
        final Dialog errorMessageDialog = new Dialog(mActivity);
        errorMessageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        errorMessageDialog.setContentView(R.layout.dialog_permission_denied);
        errorMessageDialog.getWindow().setGravity(Gravity.BOTTOM);
        errorMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        errorMessageDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        errorMessageDialog.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;

        TextView tV_permission_name = (TextView) errorMessageDialog.findViewById(R.id.tV_permission_name);
        String deniedPermission;
        if (permissionName.contains("CAMERA"))
        {
            deniedPermission = mActivity.getResources().getString(R.string.camera);
        }
        else if (permissionName.contains("WRITE_EXTERNAL_STORAGE"))
        {
            deniedPermission = mActivity.getResources().getString(R.string.external_storage);
        }
        else if (permissionName.contains("ACCESS_COARSE_LOCATION"))
        {
            deniedPermission = mActivity.getResources().getString(R.string.coarse_location);
        }
        else if (permissionName.contains("ACCESS_FINE_LOCATION"))
        {
            deniedPermission = mActivity.getResources().getString(R.string.fine_location);
        }
        else if (permissionName.contains("READ_CONTACTS"))
        {
            deniedPermission = mActivity.getResources().getString(R.string.read_contact);
        } else if (permissionName.contains("RECORD_AUDIO"))
        {
            deniedPermission = mActivity.getResources().getString(R.string.record_audio);
        }
        else deniedPermission = permissionName;
        deniedPermission=  mActivity.getResources().getString(R.string.please_allow_the_permission) + " "+deniedPermission+" "+mActivity.getResources().getString(R.string.by_clicking_on_allow_button);
        tV_permission_name.setText(deniedPermission);

        // Cancel
        TextView tV_cancel= (TextView) errorMessageDialog.findViewById(R.id.tV_cancel);
        tV_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorMessageDialog.dismiss();
                if (isToFinish)
                    mActivity.finish();
            }
        });

        // allow
        RelativeLayout rL_allow= (RelativeLayout) errorMessageDialog.findViewById(R.id.rL_allow);
        rL_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                {
                    if (mActivity.shouldShowRequestPermissionRationale(permissionName))
                        ActivityCompat.requestPermissions(mActivity,new String[]{permissionName}, VariableConstants.PERMISSION_REQUEST_CODE);
                }
                errorMessageDialog.dismiss();
            }
        });
        errorMessageDialog.show();
    }
}
