package com.heaven7.android.util2;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;

/**
 * Created by heaven7 on 2017/3/31 0031.
 */

public class AnimUtils {


    /**
     * clear the animator of the target view.
     * @param v the target view
     */
    public static void clearAnimator(View v){
        v.setAlpha(1);
        v.setScaleY(1);
        v.setScaleX( 1);
        v.setTranslationY(0);
        v.setTranslationX(0);
        v.setRotation( 0);
        v.setRotationY( 0);
        v.setRotationX( 0);
        v.setPivotY(v.getMeasuredHeight() / 2);
        v.setPivotX(v.getMeasuredWidth() / 2);
        v.animate().setInterpolator(null).setStartDelay(0);
    }

    public static int getDurationOfAnimationDrawable(AnimationDrawable ad){
         int duration = 0;
         for(int i =0 , size = ad.getNumberOfFrames() ; i < size ;i++){
             duration += ad.getDuration(i);
         }
        return duration;
    }
}
