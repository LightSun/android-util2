package com.heaven7.android.util2;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * this help class of view.
 * Created by heaven7 on 2016/10/27.
 *
 * @since 1.0.3
 */
public final class ViewUtil {

    /**
     * get the state bar height.
     * @param context the context
     * @return the state bar height.
     * @since 1.0.5
     */
    public static int getStatusHeight(Context context) {
        //com.android.internal.R.dimen.status_bar_height
        int statusHeight = 25; //default is 25
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            //ignore
        }
        return statusHeight;
    }
    /**
     * request focus for target view
     * @param v the target view.
     */
    public static void obtainFocus(View v) {
        if (v != null) {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            v.requestFocusFromTouch();
        }
    }

    /**
     * is the target view visible in local coordinate.
     *
     * @param view the target view
     * @return true if visible.(visible means the view's all side can see).
     */
    public static boolean isVisibleInScreen(View view) {
        if (view == null) {
            throw new NullPointerException();
        }
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return view.getLocalVisibleRect(new Rect(0, 0, dm.widthPixels, dm.heightPixels));
    }
}
