package com.demskigroup.guitaramps.utility;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * <h>CustomBottomNavigationView</h>
 * <p>
 *     In this class we used to set our custom things with bottom navigation view
 *     like to set icon size, text, custom font with text etc.
 * </p>
 * @since 2017/11/10
 */
public class CustomBottomNavigationView extends BottomNavigationView
{
    // used for animation
    private int mShiftAmount;
    private float mScaleUpFactor;
    private float mScaleDownFactor;
    private boolean animationRecord;
    private float mLargeLabelSize;
    private float mSmallLabelSize;

    // used for setupWithViewPager
    private MyOnNavigationItemSelectedListener mMyOnNavigationItemSelectedListener;
    private BottomNavigationMenuView mMenuView;
    private BottomNavigationItemView[] mButtons;
    // used for setupWithViewPager end

    public CustomBottomNavigationView(Context context) {
        super(context);
//        init();
    }

    public CustomBottomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        init();
    }

    public CustomBottomNavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        init();
    }

    /**
     * enable or disable click item animation(text scale and icon move animation in no item shifting mode)
     *
     * @param enable It means the text won't scale and icon won't move when active it in no item shifting mode if false.
     */
    public void enableAnimation(boolean enable) {
        /*
        1. get field in this class
        private final BottomNavigationMenuView mMenuView;

        2. get field in mButtons
        private BottomNavigationItemView[] mButtons;

        3. chang mShiftAmount to 0 in mButtons
        private final int mShiftAmount

        change mScaleUpFactor and mScaleDownFactor to 1f in mButtons
        private final float mScaleUpFactor
        private final float mScaleDownFactor

        4. change label font size in mButtons
        private final TextView mLargeLabel
        private final TextView mSmallLabel
         */

        // 1. get mMenuView
        BottomNavigationMenuView mMenuView = getBottomNavigationMenuView();
        // 2. get mButtons
        BottomNavigationItemView[] mButtons = getBottomNavigationItemViews();
        // 3. change field mShiftingMode value in mButtons
        for (BottomNavigationItemView button : mButtons) {
            TextView mLargeLabel = getField(button.getClass(), button, "mLargeLabel");
            TextView mSmallLabel = getField(button.getClass(), button, "mSmallLabel");

            // if disable animation, need animationRecord the source value
            if (!enable) {
                if (!animationRecord) {
                    animationRecord = true;
                    mShiftAmount = getField(button.getClass(), button, "mShiftAmount");
                    mScaleUpFactor = getField(button.getClass(), button, "mScaleUpFactor");
                    mScaleDownFactor = getField(button.getClass(), button, "mScaleDownFactor");

                    mLargeLabelSize = mLargeLabel.getTextSize();
                    mSmallLabelSize = mSmallLabel.getTextSize();

//                    System.out.println("mShiftAmount:" + mShiftAmount + " mScaleUpFactor:"
//                            + mScaleUpFactor + " mScaleDownFactor:" + mScaleDownFactor
//                            + " mLargeLabel:" + mLargeLabelSize + " mSmallLabel:" + mSmallLabelSize);
                }
                // disable
                setField(button.getClass(), button, "mShiftAmount", 0);
                setField(button.getClass(), button, "mScaleUpFactor", 1);
                setField(button.getClass(), button, "mScaleDownFactor", 1);

                // let the mLargeLabel font size equal to mSmallLabel
                mLargeLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSmallLabelSize);

                // debug start
//                mLargeLabelSize = mLargeLabel.getTextSize();
//                System.out.println("mLargeLabel:" + mLargeLabelSize);
                // debug end

            } else {
                // haven't change the value. It means it was the first call this method. So nothing need to do.
                if (!animationRecord)
                    return;
                // enable animation
                setField(button.getClass(), button, "mShiftAmount", mShiftAmount);
                setField(button.getClass(), button, "mScaleUpFactor", mScaleUpFactor);
                setField(button.getClass(), button, "mScaleDownFactor", mScaleDownFactor);
                // restore
                mLargeLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLargeLabelSize);
            }
        }
        mMenuView.updateMenuView();
    }

    /**
     * enable the shifting mode for navigation
     *
     * @param enable It will has a shift animation if true. Otherwise all items are the same width.
     */
    public void enableShiftingMode(boolean enable) {
        /*
        1. get field in this class
        private final BottomNavigationMenuView mMenuView;

        2. change field mShiftingMode value in mMenuView
        private boolean mShiftingMode = true;
         */
        // 1. get mMenuView
        BottomNavigationMenuView mMenuView = getBottomNavigationMenuView();
        // 2. change field mShiftingMode value in mMenuView
        setField(mMenuView.getClass(), mMenuView, "mShiftingMode", enable);

        mMenuView.updateMenuView();
    }

    /**
     * enable the shifting mode for each item
     *
     * @param enable It will has a shift animation for item if true. Otherwise the item text always be shown.
     */
    public void enableItemShiftingMode(boolean enable) {
        /*
        1. get field in this class
        private final BottomNavigationMenuView mMenuView;

        2. get field in this mMenuView
        private BottomNavigationItemView[] mButtons;

        3. change field mShiftingMode value in mButtons
        private boolean mShiftingMode = true;
         */
        // 1. get mMenuView
        BottomNavigationMenuView mMenuView = getBottomNavigationMenuView();
        // 2. get mButtons
        BottomNavigationItemView[] mButtons = getBottomNavigationItemViews();
        // 3. change field mShiftingMode value in mButtons
        for (BottomNavigationItemView button : mButtons) {
            setField(button.getClass(), button, "mShiftingMode", enable);
        }
        mMenuView.updateMenuView();
    }

    /**
     * get menu item position in menu
     *
     * @param item the Menu item
     * @return position if success, -1 otherwise
     */
    public int getMenuItemPosition(MenuItem item) {
        // get item id
        int itemId = item.getItemId();
        // get meunu
        Menu menu = getMenu();
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            if (menu.getItem(i).getItemId() == itemId) {
                return i;
            }
        }
        return -1;
    }

    /**
     * set the current checked item
     *
     * @param item start from 0.
     */
    public void setCurrentItem(int item) {
        // check bounds
        if (item < 0 || item >= getMaxItemCount()) {
            throw new ArrayIndexOutOfBoundsException("item is out of bounds, we expected 0 - "
                    + (getMaxItemCount() - 1) + ". Actually " + item);
        }

        /*
        1. get field in this class
        private final BottomNavigationMenuView mMenuView;

        2. get field in mMenuView
        private BottomNavigationItemView[] mButtons;
        private final OnClickListener mOnClickListener;

        3. call mOnClickListener.onClick();
         */
        // 1. get mMenuView
        BottomNavigationMenuView mMenuView = getBottomNavigationMenuView();
        // 2. get mButtons
        BottomNavigationItemView[] mButtons = getBottomNavigationItemViews();
        // get mOnClickListener
        View.OnClickListener mOnClickListener = getField(mMenuView.getClass(), mMenuView, "mOnClickListener");

//        System.out.println("mMenuView:" + mMenuView + " mButtons:" + mButtons + " mOnClickListener" + mOnClickListener);
        // 3. call mOnClickListener.onClick();
        mOnClickListener.onClick(mButtons[item]);

    }

    @Override
    public void setOnNavigationItemSelectedListener(@Nullable OnNavigationItemSelectedListener listener) {
        // if not set up with view pager, the same with father
        if (null == mMyOnNavigationItemSelectedListener) {
            super.setOnNavigationItemSelectedListener(listener);
            return;
        }

        mMyOnNavigationItemSelectedListener.setOnNavigationItemSelectedListener(listener);
    }

    /**
     * get private mMenuView
     *
     * @return returs the menu Bottom View
     */
    private BottomNavigationMenuView getBottomNavigationMenuView() {
        if (null == mMenuView)
            mMenuView = getField(BottomNavigationView.class, this, "mMenuView");
        return mMenuView;
    }

    /**
     * get private mButtons in mMenuView
     * @return the BottomNavigationItemView var
     */
    public BottomNavigationItemView[] getBottomNavigationItemViews() {
        if (null != mButtons)
            return mButtons;
        /*
         * 1 private final BottomNavigationMenuView mMenuView;
         * 2 private BottomNavigationItemView[] mButtons;
         */
        BottomNavigationMenuView mMenuView = getBottomNavigationMenuView();
        mButtons = getField(mMenuView.getClass(), mMenuView, "mButtons");
        return mButtons;
    }

    /**
     * get private mButton in mMenuView at position
     * @param position the position of menu item
     * @return the BottomNavigationItemView position
     */
    public BottomNavigationItemView getBottomNavigationItemView(int position) {
        return getBottomNavigationItemViews()[position];
    }

    /**
     * get icon at position
     * @param position of icon
     * @return The fiekld
     */
    public ImageView getIconAt(int position) {
        /*
         * 1 private final BottomNavigationMenuView mMenuView;
         * 2 private BottomNavigationItemView[] mButtons;
         * 3 private ImageView mIcon;
         */
        BottomNavigationItemView mButtons = getBottomNavigationItemView(position);
        return getField(BottomNavigationItemView.class, mButtons, "mIcon");
    }

    /**
     * get small label at position
     * Each item has tow label, one is large, another is small.
     *
     * @param position of small label
     * @return the small label
     */
    public TextView getSmallLabelAt(int position) {
        /*
         * 1 private final BottomNavigationMenuView mMenuView;
         * 2 private BottomNavigationItemView[] mButtons;
         * 3 private final TextView mSmallLabel;
         */
        BottomNavigationItemView mButtons = getBottomNavigationItemView(position);
        return getField(BottomNavigationItemView.class, mButtons, "mSmallLabel");
    }

    /**
     * get large label at position
     * Each item has tow label, one is large, another is small.
     * @param position of large label
     * @return the textview
     */
    public TextView getLargeLabelAt(int position) {
        /*
         * 1 private final BottomNavigationMenuView mMenuView;
         * 2 private BottomNavigationItemView[] mButtons;
         * 3 private final TextView mLargeLabel;
         */
        BottomNavigationItemView mButtons = getBottomNavigationItemView(position);
        return getField(BottomNavigationItemView.class, mButtons, "mLargeLabel");
    }

    /**
     * return item count
     * @return the menu item size
     */
    public int getItemCount() {
        BottomNavigationItemView[] bottomNavigationItemViews = getBottomNavigationItemViews();
        if (null == bottomNavigationItemViews)
            return 0;
        return bottomNavigationItemViews.length;
    }

    /**
     * set all item small TextView size
     * Each item has tow label, one is large, another is small.
     * Small one will be shown when item state is normal
     * Large one will be shown when item checked.
     * @param sp the text size
     */
    public void setSmallTextSize(float sp) {
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            getSmallLabelAt(i).setTextSize(sp);
        }
        mMenuView.updateMenuView();
    }

    /**
     * set all item large TextView size
     * Each item has tow label, one is large, another is small.
     * Small one will be shown when item state is normal.
     * Large one will be shown when item checked.
     *
     * @param sp the large text size
     */
    public void setLargeTextSize(float sp) {
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            getLargeLabelAt(i).setTextSize(sp);
        }
        mMenuView.updateMenuView();
    }

    /**
     * set all item large and small TextView size
     * Each item has tow label, one is large, another is small.
     * Small one will be shown when item state is normal
     * Large one will be shown when item checked.
     *
     * @param sp the text size
     */
    public void setTextSize(float sp) {
        setLargeTextSize(sp);
        setSmallTextSize(sp);
    }

    /**
     * set item ImageView size which at position
     *
     * @param position position start from 0
     * @param width    in dp
     * @param height   in dp
     */
    public void setIconSizeAt(int position, float width, float height) {
        ImageView icon = getIconAt(position);
        // update size
        ViewGroup.LayoutParams layoutParams = icon.getLayoutParams();
        layoutParams.width = dp2px(getContext(), width);
        layoutParams.height = dp2px(getContext(), height);
        icon.setLayoutParams(layoutParams);

        mMenuView.updateMenuView();
    }

    /**
     * set all item ImageView size
     *
     * @param width  in dp
     * @param height in dp
     */
    public void setIconSize(float width, float height) {
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            setIconSizeAt(i, width, height);
        }
    }

    /**
     * dp to px
     *
     * @param context the current context
     * @param dpValue dp
     * @return px
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * set Typeface for all item TextView
     */
    public void setTypeface(Typeface typeface) {
        int count = getItemCount();
        for (int i = 0; i < count; i++) {
            getLargeLabelAt(i).setTypeface(typeface);
            getSmallLabelAt(i).setTypeface(typeface);
        }
        mMenuView.updateMenuView();
    }

    /**
     * get private filed in this specific object
     *
     * @param targetClass the target class
     * @param instance    the filed owner
     * @param fieldName the field name
     * @param <T> the type
     * @return field if success, null otherwise.
     */
    private <T> T getField(Class targetClass, Object instance, String fieldName) {
        try {
            Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(instance);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * change the field value
     *
     * @param targetClass the target class
     * @param instance    the filed owner
     * @param fieldName the field name
     */
    private void setField(Class targetClass, Object instance, String fieldName, Object value) {
        try {
            Field field = targetClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, value);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Decorate OnNavigationItemSelectedListener for setupWithViewPager
     */
    private static class MyOnNavigationItemSelectedListener implements OnNavigationItemSelectedListener {
        private OnNavigationItemSelectedListener listener;
        private final WeakReference<ViewPager> viewPagerRef;
        private boolean smoothScroll;
        private SparseIntArray items;// used for change ViewPager selected item
        private int previousPosition = -1;


        MyOnNavigationItemSelectedListener(ViewPager viewPager, CustomBottomNavigationView bnve, boolean smoothScroll, OnNavigationItemSelectedListener listener) {
            this.viewPagerRef = new WeakReference<>(viewPager);
            this.listener = listener;
            this.smoothScroll = smoothScroll;

            // create items
            Menu menu = bnve.getMenu();
            int size = menu.size();
            items = new SparseIntArray(size);
            for (int i = 0; i < size; i++) {
                int itemId = menu.getItem(i).getItemId();
                items.put(itemId, i);
            }
        }

        void setOnNavigationItemSelectedListener(OnNavigationItemSelectedListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int position = items.get(item.getItemId());
            // only set item when item changed
            if (previousPosition == position) {
                return true;
            }

            // user listener
            if (null != listener) {
                boolean bool = listener.onNavigationItemSelected(item);
                // if the selected is invalid, no need change the view pager
                if (!bool)
                    return false;
            }

            // change view pager
            ViewPager viewPager = viewPagerRef.get();
            if (null == viewPager)
                return false;

            viewPager.setCurrentItem(items.get(item.getItemId()), smoothScroll);

            // update previous position
            previousPosition = position;

            return true;
        }
    }
}
