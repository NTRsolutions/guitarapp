package com.demskigroup.guitaramps.get_current_location;

/**
 * <h>FusedLocationReceiver</h>
 * <p>
 *     We used this interface for callback once we receive the current lat,lng
 * </p>
 * @since 25/7/16
 */
public interface FusedLocationReceiver
{
     void onUpdateLocation();
}
