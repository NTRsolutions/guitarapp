package com.demskigroup.guitaramps.custom_fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

/**
 * <h>CheckBoxRobotoRegular</h>
 * <p>
 *     In this class we used to make my custom CheckBox with roboto-regular font.
 * </p>
 * @since 4/17/2017
 */
public class CheckBoxRobotoRegular extends AppCompatCheckBox
{
    private static Typeface FONT_NAME;

    public CheckBoxRobotoRegular(Context context) {
        super(context);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(FONT_NAME);
    }

    public CheckBoxRobotoRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(FONT_NAME);
    }

    public CheckBoxRobotoRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-regular.ttf");
        this.setTypeface(FONT_NAME);
    }
}
