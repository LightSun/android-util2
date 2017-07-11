package com.heaven7.android.util2;

import android.graphics.drawable.AnimationDrawable;
import android.support.v4.view.ViewCompat;
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
        ViewCompat.setAlpha(v, 1);
        ViewCompat.setScaleY(v, 1);
        ViewCompat.setScaleX(v, 1);
        ViewCompat.setTranslationY(v, 0);
        ViewCompat.setTranslationX(v, 0);
        ViewCompat.setRotation(v, 0);
        ViewCompat.setRotationY(v, 0);
        ViewCompat.setRotationX(v, 0);
        ViewCompat.setPivotY(v, v.getMeasuredHeight() / 2);
        ViewCompat.setPivotX(v, v.getMeasuredWidth() / 2);
        ViewCompat.animate(v).setInterpolator(null).setStartDelay(0);
    }

    public static int getDurationOfAnimationDrawable(AnimationDrawable ad){
         int duration = 0;
         for(int i =0 , size = ad.getNumberOfFrames() ; i < size ;i++){
             duration += ad.getDuration(i);
         }
        return duration;
    }
}
