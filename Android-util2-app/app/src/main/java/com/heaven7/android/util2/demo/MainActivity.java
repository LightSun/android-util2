package com.heaven7.android.util2.demo;

import com.heaven7.android.util2.demo.sample.ToastTestActivity;

import java.util.List;

/**
 * Created by heaven7 on 2017/7/12 0012.
 */

public class MainActivity extends AbsMainActivity {

    @Override
    protected void addDemos(List<ActivityInfo> list) {
        list.add(new ActivityInfo(ToastTestActivity.class, "ToastTestActivity"));

    }
}
