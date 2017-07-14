package com.heaven7.android.util2;

import android.animation.Animator;

/**
 * the animator listener adapter.
 * use {@linkplain android.animation.AnimatorListenerAdapter} instead.
 * this will be removed in future.
 * @author heaven7
 */
@Deprecated
public abstract class AnimatorListenerAdapter implements Animator.AnimatorListener {

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}