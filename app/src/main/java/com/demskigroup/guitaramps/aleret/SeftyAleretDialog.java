package com.demskigroup.guitaramps.aleret;

import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import com.demskigroup.guitaramps.R;

/**
 * <h2>SeftyAleretDialog</h2>
 * <P>
 *     To show tha app error aleret
 * </P>
 * @since  10/4/2017.
 * @version 1.0.
 *@author 3Embed.
 */
public class SeftyAleretDialog
{
    private   AlertDialog dialog=null;
    private View dialogView=null;
    private SeftyAleretDialog(){}
    private static SeftyAleretDialog dialogObj=new SeftyAleretDialog();
    public static SeftyAleretDialog getInstance()
    {
       return dialogObj;
    }

    public  void showSefty(final Activity activity)
    {
        if(dialog!=null&&dialog.isShowing())
        {
            dialog.dismiss();
        }
        dialogView=activity.getLayoutInflater().inflate(R.layout.seft_aleret_layout,null);
        AlertDialog.Builder  dialogBuilder=new AlertDialog.Builder(activity, R.style.AppErrorTheme);
        dialogBuilder.setView(dialogView);
        dialogView.findViewById(R.id.close_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
                dialog.cancel();
            }
        });
        dialogBuilder.setCancelable(false);
       dialog=dialogBuilder.show();
    }
}

