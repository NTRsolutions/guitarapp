package com.demskigroup.guitaramps.custom_fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * <h>TextViewRobotoBold</h>
 * <p>
 *     In this class we used to make my custom TextView with roboto-bold font.
 * </p>
 * @since 4/10/2017
 */
public class TextViewRobotoBold extends AppCompatTextView
{
    private static Typeface FONT_NAME;

    public TextViewRobotoBold(Context context)
    {
        super(context);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-bold.ttf");
        this.setTypeface(FONT_NAME);
    }

    public TextViewRobotoBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-bold.ttf");
        this.setTypeface(FONT_NAME);
    }

    public TextViewRobotoBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-bold.ttf");
        this.setTypeface(FONT_NAME);
    }
}

