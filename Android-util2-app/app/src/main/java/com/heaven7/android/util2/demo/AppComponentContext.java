package com.heaven7.android.util2.demo;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.heaven7.android.component.toast.AppToastComponent;

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
     * @return  the
     */
    AppToastComponent getToastWindow();

    /**
     * on initialize
     * @param context the context
     * @param savedInstanceState the bundle of save instance
     */
     void onInitialize(Context context, @Nullable Bundle savedInstanceState);

}