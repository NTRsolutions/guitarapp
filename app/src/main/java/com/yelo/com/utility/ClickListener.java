package com.yelo.com.utility;

import android.view.View;

/**
 * <h>ClickListener</h>
 * <p>
 *     In this interface we used to define onItemClick method. to get position and view
 *     whenever we click on recyclerview any particular row.
 * </p>
 * @since 4/12/2017.
 */
public interface ClickListener
{
    void onItemClick(View view,int position);
}
