package com.demskigroup.guitaramps.aleret;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.demskigroup.guitaramps.R;

/**
 * @since  16/4/16.
 * @author 3Embed.
 */
public class CircleProgressDialog
{
    private static CircleProgressDialog CIRCLE_ALERET=new CircleProgressDialog();
    private Dialog progress_bar=null;
    private TextView progress_bar_title=null;
    LayoutInflater inflater = null;

    private CircleProgressDialog()
    {
    }

    public static CircleProgressDialog getInstance()
    {
        if(CIRCLE_ALERET==null)
        {
            CIRCLE_ALERET=new CircleProgressDialog();
            return CIRCLE_ALERET;
        }else
        {
            return CIRCLE_ALERET;
        }
    }

    public Dialog get_Circle_Progress_bar(Activity mactivity)
    {
        inflater = mactivity.getLayoutInflater();
        if(progress_bar!=null)
        {
            if(progress_bar.isShowing())
            {
                progress_bar.dismiss();
            }
        }
        /*
         * Initializing the Circle progress dialog. */
        progress_bar = new Dialog(mactivity,android.R.style.Theme_Translucent);
        progress_bar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        @SuppressLint("InflateParams") View dialogView = inflater.inflate(R.layout.circle_progress_dialog, null);
        progress_bar_title=(TextView)dialogView.findViewById(R.id.progress_title);
        progress_bar.setContentView(dialogView);
        progress_bar.setCancelable(true);
        progress_bar.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            public void onCancel(DialogInterface dialog)
            {
                //Canceled..
            }
        });
      return progress_bar;
    }

    public void set_Progress_title(String text)
    {
        if(progress_bar_title!=null&&text!=null)
        {
            progress_bar_title.setText(text);
        }

    }
    public void set_Title_Color(int color_code)
    {
        if(progress_bar_title!=null)
        {
            progress_bar_title.setTextColor(color_code);
        }
    }
    public void set_Title_Text_Style(Typeface title_text_style)
    {
        if(progress_bar_title!=null)
        {
            progress_bar_title.setTypeface(title_text_style);
        }
    }

}
