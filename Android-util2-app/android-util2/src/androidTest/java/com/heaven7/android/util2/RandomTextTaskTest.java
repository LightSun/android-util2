package com.heaven7.android.util2;

import android.widget.TextView;

import java.util.Random;

/**
 * random text task test
 * @author heaven7
 */
public class RandomTextTaskTest {

    void test1(){
        final RandomTextTask mTask = new RandomTextTask(){
            @Override
            protected void onSetText(TextView view, int animatedValue, int count) {
                view.setText(count + "");
                if(count >= 90){
                    markEnd();
                }
            }
            @Override
            protected int randomDelayTime(Random r) {
                return random(20,80);
            }
        };

        mTask.setTaskListener(new RandomTextTask.SimpleTaskListener(){});
       // mTask.setTextView(mTv_score).setDuration(RandomTextTask.INFINITE).start();
    }
}
