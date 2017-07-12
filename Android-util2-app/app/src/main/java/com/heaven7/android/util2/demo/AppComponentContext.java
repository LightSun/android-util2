package com.heaven7.android.util2.demo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.heaven7.android.util2.IWindow;
import com.heaven7.core.util.Toaster;

/**
 * the component context.
 * Created by heaven7 on 2017/3/3.
 */
public interface AppComponentContext {

    /**
     * get the layout id.
     * @return the layout id
     */
     int getLayoutId();

    /**
     * get the toaster.
     * @return  the {@link Toaster}
     */
    IWindow getToaster();

    /**
     * on initialize
     * @param context the context
     * @param savedInstanceState the bundle of save instance
     */
     void onInitialize(Context context, @Nullable Bundle savedInstanceState);

}