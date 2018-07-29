package com.demskigroup.guitaramps.custom_fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * <h>EditTextRobotoRegular</h>
 * <p>
 *     In this class we used to make my custom Edit Text with roboto-regular font.
 * </p>
 * @since 3/29/2017
 */
public class EditTextRobotoRegular extends AppCompatEditText
{
    private static Typeface FONT_NAME;

    public EditTextRobotoRegular(Context context) {
        super(context);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(FONT_NAME);
    }

    public EditTextRobotoRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(FONT_NAME);
    }

    public EditTextRobotoRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(FONT_NAME);
    }
}
