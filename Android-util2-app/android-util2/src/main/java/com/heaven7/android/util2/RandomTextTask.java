package com.heaven7.android.util2;

import android.os.SystemClock;
import android.widget.EditText;
import android.widget.TextView;

import com.heaven7.core.util.Logger;
import com.heaven7.core.util.MainWorker;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * random text task. can not used for adapter item.
 * @author heaven7
 */
public class RandomTextTask implements Runnable {

    /**
     * Repeat the task indefinitely.
     */
    public static final int INFINITE = -1;

    private static final Random sRandom = new Random();
    private WeakReference<TextView> mWeakView;
    private final int mMin;
    private final int mMax;

    private long mDuration = 800;
    private long mLastTime;
    private long mConsumeTime;
    private int mCount ;
    /** may be cancel or normal end. */
    private volatile boolean mEnded;
    private TaskListener mListener;

    public RandomTextTask() {
        this(10, 100);
    }

    public RandomTextTask(int min, int max) {
        this.mMin = min;
        this.mMax = max;
    }

    public final void setTaskListener(TaskListener l){
        if(mCount > 0){
            throw new IllegalStateException("must set listener before call start() !");
        }
        this.mListener = l;
    }

    public final RandomTextTask setTextView(TextView tv) {
        mWeakView = new WeakReference<TextView>(tv);
        return this;
    }

    public final RandomTextTask setDuration(long duration) {
        mDuration = duration;
        return this;
    }

    public final TaskListener getTaskListener() {
        return mListener;
    }

    public final int getMaxValue(){
        return mMax;
    }

    public final int getMinValue() {
        return mMin;
    }

    public static int random(int min, int max) {
        return min + sRandom.nextInt(max - min);
    }

    public final void start() {
        if(mListener != null){
            mListener.onStart(mWeakView.get());
        }
        reset();
        mLastTime = SystemClock.elapsedRealtime();
        MainWorker.removePreviousAndPostDelay(randomDelayTime(sRandom), this);
    }

    /**
     * reset the task.
     */
    protected void reset() {
        mLastTime = 0;
        mConsumeTime = 0;
        mCount = 0;
        mEnded = false;
    }

    /**
     * cancel the task.
     */
    public final void cancel() {
        cancelInternal(true);
    }

    /**
     *
     * @param callback true to call {@linkplain TaskListener#onCancel(TextView, int)}
     */
    private void cancelInternal(boolean callback) {
        final int count = this.mCount;
        MainWorker.remove(this);
        reset();
        mEnded = true;
        if(callback && mListener != null){
            mListener.onCancel(mWeakView.get(), count);
        }
    }
    /** mark the task end.called by sub class. */
    protected void markEnd(){
        mEnded = true;
    }
    private void callbackOnEnd(int count){
        if(mListener != null){
            mListener.onEnd(mWeakView.get(), count);
        }
    }

    protected int randomDelayTime(Random r) {
        return random(200, 800);
    }

    protected void onSetText(TextView view, int animatedValue, int count) {
        view.setText(String.valueOf(calculateValue(count, animatedValue)));
        if(view instanceof EditText){
            ((EditText) view).setSelection(view.length());
        }
    }
    protected int calculateValue(int count, int animatedValue){
        return animatedValue + sRandom.nextInt(animatedValue);
    }

    @Override
    public void run() {
        final long current = SystemClock.elapsedRealtime();
        final TextView view = mWeakView.get();
        final int count = this.mCount;
        if (view == null) {
            // view is recycled.
            cancelInternal(false);
            callbackOnEnd(count);
            Logger.i("RandomTextTask","run","view is recycled");
            return;
        }
        if(mDuration != INFINITE){
            mConsumeTime += current - mLastTime;
            mLastTime = current;
            //time reach
            if (mConsumeTime >= mDuration){
                cancelInternal(false);
                callbackOnEnd(count);
                return;
            }
        }
        mCount ++ ;
        final int animValue = random(mMin, mMax);
        onSetText(view, animValue, mCount);
        if(mListener != null){
            mListener.onUpdate(view, mCount, animValue);
        }
        if(!mEnded) {
            MainWorker.postDelay(randomDelayTime(sRandom), this);
        }else{
            callbackOnEnd(mCount);
        }
    }

    /**
     * the task listener of {@linkplain RandomTextTask}
     */
    public interface TaskListener{
        void onStart(TextView view);
        void onUpdate(TextView view, int count, int animatedValue);
        void onEnd(TextView view, int count);
        void onCancel(TextView view, int count);
    }

    public static abstract class SimpleTaskListener implements TaskListener{

        @Override
        public void onStart(TextView view) {
             //Logger.i("SimpleOnTaskListener","onStart","");
        }

        @Override
        public void onUpdate(TextView view, int count, int animatedValue) {
             //Logger.i("SimpleOnTaskListener","onUpdate","count = " + count);
        }

        @Override
        public void onEnd(TextView view, int count) {
            //Logger.i("SimpleOnTaskListener","onEnd", "count = " + count);
        }

        @Override
        public void onCancel(TextView view, int count) {
            //Logger.i("SimpleOnTaskListener","onCancel","count = " + count);
        }
    }
}
