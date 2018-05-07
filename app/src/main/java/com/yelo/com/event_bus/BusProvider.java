package com.yelo.com.event_bus;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * @since 14-Jul-17.
 */
public class BusProvider
{
    private static final Bus BUS = new Bus(ThreadEnforcer.ANY);
    public static Bus getInstance()
    {
        return BUS;
    }
    private BusProvider() {
        // No instances.
    }

}

