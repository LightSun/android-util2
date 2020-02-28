package com.heaven7.android.util2.demo.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.heaven7.adapter.BaseSelector;
import com.heaven7.adapter.QuickRecycleViewAdapter;
import com.heaven7.android.component.guide.AppGuideComponent;
import com.heaven7.android.component.guide.GuideComponent;
import com.heaven7.android.component.guide.RelativeLocation;
import com.heaven7.android.util2.GuideHelper;
import com.heaven7.android.util2.demo.BaseActivity;
import com.heaven7.android.util2.demo.R;
import com.heaven7.core.util.Logger;
import com.heaven7.core.util.MainWorker;
import com.heaven7.core.util.ViewHelper;
import com.heaven7.java.base.util.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by heaven7 on 2017/4/20 0020.
 */

public class TestGuideActivity extends BaseActivity {

    @BindView(R.id.tb)
    ToggleButton mTb_1;

    @BindView(R.id.nsv)
    NestedScrollView mNsv;

    @BindView(R.id.rv)
    RecyclerView mRv;

    @BindView(R.id.tv_title)
    TextView mTv_title;

    private AppGuideComponent mGH;
    private TextView mTip;
    private byte mIndex = -1;

    @Override
    public int getLayoutId() {
        return R.layout.test_toggle_button;
    }

    @Override
    public void onInitialize(Context context, @Nullable Bundle savedInstanceState) {
        mRv.setLayoutManager(new LinearLayoutManager(context));
        mRv.setNestedScrollingEnabled(false);

        mGH = new GuideHelper(this, getLayoutId());

        ViewGroup tipView = (ViewGroup) getLayoutInflater().inflate(R.layout.test_tip, null);
        mTip = (TextView) tipView.findViewById(R.id.test_tv_tip);
        tipView.removeAllViews();

        populateData(context);
    }

    //模拟实际场景，设置数据
    private void populateData(Context context) {
        mTv_title.setText("第三方空间好几个环节都是开发商的开发大赛的十分关键的是房价肯定是开放的考虑是否快乐的首付款的说法的十分的说法是地方艰苦");
        List<BaseSelector>  list = new ArrayList<>();
        for(int i=0 ; i < 20 ; i++){
            list.add(new BaseSelector());
        }
        mRv.setAdapter(new QuickRecycleViewAdapter<BaseSelector>(
                R.layout.item_test_guide, list) {
            @Override
            protected void onBindData(Context context, int position,
                                      BaseSelector item, int itemLayoutId, ViewHelper helper) {

            }
        });
        mNsv.getParent().requestChildFocus(mNsv, mNsv); //请求焦点。对于scrollview 类的可以自动滑动到顶部
       // mNsv.scrollTo(0, 0);
        showTipDelay();
        /*mNsv.post(new Runnable() {
            @Override
            public void run() {
                if(!mNsv.fullScroll(View.FOCUS_UP)){
                    Logger.w("TestGuideActivity","run","fullScroll UP failed.");
                }else{
                    mNsv.removeCallbacks(this);
                    showTipDelay();
                }
            }
        });*/
    }

    private void showTipDelay(){
        MainWorker.postDelay(5, new Runnable() {
            @Override
            public void run() {
                showTip();
            }
        });
    }

    private void showTip() {
        mIndex ++ ;
        final GuideComponent gc = new GuideComponent.Builder()
                .anchor(mTb_1)
                //.tip(mTip)
                .location(new RelativeLocation(
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
                mGH.show(ArrayUtils.toArray(gc), new AppGuideComponent.GuideCallback() {
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
