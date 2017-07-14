package com.heaven7.android.util2;

import android.app.Activity;
import android.os.Build;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * a class help we handle weak reference of activity.
 * Created by heaven7 on 2017/7/11 0011.
 */

public class WeakActivityOwner<T extends Activity> {

    private final WeakReference<T> mWeakActivity;

    public WeakActivityOwner(T activity){
        mWeakActivity = new WeakReference<T>(activity);
    }

    /**
     * get the activity which is owned. if the activity is finish or destroyed or recycled. it return null.
     * @return the activity, may be null.
     */
    public T getActivity(){
        T activity = mWeakActivity.get();
        if(activity != null) {
            if (activity.isFinishing()) {
                return null;
            }
            if (Build.VERSION.SDK_INT >= 17 && activity.isDestroyed()) {
                Log.w("WeakActivityOwner","getActivity(): memory leaked ? activity = "
                        + activity.getClass().getName());
                return null;
            }
            return activity;
        }
        return null;
    }
}
