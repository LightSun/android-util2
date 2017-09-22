package com.heaven7.android.util2.demo;

import com.heaven7.android.util2.demo.sample.Button3Activity;
import com.heaven7.android.util2.demo.sample.TestGetImageInN;
import com.heaven7.android.util2.demo.sample.TestGuideActivity;
import com.heaven7.android.util2.demo.sample.ToastTestActivity;

import java.util.List;

/**
 * Created by heaven7 on 2017/7/12 0012.
 */

public class MainActivity extends AbsMainActivity {

    @Override
    protected void addDemos(List<ActivityInfo> list) {
        list.add(new ActivityInfo(ToastTestActivity.class, "ToastTestActivity"));
        list.add(new ActivityInfo(TestGuideActivity.class, "TestGuideActivity"));
        list.add(new ActivityInfo(TestGetImageInN.class, "TestGetImageInN"));
        list.add(new ActivityInfo(Button3Activity.class, "Button3Activity"));

    }
}
