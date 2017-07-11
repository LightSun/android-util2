package com.heaven7.android.util2.logic;

import android.util.SparseArray;
import android.util.SparseIntArray;

import com.heaven7.core.util.Logger;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * the logic state. support async and count analyse.
 * default support multi tag in one {@linkplain AbstractLogicAction}.
 * Created by heaven7 on 2017/6/17 0017.
 */
public abstract class AbstractLogicAction extends ContextDataImpl implements LogicAction {

    private final ArrayList<LogicCallback> mCallbacks;

    private final SparseArray<TagInfo> mTagMap;

    /**
     * the map which used to count the tag of state perform count.
     */
    private SparseIntArray mCountMap;

    /**
     * create an instance of AbstractLogicAction.
     * @param wantCount true if you want to COUNT the count of perform assigned tag.
     * @see #perform(int, LogicParam)
     */
    public AbstractLogicAction(boolean wantCount) {
        this.mCallbacks = new ArrayList<>(4);
        this.mTagMap = new SparseArray<>(5);
       //this.mCancelMap = new SparseArray<>(4);//
        if (wantCount) {
            mCountMap = new SparseIntArray(4);
        }
    }

    @Override
    public final void addStateCallback(LogicCallback callback) {
        synchronized (mCallbacks) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public final void removeStateCallback(LogicCallback callback) {
        synchronized (mCallbacks) {
            mCallbacks.remove(callback);
        }
    }

    @Override
    public final LogicParam getLogicParameter(int tag) {
        TagInfo info;
        synchronized (mTagMap) {
            info = mTagMap.get(tag);
        }
        return info != null ? info.mLogicParam : null;
    }

    @Override
    public void perform(int tag, LogicParam param) {
        //put tag info
        synchronized (mTagMap) {
            mTagMap.put(tag, new TagInfo(param));
        }
        //start
        dispatchLogicStart(tag, param);

        //COUNT tag count if need
        final int targetCount ;
        if (mCountMap != null) {
            synchronized (AbstractLogicAction.this) {
                targetCount = mCountMap.get(tag) + 1;
                mCountMap.put(tag, targetCount);
            }
        }else{
            targetCount = 1;
        }
        //perform impl
        performImpl(tag, targetCount,  param);
    }

    @Override
    public final boolean dispatchResult(int resultCode, int tag) {
        //usually callback
        final LogicParam lm = getLogicParameter(tag);
        ArrayList<LogicCallback> callbacks = (ArrayList<LogicCallback>) mCallbacks.clone();
        for (LogicCallback cl : callbacks) {
            cl.onLogicResult(this, resultCode, tag, lm);
        }

        switch (resultCode){
            case RESULT_SUCCESS:
                return onLogicSuccess(tag);

            case RESULT_FAILED:
                return onLogicFailed(tag);
        }
        return dispatchLogicResult(resultCode, tag, lm);
    }

    @Override
    public final void cancel(int tag ,boolean immediately) {
        TagInfo info;
        synchronized (mTagMap) {
            info = mTagMap.get(tag);
        }
        if(info == null){
            return;
        }
        if(!info.mCancelled.compareAndSet(false, true)){
            Logger.w("AbstractLogicAction","cancel","cancel failed. tag = " + tag
                    + " ,param = " + info.mLogicParam);
        }
        cancelImpl(tag, immediately);
    }

    /**
     * clear the count map or analyse .
     */
    public final void clearCount() {
        synchronized (AbstractLogicAction.this) {
            if (mCountMap != null) {
                mCountMap.clear();
            }
        }
    }

    /**
     * remove the count which is assigned by target tag.
     * @param tag the tag.
     */
    public final void removeCount(int tag){
        synchronized (AbstractLogicAction.this) {
            if (mCountMap != null) {
                mCountMap.delete(tag);
            }
        }
    }

    /**
     * called on cancel last perform. often called by {@linkplain #onLogicSuccess(int)}}.
     * @param tag the tag .
     * @param param the logic parameter.
     */
    protected void onCancel(int tag, LogicParam param){

    }

    /**
     * called on logic result.
     * @param resultCode the result code. but not {@linkplain #RESULT_SUCCESS} or  {@linkplain #RESULT_FAILED}.
     * @param tag the tag
     * @param lm the logic parameter
     * @return true if dispatch success.
     */

    protected  boolean dispatchLogicResult(int resultCode, int tag, LogicParam lm){
        return false;
    }

    /**
     * do perform this logic state. also support async perform this logic state.
     *
     * @param tag       the tag of this logic state.
     * @param count     the count of perform this tag by logic. but if it was cleaned it always be one. start from 1
     * @param param     the logic parameter of this logic state
     */
    protected abstract void performImpl(int tag, int count ,LogicParam param);

    /**
     * do cancel this perform logic.
     * @param tag the tag
     * @param immediately true if cancel immediately.
     */
    protected abstract void cancelImpl(int tag, boolean immediately);


    //====================== self method ============================

    protected boolean onLogicFailed(int tag){
        getAndRemoveTagInfo(tag);
        return false;
    }

    /**
     * called on logic success.
     * @param tag the tag
     * @return true .if it is started ,but not cancelled and normal success.
     */
    protected boolean onLogicSuccess(int tag) {
        //get and remove tag info
        TagInfo info = getAndRemoveTagInfo(tag);
        if(info == null){
            return false;
        }

        //true, means it is cancelled.
        if(info.mCancelled.get()){
            onCancel(tag, info.mLogicParam);
            return false;
        }
        return true;
    }

    private TagInfo getAndRemoveTagInfo(int tag) {
        TagInfo info;
        synchronized (mTagMap) {
            info = mTagMap.get(tag);
            mTagMap.remove(tag);
        }
        return info;
    }

    private void dispatchLogicStart(int tag, LogicParam lp) {
        ArrayList<LogicCallback> callbacks = (ArrayList<LogicCallback>) mCallbacks.clone();
        for (LogicCallback cl : callbacks) {
            cl.onLogicStart(this, tag, lp);
        }
    }

    private static class TagInfo{
        final AtomicBoolean mCancelled;
        final LogicParam mLogicParam;

        public TagInfo(LogicParam mLogicParam) {
            this.mCancelled = new AtomicBoolean(false);
            this.mLogicParam = mLogicParam;
        }
    }
}
