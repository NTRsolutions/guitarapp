package com.yelo.com.mqttchat.Utilities;

/**
 * @since  20/06/17.
 * @author 3Embed.
 * /45.55.223.151:8009
 */
public class ApiOnServer
{
    public static final String CHAT_RECEIVED_THUMBNAILS_FOLDER = "/Yelo/receivedThumbnails";
    public static final String CHAT_UPLOAD_THUMBNAILS_FOLDER = "/Yelo/upload";
    public static final String CHAT_DOODLES_FOLDER = "/Yelo/doodles";
    public static final String CHAT_DOWNLOADS_FOLDER = "/Yelo/";
    public static final String IMAGE_CAPTURE_URI = "/Yelo";
    public static final String CHAT_MULTER_UPLOAD_URL = "";     //http://54.23.222.452:8009/
    private static final String CHAT_UPLOAD_SERVER_URL = "";    //http://54.23.222.452/
    public static final String VIDEO_THUMBNAILS = "/Yelo/thumbnails";
    public static final String CHAT_UPLOAD_PATH = CHAT_UPLOAD_SERVER_URL +"chat/profilePics/";
    public static final String PROFILEPIC_UPLOAD_PATH = CHAT_UPLOAD_SERVER_URL +"chat/profilePics/";
    public static final String DELETE_DOWNLOAD = CHAT_MULTER_UPLOAD_URL +"deleteImage";
    public static final String HOST = "1883";   //54.23.222.452
    public static final String PORT = "1883";   //54.23.222.452
    private static final String API_MAIN_LINK ="http://34.209.156.227:5010/";  //http://54.23.222.452:5010/
    public static final String LOGIN_API = API_MAIN_LINK + "User/LoginWithEmail";
    public static final String VERIFY_API = API_MAIN_LINK + "User/verifyEmail";
    public static final String SIGNUP_API = API_MAIN_LINK + "User/SignupWithEmail";
    public static final String GET_USERS_API = API_MAIN_LINK + "User";
    public static final String TRENDING_STICKERS = "http://api.giphy.com/v1/stickers/trending?api_key=dc6zaTOxFJmzC";
    public static final String TRENDING_GIFS = "http://api.giphy.com/v1/gifs/trending?api_key=dc6zaTOxFJmzC";
    public static final String GIPHY_APIKEY = "&api_key=dc6zaTOxFJmzC";
    public static final String SEARCH_STICKERS = "http://api.giphy.com/v1/stickers/search?q=";
    public static final String SEARCH_GIFS = "http://api.giphy.com/v1/gifs/search?q=";
    public static final String USER_PROFILE = API_MAIN_LINK + "User/Profile";
    public static final String FETCH_CHATS = API_MAIN_LINK + "Chats";
    public static final String FETCH_CATEGORIES_MAIN = API_MAIN_LINK + "Chats";


    /*
     *Mqtt user name and password */
    public static final String MQTTUSER_NAME="guitaramps";    //appname
    public static final String MQTTPASSWORD="6ZWQyA92QwRbCr9s2Y";     //password
    /**
     * GET api to fetch the messages into the list*/
    public static final String FETCH_MESSAGES = API_MAIN_LINK + "Messages";
    /*
    * Authorization key fro chat */
    public static final String AUTH_KEY="";//server authorization key

    public static final String PUSH_KEY="";// firebase legacy server key
}
