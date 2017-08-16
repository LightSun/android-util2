package com.heaven7.android.util2.demo;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.heaven7.android.util2.demo.util.SimpleToast;
import com.heaven7.android.util2.demo.util.SystemInfo;

import butterknife.ButterKnife;

/**
 * the base activity
 * Created by heaven7 on 2017/3/3.
 */

public abstract class BaseActivity extends AppCompatActivity implements AppComponentContext{

    private SimpleToast mToast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onPreSetContentView();

        setContentView(getLayoutId());
        ButterKnife.bind(this);
        onInitialize(this, savedInstanceState);
        //沉浸式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            int height = SystemInfo.getStatusBarHeight(this);
            ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
            if(height > 0 && contentView != null) {
                View statusBarView = new View(this);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, height);
                statusBarView.setBackgroundResource(R.drawable.shape_status_bar_color);
                contentView.addView(statusBarView, lp);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public final IntentExecutor getIntentExecutor() {
        return new IntentExecutor();
    }

    /**
     * often used for request flags such as fullscreen.
     */
    protected void onPreSetContentView() {

    }

    public final SimpleToast getToastWindow(){
        if(mToast == null){
            mToast = SimpleToast.create(this);
        }
        return mToast;
    }

    //========================= end impl ===================================

    public class IntentExecutor {

        public void launchActivity(Class<? extends Activity> clazz, int intentFlags) {
            startActivity(new Intent(BaseActivity.this, clazz).addFlags(intentFlags));
        }

        public void launchActivity(Class<? extends Activity> clazz) {
            startActivity(new Intent(BaseActivity.this, clazz));
        }

        public void launchActivity(Class<? extends Activity> clazz, Bundle data) {
            launchActivity(clazz, data, 0);
        }

        public void launchActivity(Class<? extends Activity> clazz, Bundle data, int intentFlags) {
            startActivity(new Intent(BaseActivity.this, clazz).putExtras(data).addFlags(intentFlags));
        }

        public void launchActivityForResult(Class<? extends Activity> clazz, int requestCode) {
            startActivityForResult(new Intent(BaseActivity.this, clazz), requestCode);
        }

        public void launchActivityForResult(Class<? extends Activity> clazz, Bundle data, int requestCode) {
            startActivityForResult(new Intent(BaseActivity.this, clazz)
                    .putExtras(data), requestCode);
        }

        public void launchService(Class<? extends Service> clazz, Bundle data) {
            startService(new Intent(BaseActivity.this, clazz).putExtras(data));
        }

    }


}
