package com.demskigroup.guitaramps.county_code_picker;

/**
 * <h>ClickListener</h>
 * <p>
 *     In this interface we used to define getCode method. to get code and name
 *     whenever we click on recyclerview any particular row.
 * </p>
 * @since 4/12/2017.
 */
public interface SetCountryCodeListener
{
    void getCode(String code,String name);
}
