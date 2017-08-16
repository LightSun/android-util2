package com.heaven7.android.util2.demo.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;

import com.heaven7.android.component.toast.AppToastComponent;
import com.heaven7.android.util2.demo.BaseActivity;
import com.heaven7.android.util2.demo.R;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.Toaster;

import butterknife.OnClick;

/**
 * Created by heaven7 on 2017/7/12 0012.
 */

public class ToastTestActivity extends BaseActivity implements View.OnKeyListener {

    @Override
    public int getLayoutId() {
        return R.layout.ac_test_toast;
    }

    @Override
    public void onInitialize(Context context, @Nullable Bundle savedInstanceState) {
       getToastWindow().enableClick(true);
        getToastWindow().setOnKeyListener(this);
    }

    @OnClick(R.id.bt_toast_normal)
    public void onClickNormalToast(View v){
        getToastWindow().type(AppToastComponent.TYPE_NORMAL).show("your toast message");
    }
    @OnClick(R.id.bt_toast_warn)
    public void onClickWarnToast(final View v){
        getToastWindow()
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        Toaster.show(v.getContext(), "action end...");
                    }
                })
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        Toaster.show(v.getContext(), "action start...");
                    }
                })
                .type(AppToastComponent.TYPE_WARN)
                .show("your toast BackKeyListenermessage");
    }
    @OnClick(R.id.bt_toast_error)
    public void onClickErrorToast(View v){
        getToastWindow().type(AppToastComponent.TYPE_ERROR).show("your toast message");
    }
    @OnClick(R.id.bt_toast_click)
    public void onClickClickToast(View v){
        final View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toaster.show(v.getContext(), "Toast view was clicked.");
            }
        };
        getToastWindow()
                .type(AppToastComponent.TYPE_WARN)
                .enableClick(true)
                .bindView(new AppToastComponent.IViewBinder() {
                    @Override
                    public void onBind(View view) {
                        view.setOnClickListener(l);
                    }
                })
                .show("your toast message");
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Logger.i("ToastTestActivity","onKey","");
        return false;
    }
}
