package com.heaven7.android.util2;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.view.View;

import com.heaven7.core.util.Logger;
import com.heaven7.java.base.util.SmartReference;

/**
 * the SmartReference of android platform.
 * Created by heaven7.
 * @since 1.0.3
 */

public class AndroidSmartReference<T> extends SmartReference<T> {

    private static final String TAG = "ASmartRef";

    /**
     * create the smart reference for target object.
     *
     * @param t the object to reference.
     */
    public AndroidSmartReference(T t) {
        super(t);
    }

    @Override
    protected boolean shouldWeakReference(T t) {
        return t instanceof Context || t instanceof View
                || t instanceof Fragment
                || t instanceof android.support.v4.app.Fragment;
    }

    @Override
    protected boolean shouldDestroyReference(T t) {
        final String name = t.getClass().getName();
        if(t instanceof Activity){
            final Activity ac = (Activity) t;
            if(ac.isFinishing()){
                Logger.w(TAG,"shouldDestroyReference","the activity(" + name + ") is finishing.");
                return true;
            }
            if (Build.VERSION.SDK_INT >= 17 && ac.isDestroyed()) {
                Logger.w(TAG, "shouldDestroyReference","memory leaked ? activity = "
                        + name);
                return true;
            }
        }
        if(t instanceof android.support.v4.app.Fragment){
            final android.support.v4.app.Fragment frag = (android.support.v4.app.Fragment) t;
            if(frag.isDetached() || frag.isRemoving()){
                Logger.w(TAG,"shouldDestroyReference","fragment is detached or removing. fragment = " + name);
                return true;
            }
        }
        if( t instanceof Fragment){
            final Fragment frag = (Fragment) t;
            if(frag.isDetached() || frag.isRemoving()){
                Logger.w(TAG,"shouldDestroyReference","fragment is detached or removing. fragment = " + name);
                return true;
            }
        }
        return false;
    }
}
