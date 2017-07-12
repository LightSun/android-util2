package com.heaven7.android.util2.demo.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.heaven7.android.util2.IWindow;
import com.heaven7.android.util2.demo.BaseActivity;
import com.heaven7.android.util2.demo.R;
import com.heaven7.core.util.Toaster;

import butterknife.OnClick;

/**
 * Created by heaven7 on 2017/7/12 0012.
 */

public class ToastTestActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.ac_test_toast;
    }

    @Override
    public void onInitialize(Context context, @Nullable Bundle savedInstanceState) {

    }

    @OnClick(R.id.bt_toast_normal)
    public void onClickNormalToast(View v){
        getToaster().type(IWindow.TYPE_NORMAL).show("your toast message");
    }
    @OnClick(R.id.bt_toast_warn)
    public void onClickWarnToast(View v){
        getToaster().type(IWindow.TYPE_WARN).show("your toast message");
    }
    @OnClick(R.id.bt_toast_error)
    public void onClickErrorToast(View v){
        getToaster().type(IWindow.TYPE_ERROR).show("your toast message");
    }
    @OnClick(R.id.bt_toast_click)
    public void onClickClickToast(View v){
        final View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toaster.show(v.getContext(), "Toast view was clicked.");
            }
        };
        getToaster()
                .type(IWindow.TYPE_WARN)
                .enableClick(true)
                .bindView(new IWindow.IViewBinder() {
                    @Override
                    public void onBind(View view) {
                        view.setOnClickListener(l);
                    }
                })
                .show("your toast message");
    }
}
