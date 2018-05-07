package com.yelo.com.event_bus;

import com.yelo.com.pojo_class.home_explore_pojo.ExploreResponseDatas;

/**
 * <h>MarkAsSellingHolder</h>
 * <p>
 *     This is pojo class for passing events.
 * </p>
 * @since 17-Jul-17
 */
public class MarkAsSellingHolder
{
    private ExploreResponseDatas details;

    public MarkAsSellingHolder(ExploreResponseDatas details)
    {
        this.details=details;
    }

    public ExploreResponseDatas getDetails() {
        return details;
    }

    public void setDetails(ExploreResponseDatas details) {
        this.details = details;
    }
}
