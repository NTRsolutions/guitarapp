package com.demskigroup.guitaramps.badgeView;

import android.content.Context;

/**
 * <h>DisplayUtil</h>
 * @since 21-Nov-17
 */
class DisplayUtil {
    static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
