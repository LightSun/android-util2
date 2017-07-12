package com.heaven7.android.util2.demo.util;

import android.content.Context;

/**
 * Created by heaven7 on 2017/7/12 0012.
 */

public class SystemInfo {

    private static int sStatusBarHeight;

    public static int getStatusBarHeight(Context context) {
        if (sStatusBarHeight > 0) {
            return sStatusBarHeight;
        }
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            sStatusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }else{
            sStatusBarHeight = 25;
        }
        return sStatusBarHeight;
    }
}
