package com.yelo.com.custom_fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * <h>TextViewRobotoRegular</h>
 * <p>
 *     In this class we used to make my custom TextView with roboto-regular font.
 * </p>
 * @since 3/29/2017
 */
public class TextViewRobotoRegular extends AppCompatTextView
{
    private static Typeface FONT_NAME;

    public TextViewRobotoRegular(Context context)
    {
        super(context);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(FONT_NAME);
    }

    public TextViewRobotoRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(FONT_NAME);
    }

    public TextViewRobotoRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(FONT_NAME);
    }
}
