package com.heaven7.android.util2.demo.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.heaven7.android.util2.GuideHelper;
import com.heaven7.android.util2.demo.BaseActivity;
import com.heaven7.android.util2.demo.R;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.MainWorker;
import com.heaven7.java.base.util.ArrayUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by heaven7 on 2017/4/20 0020.
 */

public class TestGuideActivity extends BaseActivity {

    @BindView(R.id.tb)
    ToggleButton mTb_1;

    private GuideHelper mGH;
    private TextView mTip;
    private byte mIndex = -1;

    @Override
    public int getLayoutId() {
        return R.layout.test_toggle_button;
    }

    @Override
    public void onInitialize(Context context, @Nullable Bundle savedInstanceState) {
        mGH = new GuideHelper(this, getLayoutId());

        ViewGroup tipView = (ViewGroup) getLayoutInflater().inflate(R.layout.test_tip, null);
        mTip = (TextView) tipView.findViewById(R.id.test_tv_tip);
        tipView.removeAllViews();

        showTip();
    }

    private void showTip() {
        mIndex ++ ;
        final GuideHelper.GuideComponent gc = new GuideHelper.GuideComponent.Builder()
                .anchor(mTb_1)
                //.tip(mTip)
                .location(new GuideHelper.RelativeLocation(
                        (byte) ((mIndex % 4) + 1), 40,
                        GuideHelper.RELATIVE_ANCHOR, 0.5f))
                .build();

        mTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.i("TestUiActivity","onClick",">>> mTip");
            }
        });
        MainWorker.postDelay(20, new Runnable() {
            @Override
            public void run() {
                mGH.show(ArrayUtils.toArray(gc), new GuideHelper.GuideCallback() {
                    @Override
                    public boolean handleClickRoot(View root) {
                        Logger.i("TestUiActivity","handleClickRoot","");
                        return super.handleClickRoot(root);
                    }
                    @Override
                    public boolean handleClickTip(View tip) {
                        Logger.i("TestUiActivity","handleClickTip","");
                        return super.handleClickTip(tip);
                    }
                    @Override
                    public boolean handleClickAnchor(View copyOfAnchor) {
                        Logger.i("TestUiActivity","handleClickAnchor","");
                        return super.handleClickAnchor(copyOfAnchor);
                    }
                    @Override
                    public void onShow() {
                        Logger.i("TestUiActivity","onShow","");
                        super.onShow();
                    }
                    @Override
                    public void onDismiss() {
                        Logger.i("TestUiActivity","onDismiss","");
                        super.onDismiss();
                    }
                });
            }
        });
    }

    @OnClick(R.id.tb)
    public void onClickShowToast(View v){
        Logger.i("TestUiActivity","onClickShowToast","tb");
        getToastWindow().show("dsfjdsfjdsfjdsk");
    }
    @OnClick(R.id.sc)
    public void onClickSwitchCompat(View v){
        showTip();
    }
}
