package com.yelo.com.pojo_class.cloudinary_details_pojo;

/**
 * @since  8/12/16.
 */

public class Cloudinary_Details_reponse
{
    private Cloudinary_reponse response;

    private String message="",code="";

    public Cloudinary_reponse getResponse ()
    {
        return response;
    }

    public void setResponse (Cloudinary_reponse response)
    {
        this.response = response;
    }

    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getCode ()
    {
        return code;
    }

    public void setCode (String code)
    {
        this.code = code;
    }
}
