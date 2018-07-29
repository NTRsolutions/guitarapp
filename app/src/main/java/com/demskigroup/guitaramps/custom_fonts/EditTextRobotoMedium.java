package com.demskigroup.guitaramps.custom_fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * <h>EditTextRobotoMedium</h>
 * <p>
 *     In this class we used to make my custom Edit text with roboto-medium font.
 * </p>
 * @since 4/11/2017
 */
public class EditTextRobotoMedium extends AppCompatEditText
{
    private static Typeface FONT_NAME;

    public EditTextRobotoMedium(Context context)
    {
        super(context);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(),"fonts/roboto-medium.ttf");
        this.setTypeface(FONT_NAME);
    }

    public EditTextRobotoMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(),"fonts/roboto-medium.ttf");
        this.setTypeface(FONT_NAME);
    }

    public EditTextRobotoMedium(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(),"fonts/roboto-medium.ttf");
        this.setTypeface(FONT_NAME);
    }
}
