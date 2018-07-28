/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.demskigroup.guitaramps.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.TextureView;
import android.view.WindowManager;

/**
 * A {@link TextureView} that can be adjusted to a specified aspect ratio.
 */
public class AutoFitTextureView extends TextureView {

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;
    private int correctWidth=-1;
    private int correctHeight=-1;
    public DisplayMetrics mMetrics = new DisplayMetrics();
    public AutoFitTextureView(Context context) {
        this(context, null);
    }
    public AutoFitTextureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the ratio
     * calculated from the parameters. Note that the actual sizes of parameters don't matter, that
     * is, calling setAspectRatio(2, 3) and setAspectRatio(4, 6) make the same result.
     *
     * @param width  Relative horizontal size
     * @param height Relative vertical size
     */
    public void setAspectRatio(int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException("Size cannot be negative.");
        }
        mRatioWidth = width;
        mRatioHeight = height;
        requestLayout();
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = MeasureSpec.getSize(widthMeasureSpec);
//        int height = MeasureSpec.getSize(heightMeasureSpec);
//        if (0 == mRatioWidth || 0 == mRatioHeight) {
//            setMeasuredDimension(width, height);
//        } else {
//            if (width < height * mRatioWidth / mRatioHeight) {
//                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth);
//            } else {
//                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height);
//            }
//        }
//    }
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int width = MeasureSpec.getSize(widthMeasureSpec);
    int height = MeasureSpec.getSize(heightMeasureSpec);



    Log.d("log91",width+" "+height);


    if (0 == mRatioWidth || 0 == mRatioHeight) {
        setMeasuredDimension(width, height);
    } else {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(mMetrics);
        double ratio = (double)mRatioWidth / (double)mRatioHeight;
        double invertedRatio = (double)mRatioHeight / (double)mRatioWidth;
        double portraitHeight = width * invertedRatio;
        double portraitWidth = width * (mMetrics.heightPixels / portraitHeight);
        double landscapeWidth = height * ratio;
        double landscapeHeight = (mMetrics.widthPixels / landscapeWidth) * height;

        if (width < height * mRatioWidth / mRatioHeight) {
            setMeasuredDimension((int)portraitWidth, mMetrics.heightPixels);

correctWidth=(int)portraitWidth;

            correctHeight=mMetrics.heightPixels;
            Log.d("log92",(int)portraitWidth+" "+ mMetrics.heightPixels);

        } else {


            if(correctWidth!=-1&&correctHeight!=-1){
                Log.d("log94","94");
            setMeasuredDimension(correctWidth, correctHeight);}

            else{
                Log.d("log95","95");
                setMeasuredDimension(mMetrics.widthPixels, (int)landscapeHeight);

            }

            Log.d("log93",mMetrics.widthPixels+" "+ (int)landscapeHeight);
        }
    }
}
}
