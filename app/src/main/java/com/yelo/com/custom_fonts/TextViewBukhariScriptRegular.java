package com.yelo.com.custom_fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * <h>TextViewBukhariScriptRegular</h>
 * <p>
 *     In this class we used to make my custom TextView with BukhariScript-Regular font.
 * </p>
 * @since 3/29/2017
 */
public class TextViewBukhariScriptRegular extends AppCompatTextView
{
    private static Typeface FONT_NAME;

    public TextViewBukhariScriptRegular(Context context) {
        super(context);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(),"fonts/BukhariScript-Regular.otf");
        this.setTypeface(FONT_NAME);
    }

    public TextViewBukhariScriptRegular(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(),"fonts/BukhariScript-Regular.otf");
        this.setTypeface(FONT_NAME);
    }

    public TextViewBukhariScriptRegular(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (FONT_NAME==null)
            FONT_NAME=Typeface.createFromAsset(context.getAssets(),"fonts/BukhariScript-Regular.otf");
        this.setTypeface(FONT_NAME);
    }
}
