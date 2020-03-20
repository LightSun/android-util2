package com.heaven7.android.util2;

import android.app.Activity;
import android.app.Fragment;
import android.os.Build;

import com.heaven7.core.util.Logger;
import com.heaven7.java.base.util.Throwables;

import java.lang.ref.WeakReference;

/**
 * this class help we handle weak reference object. such as: Activity, fragment, view.
 * Created by heaven7 on 2017/7/12 0012.
 * @since 1.0.1
 */
public class WeakObject<T> {

    private static final String TAG = "WeakObject";
    private final WeakReference<T> mWeakRef;

    public WeakObject(T obj){
        Throwables.checkNull(obj);
        mWeakRef = new WeakReference<T>(obj);
    }

    /**
     * get the raw object.
     * @return the raw object. may be null if is recycled or unexpected state occurs.
     */
    public T get(){
        T t = mWeakRef.get();
        if(t == null){
            return null;
        }
        final String name = t.getClass().getName();
        if(t instanceof Activity){
            final Activity ac = (Activity) t;
            if(ac.isFinishing()){
                Logger.w(TAG,"get","the activity(" + name + ") is finishing.");
                return null;
            }
            if (Build.VERSION.SDK_INT >= 17 && ac.isDestroyed()) {
                Logger.w(TAG, "get","memory leaked ? activity = "
                        + name);
                return null;
            }
            return t;
        }
        if(t instanceof androidx.fragment.app.Fragment){
            final androidx.fragment.app.Fragment frag = (androidx.fragment.app.Fragment) t;
            if(frag.isDetached() || frag.isRemoving()){
                Logger.w(TAG,"get","fragment is detached or removing. fragment = " + name);
                return null;
            }
            return t;
        }
        if( t instanceof Fragment){
            final Fragment frag = (Fragment) t;
            if(frag.isDetached() || frag.isRemoving()){
                Logger.w(TAG,"get","fragment is detached or removing. fragment = " + name);
                return null;
            }
            return t;
        }
       /* if(t instanceof View){
            if(Build.VERSION.SDK_INT >= 19 && !((View) t).isAttachedToWindow()){
                Logger.w(TAG,"get","view is detached. memory leaked?  view = " + name);
                return null;
            }
            return t;
        }*/
        return t;
    }

}
