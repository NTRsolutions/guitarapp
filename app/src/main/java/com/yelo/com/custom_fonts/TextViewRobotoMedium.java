package com.yelo.com.custom_fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * <h>TextViewRobotoMedium</h>
 * <p>
 *     In this class we used to make my custom TextView with roboto-medium font.
 * </p>
 * @since 3/30/2017
 */
public class TextViewRobotoMedium extends AppCompatTextView
{
    private static Typeface FONT_NAME;

    public TextViewRobotoMedium(Context context)
    {
        super(context);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-medium.ttf");
        this.setTypeface(FONT_NAME);
    }

    public TextViewRobotoMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-medium.ttf");
        this.setTypeface(FONT_NAME);
    }

    public TextViewRobotoMedium(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-medium.ttf");
        this.setTypeface(FONT_NAME);
    }
}
