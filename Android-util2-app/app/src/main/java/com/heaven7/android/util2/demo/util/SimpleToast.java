package com.heaven7.android.util2.demo.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.heaven7.android.util2.BaseWindow;
import com.heaven7.android.util2.demo.R;
import com.heaven7.core.util.ViewHelper;
import com.heaven7.core.util.viewhelper.action.Getters;

/**
 * Created by heaven7 on 2017/7/12 0012.
 */

public class SimpleToast extends BaseWindow {

    protected SimpleToast(Context context) {
        super(context);
    }

    public static SimpleToast create(Context context) {
        SimpleToast toast = new SimpleToast(context);
        toast.layout(R.layout.common_toast_view, null, new IViewBinder() {
            @Override
            public void onBind(View view) {
                new ViewHelper(view).setText(R.id.tv_toast, "Toast message demo by heaven7");
            }
        });
        return toast;
    }

    private int getColorByType() {
        switch (getType()) {
            case TYPE_DEBUG:
            case TYPE_NORMAL:
                return Color.GREEN;

            case TYPE_WARN:
                return Color.YELLOW;
            case TYPE_ERROR:
                return Color.RED;

            default:
                return Color.GREEN;
        }
    }

    @Override
    public void show(String msg) {
        new ViewHelper(getWindowView())
                .setText(R.id.tv_toast, msg)
                .performViewGetter(R.id.tv_toast, new Getters.GradientDrawableGetter() {
                    @Override
                    public void onGotBackground(GradientDrawable gd, View view, ViewHelper vp) {
                        gd.setColor(getColorByType());
                    }
                });
        show();
    }
}