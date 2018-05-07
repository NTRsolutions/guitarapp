package com.yelo.com.custom_fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * <h>TextViewOrgano</h>
 * <p>
 *     In this class we used to make my custom TextView with Organo font.
 * </p>
 * @since 17-May-17
 */
public class TextViewOrgano extends AppCompatTextView
{
    private static Typeface FONT_NAME;

    public TextViewOrgano(Context context) {
        super(context);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(),"fonts/Organo.ttf");
        this.setTypeface(FONT_NAME);
    }

    public TextViewOrgano(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(),"fonts/Organo.ttf");
        this.setTypeface(FONT_NAME);
    }

    public TextViewOrgano(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(),"fonts/Organo.ttf");
        this.setTypeface(FONT_NAME);
    }
}
