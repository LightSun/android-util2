package com.heaven7.android.util2.logic;

import android.util.SparseArray;

import com.heaven7.core.util.MainWorker;

/**
 * Created by heaven7 on 2017/6/19 0019.
 */

public abstract class DelayLogicAction extends AbstractLogicAction {

    private final SparseArray<Runnable> mDelayMap;
    private final long mDelayTime;

    public DelayLogicAction(boolean wantCount, long delayTime) {
        super(wantCount);
        this.mDelayMap = new SparseArray<>(3);
        this.mDelayTime = delayTime;
    }

    @Override
    public void perform(final int tag, final LogicParam param) {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                mDelayMap.remove(tag);
                callSuperPerform(tag, param);
            }
        };
        mDelayMap.put(tag, r);
        MainWorker.postDelay(mDelayTime, r);
    }

    protected void callSuperPerform(int tag, LogicParam param){
        super.perform(tag, param);
    }

    @Override
    protected void cancelImpl(int tag, boolean immediately) {
        final Runnable r = mDelayMap.get(tag);
        if(r != null) {
            MainWorker.remove(r);
            mDelayMap.remove(tag);
        }
    }
}
