package com.yelo.com.custom_scroll_view;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

/**
 * <h>AlphaForeGroundColorSpan</h>
 * @since 24.11.2017
 */
public class AlphaForeGroundColorSpan extends ForegroundColorSpan {

    private float mAlpha;

    public AlphaForeGroundColorSpan(int color) {
        super(color);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(getAlphaColor());
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    public float getAlpha() {
        return mAlpha;
    }

    private int getAlphaColor() {
        int foregroundColor = getForegroundColor();
        return Color.argb((int) (mAlpha * 255), Color.red(foregroundColor), Color.green(foregroundColor), Color.blue(foregroundColor));
    }
}
