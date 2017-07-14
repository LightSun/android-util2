package com.heaven7.android.util2;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * a class help we handle weak reference of context.
 * Created by heaven7 on 2017/7/11 0011.
 * @since 1.0.2
 */
public class WeakContextOwner{

    private final WeakReference<Context> mWeakActivity;

    public WeakContextOwner(Context context){
        mWeakActivity = new WeakReference<Context>(context);
    }

    /**
     * get the activity which is owned. if the activity is finish or destroyed or recycled. it return null.
     * @return the activity, may be null.
     */
    public Context getContext(){
        final Context t = mWeakActivity.get();
        if(t != null && t instanceof Activity) {
            final Activity ac = (Activity) t;
            if (ac.isFinishing()) {
                return null;
            }
            if (Build.VERSION.SDK_INT >= 17 && ac.isDestroyed()) {
                Log.w("WeakActivityOwner","getActivity(): memory leaked ? t = "
                        + t.getClass().getName());
                return null;
            }
            return t;
        }
        return null;
    }

    /**
     * get the context as target type.
     * @param clazz the target type class.
     * @param <T> the context type
     * @return the target type.
     * @throws ClassCastException if the context can't be cast to target type.
     */
    public <T extends Context> T getContextAs(Class<T> clazz) throws ClassCastException{
        return (T) getContext();
    }

    /**
     * get the context as activity.
     * @return the activity
     * @throws ClassCastException if the context can't be cast to Activity.
     */
    public Activity getContextAsActivity() throws ClassCastException{
        return (Activity) getContext();
    }

}
