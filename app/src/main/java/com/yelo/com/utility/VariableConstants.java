package com.yelo.com.utility;

/**
 * <h>VariableConstants</h>
 * <p>
 *     In this class we used to declare the java constant variables
 *     which is being used throughout the app.
 * </p>
 * @since 4/4/2017
 */
public class VariableConstants
{
    static final String DATE_FORMAT="yyyy-MM-dd HH:mm:ss";
    public static final int PERMISSION_REQUEST_CODE = 8051;
    public static final int PROFILE_REQUEST_CODE=100;
    public static final int FILTER_REQUEST_CODE=578;
    public static final int REQUEST_CHECK_SETTINGS = 317;

    public static final int MODEL_REQUEST_CODE=150;
    public static final int MODEL_REQUEST_YEAR_CODE=151;
    public static final int MODEL_REQUEST_MAKE_CODE=152;


    public static final int CATEGORY_REQUEST_CODE=876;
    public static final int CONDITION_REQUEST_CODE=856;
    public static final int CURRENCY_REQUEST_CODE=816;
    public static final int CHOOSE_LOCATION_REQ_CODE=106;
    public static final int CHANGE_LOC_REQ_CODE=708;
    public static final int GOOGLE_LOGIN_REQ_CODE=701;
    public static final int SELECT_GALLERY_IMG_REQ_CODE=908;
    public static final int PRODUCT_DETAILS_REQ_CODE=903;
    public static final String EXTRA_ANIMAL_IMAGE_TRANSITION_NAME = "animal_image_transition_name";
    public static final String TYPE_MANUAL="1";
    public static final String TYPE_FACEBOOK="2";
    public static final String TYPE_GOOGLE="3";
    public static final String DEVICE_TYPE="2";
    public static final int VERIFY_EMAIL_REQ_CODE=757;
    public static final int SELLING_REQ_CODE=680;
    public static final int PAYPAL_REQ_CODE=979;
    public static final String TWITTER_KEY = "";//twitter key
    public static final String TWITTER_SECRET = "";//twitter secret key
    public static final int TWEETER_REQUEST_CODE = 478;
    public static boolean IS_TO_SHOW_START_BROWSING=false;
    public static final String GET_PAYPAL_LINK="https://www.paypal.com/paypalme/grab";
    public static final int USER_FOLLOW_REQ_CODE=9795;
    public static final int FB_FRIEND_REQ_CODE=463;
    public static final int CONTACT_FRIEND_REQ_CODE=461;
    static final String BODY_AUT_KEY="";//put auth key of server
    static final String BODY_AUT_PASSWORD="";//put auth password of server
    public static final int FOLLOW_COUNT_REQ_CODE=703;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static boolean IS_TO_ADD_SOLD_ITEM=false;
    public static final int UPDATE_IMAGE_REQ_CODE=568;
    public static final int IS_NOTIFICATION_SEEN_REQ_CODE=697;
    public static final int FAV_ITEM_REQ_CODE=902;
    public static final int LANDING_REQ_CODE = 765;
    public static final int LOGIN_SIGNUP_REQ_CODE = 801;
    public static final int NUMBER_VERIFICATION_REQ_CODE = 7076;
    public static final int RATE_USER_REQ_CODE=890;

    // set duration type variables for insight api
    public static final String WEEK="week";
    public static final String MONTH="month";
    public static final String YEAR="year";

    /**
     * Camera Fields
     */
    //keep track of camera capture intent
    public static final int CAMERA_CAPTURE = 1;
    //keep track of cropping intent
    public static final int PIC_CROP = 3;
    //keep track of gallery intent
    public static final int PICK_IMAGE_REQUEST = 2;
}
