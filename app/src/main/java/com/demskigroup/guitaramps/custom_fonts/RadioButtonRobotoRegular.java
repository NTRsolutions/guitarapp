package com.demskigroup.guitaramps.custom_fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

/**
 * <h>RadioButtonRobotoRegular</h>
 * <p>
 *     In this class we used to make my custom Radio-button with roboto-regular font.
 * </p>
 * @since 4/18/2017
 */
public class RadioButtonRobotoRegular extends AppCompatRadioButton
{
    private static Typeface FONT_NAME;

    public RadioButtonRobotoRegular(Context context) {
        super(context);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(FONT_NAME);
    }

    public RadioButtonRobotoRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(FONT_NAME);
    }

    public RadioButtonRobotoRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(FONT_NAME);
    }
}
