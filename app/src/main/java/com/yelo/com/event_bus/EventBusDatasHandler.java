package com.yelo.com.event_bus;

import android.app.Activity;

import com.yelo.com.pojo_class.UserPostDataPojo;
import com.yelo.com.pojo_class.facebook_friends_pojo.FacebookFriendsPosts;
import com.yelo.com.pojo_class.home_explore_pojo.ExploreResponseDatas;
import com.yelo.com.pojo_class.likedPosts.LikedPostResponseDatas;
import com.yelo.com.pojo_class.phone_contact_pojo.PhoneContactPostData;
import com.yelo.com.pojo_class.product_details_pojo.ProductResponseDatas;
import com.yelo.com.pojo_class.profile_selling_pojo.ProfileSellingData;
import com.yelo.com.pojo_class.profile_sold_pojo.ProfileSoldDatas;
import com.yelo.com.pojo_class.rate_user_pojo.RateUserDatas;
import com.yelo.com.pojo_class.social_frag_pojo.SocialDatas;
import com.yelo.com.pojo_class.sold_somewhere_else_pojo.SoldSomeWhereData;
import com.yelo.com.utility.SessionManager;

/**
 * <h>EventBusDatasHandler</h>
 * <p>
 *   This class acts as a mediator between the fragments while passing datas through Event Bus.
 * </p>
 * @since 26-Oct-17.
 */
public class EventBusDatasHandler
{
    private static final String TAG = EventBusDatasHandler.class.getSimpleName();
    private SessionManager mSessionManager;

    public EventBusDatasHandler(Activity mActivity) {
        mSessionManager = new SessionManager(mActivity);
    }

    /**
     * <h>SetSocialDatasFromProductDetails</h>
     * <p>
     *     In this method we used to add or remove the item from Social frag class
     *     when user follow or unfollow product from product details.
     * </p>
     * @param mProductResponse the mProductResponse the pojo class which contains the complete datas of the product.
     * @param isToAdd The boolean flag if it is true then add or else remove.
     */
    public void setSocialDatasFromProductDetails(ProductResponseDatas mProductResponse, boolean isToAdd)
    {
        try {
            if (mProductResponse!=null)
            {
                SocialDatas socialDatas = new SocialDatas();
                socialDatas.setToAddSocialData(isToAdd);
                socialDatas.setPostedOn(mProductResponse.getPostedOn());
                socialDatas.setProductName(mProductResponse.getProductName());
                socialDatas.setCategoryData(mProductResponse.getCategoryData());
                socialDatas.setCurrency(mProductResponse.getCurrency());
                socialDatas.setPrice(mProductResponse.getPrice());
                socialDatas.setMemberProfilePicUrl(mProductResponse.getMemberProfilePicUrl());
                socialDatas.setMainUrl(mProductResponse.getMainUrl());
                socialDatas.setLikes(mProductResponse.getLikes());
                socialDatas.setClickCount(mProductResponse.getClickCount());
                socialDatas.setLikeStatus(mProductResponse.getLikeStatus());
                socialDatas.setPostId(mProductResponse.getPostId());
                socialDatas.setMembername(mProductResponse.getMembername());
                BusProvider.getInstance().post(socialDatas);
            }
        }
        catch (Exception e)
        {
            System.out.println(TAG+" "+"social event bus error="+e.getMessage());
        }
    }

    /**
     * <h>SetFavDatasFromProductDetails</h>
     * <p>
     *     In this method we used to add or remove the item from Social frag class
     *     when user like or unlike product from product details.
     * </p>
     * @param mProductResponseDatas the mProductResponse the pojo class which contains the complete datas of the product.
     * @param isToAdd The boolean flag if it is true then add or else remove.
     */
    public void setFavDatasFromProductDetails(ProductResponseDatas mProductResponseDatas,boolean isToAdd)
    {
        LikedPostResponseDatas likedPostCategoryDatas=new LikedPostResponseDatas();
        likedPostCategoryDatas.setToAddLikedItem(isToAdd);
        likedPostCategoryDatas.setMainUrl(mProductResponseDatas.getMainUrl());
        likedPostCategoryDatas.setProductName(mProductResponseDatas.getProductName());
        likedPostCategoryDatas.setLikes(mProductResponseDatas.getLikes());
        likedPostCategoryDatas.setLikeStatus(mProductResponseDatas.getLikeStatus());
        likedPostCategoryDatas.setCurrency(mProductResponseDatas.getCurrency());
        likedPostCategoryDatas.setPrice(mProductResponseDatas.getPrice());
        likedPostCategoryDatas.setPostedOn(mProductResponseDatas.getPostedOn());
        likedPostCategoryDatas.setThumbnailImageUrl(mProductResponseDatas.getThumbnailImageUrl());
        likedPostCategoryDatas.setLikedByUsers(mProductResponseDatas.getLikedByUsers());
        likedPostCategoryDatas.setDescription(mProductResponseDatas.getDescription());
        likedPostCategoryDatas.setCondition(mProductResponseDatas.getCondition());
        likedPostCategoryDatas.setPlace(mProductResponseDatas.getPlace());
        likedPostCategoryDatas.setLatitude(mProductResponseDatas.getLatitude());
        likedPostCategoryDatas.setLongitude(mProductResponseDatas.getLongitude());
        likedPostCategoryDatas.setPostId(mProductResponseDatas.getPostId());
        likedPostCategoryDatas.setPostsType(mProductResponseDatas.getPostType());
        likedPostCategoryDatas.setContainerWidth(mProductResponseDatas.getContainerWidth());
        likedPostCategoryDatas.setContainerHeight(mProductResponseDatas.getContainerHeight());
        BusProvider.getInstance().post(likedPostCategoryDatas);
    }

    /**
     * <h>setSocialDatasFromDiscovery</h>
     * <p>
     *     In this method we used to add or remove the item from Social frag class
     *     when user follow or unfollow product from DiscoverPeople screen.
     * </p>
     * @param mUserPostDataPojo the mProductResponse the pojo class which contains the complete datas of the product.
     * @param isToAdd The boolean flag if it is true then add or else remove.
     */
    public void setSocialDatasFromDiscovery(String memberName, String memberPic, UserPostDataPojo mUserPostDataPojo, boolean isToAdd)
    {
        try {
            if (mUserPostDataPojo!=null)
            {
                SocialDatas socialDatas = new SocialDatas();
                socialDatas.setToAddSocialData(isToAdd);
                socialDatas.setPostedOn(mUserPostDataPojo.getPostedOn());
                socialDatas.setProductName(mUserPostDataPojo.getProductName());
                socialDatas.setCategoryData(mUserPostDataPojo.getCategory());
                socialDatas.setCurrency(mUserPostDataPojo.getCurrency());
                socialDatas.setPrice(mUserPostDataPojo.getPrice());
                socialDatas.setMemberProfilePicUrl(memberPic);
                socialDatas.setMainUrl(mUserPostDataPojo.getMainUrl());
                socialDatas.setLikes(mUserPostDataPojo.getLikes());
                socialDatas.setClickCount(mUserPostDataPojo.getClickCount());
                socialDatas.setLikeStatus(mUserPostDataPojo.getLikeStatus());
                socialDatas.setPostId(mUserPostDataPojo.getPostId());
                socialDatas.setMembername(memberName);
                BusProvider.getInstance().post(socialDatas);
            }
        }
        catch (Exception e)
        {
            System.out.println(TAG+" "+"social event bus error="+e.getMessage());
        }
    }

    /**
     * <h>setSocialDatasFromFbFriends</h>
     * <p>
     *     In this method we used to add or remove the item from Social frag class
     *     when user follow or unfollow product from DiscoverPeople screen.
     * </p>
     * @param mFacebookFriendsPosts the mProductResponse the pojo class which contains the complete datas of the product.
     * @param isToAdd The boolean flag if it is true then add or else remove.
     */
    public void setSocialDatasFromFbFriends(FacebookFriendsPosts mFacebookFriendsPosts, boolean isToAdd)
    {
        try {
            if (mFacebookFriendsPosts!=null)
            {
                SocialDatas socialDatas = new SocialDatas();
                socialDatas.setToAddSocialData(isToAdd);
                socialDatas.setPostedOn(mFacebookFriendsPosts.getPostedOn());
                socialDatas.setProductName("");
                socialDatas.setCategoryData(null);
                socialDatas.setCurrency("");
                socialDatas.setPrice("");
                socialDatas.setMemberProfilePicUrl("");
                socialDatas.setMainUrl(mFacebookFriendsPosts.getMainUrl());
                socialDatas.setLikes(mFacebookFriendsPosts.getLikes());
                socialDatas.setClickCount("");
                socialDatas.setLikeStatus("");
                socialDatas.setPostId(mFacebookFriendsPosts.getPostId());
                socialDatas.setMembername("");
                BusProvider.getInstance().post(socialDatas);
            }
        }
        catch (Exception e)
        {
            System.out.println(TAG+" "+"social event bus error="+e.getMessage());
        }
    }

    /**
     * <h>setSocialDatasFromFbFriends</h>
     * <p>
     *     In this method we used to add or remove the item from Social frag class
     *     when user follow or unfollow product from DiscoverPeople screen.
     * </p>
     * @param phoneContactPostData the mProductResponse the pojo class which contains the complete datas of the product.
     * @param isToAdd The boolean flag if it is true then add or else remove.
     */
    public void setSocialDatasFromContactsFriends(PhoneContactPostData phoneContactPostData, boolean isToAdd)
    {
        try {
            if (phoneContactPostData!=null)
            {
                SocialDatas socialDatas = new SocialDatas();
                socialDatas.setToAddSocialData(isToAdd);
                socialDatas.setPostedOn(phoneContactPostData.getPostedOn());
                socialDatas.setProductName("");
                socialDatas.setCategoryData(null);
                socialDatas.setCurrency("");
                socialDatas.setPrice("");
                socialDatas.setMemberProfilePicUrl("");
                socialDatas.setMainUrl(phoneContactPostData.getMainUrl());
                socialDatas.setLikes(phoneContactPostData.getLikes());
                socialDatas.setClickCount("");
                socialDatas.setLikeStatus("");
                socialDatas.setPostId(phoneContactPostData.getPostId());
                socialDatas.setMembername("");
                BusProvider.getInstance().post(socialDatas);
            }
        }
        catch (Exception e)
        {
            System.out.println(TAG+" "+"social event bus error="+e.getMessage());
        }
    }

    /**
     * <h>AddSoldDatas</h>
     * <p>
     *     In this method we used to make obj of Sold Frag datas set all values of that and
     *     send that object to the SoldFrag via event bus.
     * </p>
     * @param mSoldSomeWhereData The reference variables of SoldSomeWhereData.
     */
    public void addSoldDatasFromEditPost(SoldSomeWhereData mSoldSomeWhereData)
    {
        ProfileSoldDatas profileSoldDatas = new ProfileSoldDatas();
        profileSoldDatas.setPostNodeId(mSoldSomeWhereData.getPostNodeId());
        profileSoldDatas.setIsPromoted("");
        profileSoldDatas.setPlanId("");
        profileSoldDatas.setLikes("");
        profileSoldDatas.setMainUrl(mSoldSomeWhereData.getMainUrl());
        profileSoldDatas.setPostLikedBy("");
        profileSoldDatas.setPlace(mSoldSomeWhereData.getPlace());
        profileSoldDatas.setThumbnailImageUrl(mSoldSomeWhereData.getThumbnailImageUrl());
        profileSoldDatas.setPostId(mSoldSomeWhereData.getPostId());
        profileSoldDatas.setProductsTagged(mSoldSomeWhereData.getProductsTagged());
        profileSoldDatas.setProductsTaggedCoordinates("");
        profileSoldDatas.setHasAudio(mSoldSomeWhereData.getHasAudio());
        profileSoldDatas.setContainerHeight(mSoldSomeWhereData.getContainerHeight());
        profileSoldDatas.setContainerWidth(mSoldSomeWhereData.getContainerWidth());
        profileSoldDatas.setHashTags("");
        profileSoldDatas.setPostCaption(mSoldSomeWhereData.getPostCaption());
        profileSoldDatas.setLatitude(mSoldSomeWhereData.getLatitude());
        profileSoldDatas.setLongitude(mSoldSomeWhereData.getLongitude());
        profileSoldDatas.setThumbnailUrl1(mSoldSomeWhereData.getThumbnailUrl1());
        profileSoldDatas.setImageUrl1(mSoldSomeWhereData.getImageUrl1());
        profileSoldDatas.setThumbnailImageUrl(mSoldSomeWhereData.getThumbnailImageUrl());
        profileSoldDatas.setContainerWidth1(mSoldSomeWhereData.getContainerWidth1());
        profileSoldDatas.setContainerHeight1(mSoldSomeWhereData.getContainerHeight1());
        profileSoldDatas.setImageUrl2(mSoldSomeWhereData.getImageUrl2());
        profileSoldDatas.setThumbnailUrl2(mSoldSomeWhereData.getThumbnailUrl2());
        profileSoldDatas.setContainerHeight2(mSoldSomeWhereData.getContainerHeight2());
        profileSoldDatas.setContainerWidth2(mSoldSomeWhereData.getContainerWidth2());
        profileSoldDatas.setThumbnailUrl3(mSoldSomeWhereData.getThumbnailUrl3());
        profileSoldDatas.setImageUrl3(mSoldSomeWhereData.getImageUrl3());
        profileSoldDatas.setContainerHeight3(mSoldSomeWhereData.getContainerHeight3());
        profileSoldDatas.setContainerWidth3(mSoldSomeWhereData.getContainerWidth3());
        profileSoldDatas.setThumbnailUrl4(mSoldSomeWhereData.getThumbnailUrl4());
        profileSoldDatas.setImageUrl4(mSoldSomeWhereData.getImageUrl4());
        profileSoldDatas.setContainerHeight4(mSoldSomeWhereData.getContainerHeight4());
        profileSoldDatas.setContainerWidth4(mSoldSomeWhereData.getContainerWidth4());
        profileSoldDatas.setPostsType("");
        profileSoldDatas.setPostedOn(mSoldSomeWhereData.getPostedOn());
        profileSoldDatas.setLikeStatus("");
        profileSoldDatas.setSold(mSoldSomeWhereData.getSold());
        profileSoldDatas.setProductUrl(mSoldSomeWhereData.getProductUrl());
        profileSoldDatas.setDescription(mSoldSomeWhereData.getDescription());
        profileSoldDatas.setNegotiable("");
        profileSoldDatas.setCondition(mSoldSomeWhereData.getCondition());
        profileSoldDatas.setPrice(mSoldSomeWhereData.getPrice());
        profileSoldDatas.setCurrency(mSoldSomeWhereData.getCurrency());
        profileSoldDatas.setProductName(mSoldSomeWhereData.getProductName());
        profileSoldDatas.setTotalComments("");
        profileSoldDatas.setCategoryData(mSoldSomeWhereData.getCategoryData());
        BusProvider.getInstance().post(profileSoldDatas);
    }


    /**
     * <h>AddSoldDatas</h>
     * <p>
     *     In this method we used to make obj of Sold Frag datas set all values of that and
     *     send that object to the SoldFrag via event bus.
     * </p>
     * @param mRateUserDatas The reference variables of SoldSomeWhereData.
     */
    public void addSoldDatasFromRateUser(RateUserDatas mRateUserDatas)
    {
        ProfileSoldDatas profileSoldDatas = new ProfileSoldDatas();
        profileSoldDatas.setPostNodeId(mRateUserDatas.getPostNodeId());
        profileSoldDatas.setIsPromoted("");
        profileSoldDatas.setPlanId("");
        profileSoldDatas.setLikes("");
        profileSoldDatas.setMainUrl(mRateUserDatas.getMainUrl());
        profileSoldDatas.setPostLikedBy("");
        profileSoldDatas.setPlace(mRateUserDatas.getPlace());
        profileSoldDatas.setThumbnailImageUrl(mRateUserDatas.getThumbnailImageUrl());
        profileSoldDatas.setPostId(mRateUserDatas.getPostId());
        profileSoldDatas.setProductsTagged(mRateUserDatas.getProductsTagged());
        profileSoldDatas.setProductsTaggedCoordinates("");
        profileSoldDatas.setHasAudio("");
        profileSoldDatas.setContainerHeight(mRateUserDatas.getContainerHeight());
        profileSoldDatas.setContainerWidth(mRateUserDatas.getContainerWidth());
        profileSoldDatas.setHashTags("");
        profileSoldDatas.setPostCaption(mRateUserDatas.getPostCaption());
        profileSoldDatas.setLatitude(mRateUserDatas.getLatitude());
        profileSoldDatas.setLongitude(mRateUserDatas.getLongitude());
        profileSoldDatas.setThumbnailUrl1(mRateUserDatas.getThumbnailUrl1());
        profileSoldDatas.setImageUrl1(mRateUserDatas.getImageUrl1());
        profileSoldDatas.setThumbnailImageUrl(mRateUserDatas.getThumbnailImageUrl());
        profileSoldDatas.setContainerWidth1(mRateUserDatas.getContainerWidth1());
        profileSoldDatas.setContainerHeight1(mRateUserDatas.getContainerHeight1());
        profileSoldDatas.setImageUrl2(mRateUserDatas.getImageUrl2());
        profileSoldDatas.setThumbnailUrl2(mRateUserDatas.getThumbnailUrl2());
        profileSoldDatas.setContainerHeight2(mRateUserDatas.getContainerHeight2());
        profileSoldDatas.setContainerWidth2(mRateUserDatas.getContainerWidth2());
        profileSoldDatas.setThumbnailUrl3(mRateUserDatas.getThumbnailUrl3());
        profileSoldDatas.setImageUrl3(mRateUserDatas.getImageUrl3());
        profileSoldDatas.setContainerHeight3(mRateUserDatas.getContainerHeight3());
        profileSoldDatas.setContainerWidth3(mRateUserDatas.getContainerWidth3());
        profileSoldDatas.setThumbnailUrl4(mRateUserDatas.getThumbnailUrl4());
        profileSoldDatas.setImageUrl4(mRateUserDatas.getImageUrl4());
        profileSoldDatas.setContainerHeight4(mRateUserDatas.getContainerHeight4());
        profileSoldDatas.setContainerWidth4(mRateUserDatas.getContainerWidth4());
        profileSoldDatas.setPostsType("");
        profileSoldDatas.setPostedOn(mRateUserDatas.getPostedOn());
        profileSoldDatas.setLikeStatus("");
        profileSoldDatas.setSold(mRateUserDatas.getSold());
        profileSoldDatas.setProductUrl(mRateUserDatas.getProductUrl());
        profileSoldDatas.setDescription(mRateUserDatas.getDescription());
        profileSoldDatas.setNegotiable("");
        profileSoldDatas.setCondition(mRateUserDatas.getCondition());
        profileSoldDatas.setPrice(mRateUserDatas.getPrice());
        profileSoldDatas.setCurrency(mRateUserDatas.getCurrency());
        profileSoldDatas.setProductName(mRateUserDatas.getProductName());
        profileSoldDatas.setTotalComments("");
        profileSoldDatas.setCategoryData(mRateUserDatas.getCategoryData());
        BusProvider.getInstance().post(profileSoldDatas);
    }

    /**
     * <h>AddHomePageDatas</h>
     * <p>
     *     In this method we used to make obj of Home Frag datas set all values of that and
     *     send that object to the Selling via event bus.
     * </p>
     */
    public void removeHomePageDatasFromEditPost(String postId)
    {
        ExploreResponseDatas mExploreResponseDatas = new ExploreResponseDatas();
        mExploreResponseDatas.setToRemoveHomeItem(true);
        mExploreResponseDatas.setPostId(postId);
        BusProvider.getInstance().post(mExploreResponseDatas);
    }

    /**
     * <h>SetSocialDatas</h>
     * <p>
     *     In this method we used to send the one complete object of a followed product
     *     to the socail screen through screen.
     * </p>
     */
    public void removeSocialDatasFromEditPost(String postId)
    {
        try {
            SocialDatas socialDatas = new SocialDatas();
            socialDatas.setToAddSocialData(false);
            socialDatas.setPostId(postId);
            BusProvider.getInstance().post(socialDatas);
        }
        catch (Exception e)
        {
            System.out.println(TAG+" "+"social event bus error="+e.getMessage());
        }
    }

    /**
     * <h>AddSellingDatas</h>
     * <p>
     *     In this method we used to make obj of Selling Frag datas set all values of that and
     *     send that object to the Selling via event bus.
     * </p>
     */
    public void removeSellingDatasFromEditPost(String postId)
    {
        ProfileSellingData profileSellingData = new ProfileSellingData();
        profileSellingData.setToRemoveSellingItem(true);
        profileSellingData.setPostId(postId);
        BusProvider.getInstance().post(profileSellingData);
    }

    /**
     * <h>AddSellingDatas</h>
     * <p>
     *     In this method we used to make obj of Selling Frag datas set all values of that and
     *     send that object to the Selling via event bus.
     * </p>
     * @param mProductResponseDatas The reference variables of ProductResponseDatas.
     */
    public void addSellingDatasFromProductDetails(ProductResponseDatas mProductResponseDatas)
    {
        ProfileSellingData profileSellingData = new ProfileSellingData();
        profileSellingData.setPostNodeId("");
        profileSellingData.setIsPromoted("");
        profileSellingData.setPlanId("");
        profileSellingData.setLikes(mProductResponseDatas.getLikes());
        profileSellingData.setMainUrl(mProductResponseDatas.getMainUrl());
        profileSellingData.setPostLikedBy("");
        profileSellingData.setPlace(mProductResponseDatas.getPlace());
        profileSellingData.setThumbnailImageUrl(mProductResponseDatas.getThumbnailImageUrl());
        profileSellingData.setPostId(mProductResponseDatas.getPostId());
        profileSellingData.setProductsTagged(mProductResponseDatas.getProductsTagged());
        profileSellingData.setProductsTaggedCoordinates(mProductResponseDatas.getProductsTaggedCoordinates());
        profileSellingData.setHasAudio(mProductResponseDatas.getHasAudio());
        profileSellingData.setContainerHeight(mProductResponseDatas.getContainerHeight());
        profileSellingData.setContainerWidth(mProductResponseDatas.getContainerWidth());
        profileSellingData.setHashTags(mProductResponseDatas.getHashTags());
        profileSellingData.setPostCaption("");
        profileSellingData.setLatitude(mProductResponseDatas.getLatitude());
        profileSellingData.setLongitude(mProductResponseDatas.getLongitude());
        profileSellingData.setThumbnailUrl1(mProductResponseDatas.getThumbnailUrl1());
        profileSellingData.setImageUrl1(mProductResponseDatas.getImageUrl1());
        profileSellingData.setThumbnailImageUrl(mProductResponseDatas.getThumbnailImageUrl());
        profileSellingData.setContainerWidth1(mProductResponseDatas.getContainerWidth1());
        profileSellingData.setContainerHeight1(mProductResponseDatas.getContainerHeight1());
        profileSellingData.setImageUrl2(mProductResponseDatas.getImageUrl2());
        profileSellingData.setThumbnailUrl2(mProductResponseDatas.getThumbnailUrl2());
        profileSellingData.setContainerHeight2(mProductResponseDatas.getContainerHeight2());
        profileSellingData.setContainerWidth2(mProductResponseDatas.getContainerWidth2());
        profileSellingData.setThumbnailUrl3(mProductResponseDatas.getThumbnailUrl3());
        profileSellingData.setImageUrl3(mProductResponseDatas.getImageUrl3());
        profileSellingData.setContainerHeight3(mProductResponseDatas.getContainerHeight3());
        profileSellingData.setContainerWidth3(mProductResponseDatas.getContainerWidth3());
        profileSellingData.setThumbnailUrl4(mProductResponseDatas.getThumbnailUrl4());
        profileSellingData.setImageUrl4(mProductResponseDatas.getImageUrl4());
        profileSellingData.setContainerHeight4(mProductResponseDatas.getContainerHeight4());
        profileSellingData.setContainerWidth4(mProductResponseDatas.getContainerWidth4());
        profileSellingData.setPostsType("");
        profileSellingData.setPostedOn(mProductResponseDatas.getPostedOn());
        profileSellingData.setLikeStatus(mProductResponseDatas.getLikeStatus());
        profileSellingData.setSold(""+mProductResponseDatas.getSold());
        profileSellingData.setProductUrl(mProductResponseDatas.getProductUrl());
        profileSellingData.setDescription(mProductResponseDatas.getDescription());
        profileSellingData.setNegotiable(mProductResponseDatas.getNegotiable());
        profileSellingData.setCondition(mProductResponseDatas.getCondition());
        profileSellingData.setPrice(mProductResponseDatas.getPrice());
        profileSellingData.setCurrency(mProductResponseDatas.getCurrency());
        profileSellingData.setProductName(mProductResponseDatas.getProductName());
        profileSellingData.setTotalComments("");
        profileSellingData.setCategoryData(mProductResponseDatas.getCategoryData());
        BusProvider.getInstance().post(profileSellingData);
    }

    /**
     * <h>AddHomePageDatas</h>
     * <p>
     *     In this method we used to make obj of Home Frag datas set all values of that and
     *     send that object to the Selling via event bus.
     * </p>
     * @param mProductResponseDatas The reference variables of ProductResponseDatas.
     */
    public void addHomePageDatasFromProductDetails(ProductResponseDatas mProductResponseDatas)
    {
        ExploreResponseDatas mExploreResponseDatas = new ExploreResponseDatas();
        mExploreResponseDatas.setPostedByUserName(mSessionManager.getUserName());
        mExploreResponseDatas.setPostNodeId("");
        mExploreResponseDatas.setIsPromoted("");
        mExploreResponseDatas.setLikes(mProductResponseDatas.getLikes());
        mExploreResponseDatas.setMainUrl(mProductResponseDatas.getMainUrl());
        mExploreResponseDatas.setPlace(mProductResponseDatas.getPlace());
        mExploreResponseDatas.setThumbnailImageUrl(mProductResponseDatas.getThumbnailImageUrl());
        mExploreResponseDatas.setPostId(mProductResponseDatas.getPostId());
        mExploreResponseDatas.setHasAudio(mProductResponseDatas.getHasAudio());
        mExploreResponseDatas.setContainerHeight(mProductResponseDatas.getContainerHeight());
        mExploreResponseDatas.setContainerWidth(mProductResponseDatas.getContainerWidth());
        mExploreResponseDatas.setHashTags(mProductResponseDatas.getHashTags());
        mExploreResponseDatas.setPostCaption("");
        mExploreResponseDatas.setLatitude(mProductResponseDatas.getLatitude());
        mExploreResponseDatas.setLongitude(mProductResponseDatas.getLongitude());
        mExploreResponseDatas.setThumbnailUrl1(mProductResponseDatas.getThumbnailUrl1());
        mExploreResponseDatas.setImageUrl1(mProductResponseDatas.getImageUrl1());
        mExploreResponseDatas.setThumbnailImageUrl(mProductResponseDatas.getThumbnailImageUrl());
        mExploreResponseDatas.setContainerWidth1(mProductResponseDatas.getContainerWidth1());
        mExploreResponseDatas.setContainerHeight1(mProductResponseDatas.getContainerHeight1());
        mExploreResponseDatas.setImageUrl2(mProductResponseDatas.getImageUrl2());
        mExploreResponseDatas.setThumbnailUrl2(mProductResponseDatas.getThumbnailUrl2());
        mExploreResponseDatas.setContainerHeight2(mProductResponseDatas.getContainerHeight2());
        mExploreResponseDatas.setContainerWidth2(mProductResponseDatas.getContainerWidth2());
        mExploreResponseDatas.setThumbnailUrl3(mProductResponseDatas.getThumbnailUrl3());
        mExploreResponseDatas.setImageUrl3(mProductResponseDatas.getImageUrl3());
        mExploreResponseDatas.setContainerHeight3(mProductResponseDatas.getContainerHeight3());
        mExploreResponseDatas.setContainerWidth3(mProductResponseDatas.getContainerWidth3());
        mExploreResponseDatas.setThumbnailUrl4(mProductResponseDatas.getThumbnailUrl4());
        mExploreResponseDatas.setImageUrl4(mProductResponseDatas.getImageUrl4());
        mExploreResponseDatas.setContainerHeight4(mProductResponseDatas.getContainerHeight4());
        mExploreResponseDatas.setContainerWidth4(mProductResponseDatas.getContainerWidth4());
        mExploreResponseDatas.setPostsType("");
        mExploreResponseDatas.setPostedOn(mProductResponseDatas.getPostedOn());
        mExploreResponseDatas.setLikeStatus(mProductResponseDatas.getLikeStatus());
        mExploreResponseDatas.setProductUrl(mProductResponseDatas.getProductUrl());
        mExploreResponseDatas.setDescription(mProductResponseDatas.getDescription());
        mExploreResponseDatas.setNegotiable(mProductResponseDatas.getNegotiable());
        mExploreResponseDatas.setCondition(mProductResponseDatas.getCondition());
        mExploreResponseDatas.setPrice(mProductResponseDatas.getPrice());
        mExploreResponseDatas.setCurrency(mProductResponseDatas.getCurrency());
        mExploreResponseDatas.setProductName(mProductResponseDatas.getProductName());
        mExploreResponseDatas.setTotalComments("");
        mExploreResponseDatas.setCategoryData(mProductResponseDatas.getCategoryData());
        BusProvider.getInstance().post(mExploreResponseDatas);
    }

    /**
     * <h>SetSocialDatas</h>
     * <p>
     *     In this method we used to send the one complete object of a followed product
     *     to the socail screen through screen.
     * </p>
     */
    public void setSocialDatasFromProductDetails(ProductResponseDatas mProductResponseDatas)
    {
        try {
            SocialDatas socialDatas = new SocialDatas();
            socialDatas.setToAddSocialData(true);
            socialDatas.setPostedOn(mProductResponseDatas.getPostedOn());
            socialDatas.setProductName(mProductResponseDatas.getProductName());
            socialDatas.setCategoryData(mProductResponseDatas.getCategoryData());
            socialDatas.setCurrency(mProductResponseDatas.getCurrency());
            socialDatas.setPrice(mProductResponseDatas.getPrice());
            socialDatas.setMemberProfilePicUrl(mProductResponseDatas.getMemberProfilePicUrl());
            socialDatas.setMainUrl(mProductResponseDatas.getMainUrl());
            socialDatas.setLikes(mProductResponseDatas.getLikes());
            socialDatas.setClickCount(mProductResponseDatas.getClickCount());
            socialDatas.setLikeStatus(mProductResponseDatas.getLikeStatus());
            socialDatas.setPostId(mProductResponseDatas.getPostId());
            socialDatas.setMembername(mSessionManager.getUserName());
            BusProvider.getInstance().post(socialDatas);
        }
        catch (Exception e)
        {
            System.out.println(TAG+" "+"social event bus error="+e.getMessage());
        }
    }
}
