package com.yelo.com.mqttchat.Utilities;

/*
 * Created by moda on 04/04/16.
 */

import android.app.Activity;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import com.yelo.com.R;
 /* Floating view is used to display a custom view for attachments
 in the chat screen */

public class FloatingView {


    private static PopupWindow popWindow;

    private FloatingView() {
    }


    public static void onShowPopup(Activity activity, View inflatedView) {

        Display display = activity.getWindowManager().getDefaultDisplay();


        float density = activity.getResources().getDisplayMetrics().density;


        final Point size = new Point();
        display.getSize(size);

        popWindow = new PopupWindow(inflatedView, size.x, ViewGroup.LayoutParams.WRAP_CONTENT,
                true);

        popWindow.setBackgroundDrawable(ContextCompat.getDrawable(activity,
                R.drawable.comment_popup_bg));

        popWindow.setFocusable(true);

        popWindow.setOutsideTouchable(true);

        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        popWindow.showAtLocation(activity.getCurrentFocus(), Gravity.TOP, 0,
                Math.round(86 * density));
    }

    public static void dismissWindow() {

        if (popWindow != null) {

            popWindow.dismiss();
        }
    }
}
