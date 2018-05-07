package com.yelo.com.custom_fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.AttributeSet;

/**
 * <h>RadioButtonMedium</h>
 * <p>
 *     In this class we used to make my custom Radio-button with roboto-medium font.
 * </p>
 * @since 15-May-17
 */
public class RadioButtonMedium extends AppCompatRadioButton
{
    private static Typeface FONT_NAME;

    public RadioButtonMedium(Context context)
    {
        super(context);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-medium.ttf");
        this.setTypeface(FONT_NAME);
    }

    public RadioButtonMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-medium.ttf");
        this.setTypeface(FONT_NAME);
    }

    public RadioButtonMedium(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(), "fonts/roboto-medium.ttf");
        this.setTypeface(FONT_NAME);
    }
}
