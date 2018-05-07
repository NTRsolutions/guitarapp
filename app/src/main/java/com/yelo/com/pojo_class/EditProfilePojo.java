package com.yelo.com.pojo_class;

import com.yelo.com.pojo_class.edit_profile_pojo.EditProfileData;

/**
 * Created by hello on 4/15/2017.
 */

public class EditProfilePojo
{
    /*"code":200,
            "message":"ok",
            "token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiamF5eiIsImVtYWlsIjoiamF5ekBnbWFpbC5jb20iLCJpYXQiOjE0OTIyNTMzODksImV4cCI6MTQ5NzQzNzM4OX0.9Oux56S0KdLz0TrdzLRqKN536nyQPVyo0G8DoeUKM98",
            "data":{},
        "db2Response":{}*/

    private String code="",message="",token="";
    private EditProfileData data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public EditProfileData getData() {
        return data;
    }

    public void setData(EditProfileData data) {
        this.data = data;
    }
}
