package com.yelo.com.custom_scroll_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * <h>AlphaForeGroundColorSpan</h>
 * @since 24.11.2017
 */
public class ScrollViewHelper extends ScrollView {

    private OnScrollViewListener mOnScrollViewListener;

    public ScrollViewHelper(Context context) {
        super(context);
    }
    public ScrollViewHelper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ScrollViewHelper(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    interface OnScrollViewListener {
        void onScrollChanged(ScrollViewHelper v, int l, int t, int oldl, int oldt);
    }

    public void setOnScrollViewListener(OnScrollViewListener l) {
        this.mOnScrollViewListener = l;
    }

    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        mOnScrollViewListener.onScrollChanged( this, l, t, oldl, oldt );
        super.onScrollChanged( l, t, oldl, oldt );
    }
}
