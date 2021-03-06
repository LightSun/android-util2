package com.heaven7.android.util2.demo.sample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.heaven7.android.util2.ImageHelper;
import com.heaven7.android.util2.demo.BaseActivity;
import com.heaven7.android.util2.demo.R;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.PermissionHelper;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * https://juejin.im/entry/59314060ac502e006880d9c7
 * Created by Administrator on 2017/9/22 0022.
 */

public class TestGetImageInN extends BaseActivity {

    public static final int RC_WRITE_SD = 2;

    private ImageHelper mImageGetter;
    private PermissionHelper mPermissionHelper = new PermissionHelper(this);

    @BindView(R.id.iv_photo)
    ImageView iv_photo;


    @Override
    public int getLayoutId() {
        return R.layout.ac_test_toast;
    }

    @Override
    public void onInitialize(Context context, @Nullable Bundle savedInstanceState) {
        mImageGetter = new ImageHelper(getExternalFilesDir(
                Environment.DIRECTORY_PICTURES).getAbsolutePath(), TestGetImageInN.this, new ImageCallbackImpl()){
        };
        mPermissionHelper.startRequestPermission(
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new int[]{RC_WRITE_SD},
                new PermissionHelper.ICallback() {
                    @Override
                    public void onRequestPermissionResult(String requestPermission, int requestCode, boolean success) {
                        if (success) {
                            Logger.i("TestGetImageInN","onRequestPermissionResult","success: " + requestPermission);
                            initImageGetter();
                        }
                    }
                    @Override
                    public boolean handlePermissionHadRefused(String s, int i, Runnable runnable) {
                        return false;
                    }
                });

    }

    private void initImageGetter(){

    }

    @OnClick(R.id.bt_toast_normal)
    public void onClickNormalToast(View v){
        mImageGetter.pick();
    }

    @OnClick(R.id.bt_toast_warn)
    public void onClickWarnToast(final View v){
        mImageGetter.camera();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageGetter.onActivityResult(requestCode, resultCode, data);
    }

    private class ImageCallbackImpl extends ImageHelper.ImageCallback {

        @Override
        public void buildZoomIntent(Intent defaultIntent) {
        }

        @Override
        public void onSuccess(final File file, final Bitmap photo) {
            //mIcon.setImageBitmap(photo);
            Logger.i("ImageCallbackImpl","onSuccess","file = " + file + " , photo = " + photo);
            iv_photo.setImageBitmap(photo);
        }
    }
}
