package com.heaven7.android.util2;

import android.animation.Animator;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.heaven7.adapter.AdapterManager;

import java.util.List;

/**
 * the animator adapter
 *
 * @author heaven7
 */
public abstract class AnimatorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final RecyclerView.Adapter<RecyclerView.ViewHolder> mAdapter;
    private int mDuration = 1500;
    /**
     * the interpolator
     */
    private Interpolator mInterpolator = new LinearInterpolator();
    /**
     * the last position of anim
     */
    private int mLastPosition = -1;
    /**
     * the anim count
     */
    private int mAnimCount = 2;
    /**
     * if animate every time (eg: whenever adapter crud.)
     */
    private boolean mAnimEveryTime = false;
    /**
     * assign the target position which will be only animate .
     */
    private int[] mAnimPositions;
    /**
     * cancel the animate or not
     */
    private boolean mCanceled;
    /**
     * the animate callback.
     */
    private AnimateCallback mCallback;

    private long mSartDelay = 300;

    public AnimatorAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        mAdapter = adapter;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        mAdapter.registerAdapterDataObserver(observer);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver observer) {
        super.unregisterAdapterDataObserver(observer);
        mAdapter.unregisterAdapterDataObserver(observer);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        mAdapter.onBindViewHolder(holder, position);
        if(mAdapter instanceof AdapterManager.IHeaderFooterManager){
            AdapterManager.IHeaderFooterManager fm = (AdapterManager.IHeaderFooterManager) this.mAdapter;
            if(fm.isFooter(position) || fm.isHeader(position)) {
                return;
            }
        }

        //every item have animator
        final int adapterPosition = holder.getAdapterPosition();
        if (!mCanceled && shouldAnimate(adapterPosition)) {
            final int animCount = mAnimCount;
            final Animator[] animators = getAnimators(holder.itemView);
            if (animators == null || animators.length == 0) {
                //no animator.
                return;
            }
           // Logger.i("AnimatorAdapter","onBindViewHolder","animators.size = " + animators.length);
            for (final Animator anim : animators) {
                anim.setDuration(mDuration);
                anim.setInterpolator(mInterpolator);
                anim.setStartDelay(mSartDelay);
                new AnimateHelper(holder.itemView, adapterPosition, anim, animCount).start();
            }
            mLastPosition = adapterPosition;
        } else {
            onClearAnimator(holder.itemView);
        }
    }


    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        mAdapter.onViewRecycled(holder);
        // AnimUtils.clearAnimator(holder.itemView);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mAdapter.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    //==============================================================
    public void startAnimation(int position, int duration, int count, AnimateCallback cl) {
        startAnimation(new int[]{position}, duration, count, cl);
    }

    public void startAnimation(List<Integer> positions, int duration, int count, AnimateCallback cl){
        final int size = positions.size();
        int[] arr = new int[size];
        for (int i = 0 ; i < size ; i++){
            arr[i] = positions.get(i);
        }
        startAnimation(arr, duration, count, cl);
    }
    /***
     * only positions and count can clear
     */
    public void startAnimation(int[] positions, int duration, int count, AnimateCallback cl) {
        if (positions == null) {
            throw new NullPointerException();
        }
        this.setCanceled(false);
        this.mAnimPositions = positions;
        this.setDuration(duration);
        this.setAnimateCount(count);
        this.mCallback = cl;
        for (int pos : positions) {
            notifyItemChanged(pos);
        }
    }

    //==============================================================

    public RecyclerView.Adapter<RecyclerView.ViewHolder> getWrappedAdapter() {
        return mAdapter;
    }

    public void setStartDelay(long delay){
        if(delay < 0){
            delay = 0;
        }
        this.mSartDelay = delay;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public void setStartPosition(int start) {
        mLastPosition = start;
    }

    public void setAnimateCount(int count) {
        this.mAnimCount = count;
    }

    public void setAnimateEveryTime(boolean mAnimEveryTime) {
        this.mAnimEveryTime = mAnimEveryTime;
    }

    /**
     * this will effect the next notify.
     */
    public void setCanceled(boolean mCanceled) {
        this.mCanceled = mCanceled;
    }
    //================================================================

    protected boolean shouldAnimate(int adapterPosition) {
        if (mAnimPositions != null && mAnimPositions.length > 0) {
            for (int pos : mAnimPositions) {
                if (pos == adapterPosition) {
                    return true;
                }
            }
            return false;
        }
        return mAnimEveryTime || adapterPosition > mLastPosition;
    }


    protected abstract Animator[] getAnimators(View itemView);

    /**
     * called on clear animator
     *
     * @param itemView the item view of item
     */
    protected void onClearAnimator(View itemView) {
        AnimUtils.clearAnimator(itemView);
    }

    protected void onAnimateEnd(View itemView, int position) {

    }

    /**
     * the animate helper
     */
    private class AnimateHelper extends AnimatorListenerAdapter {
        final View mItemView;
        final int mPositon;
        final Animator anim;
        int count;

        public AnimateHelper(View itemView, int adapterPos, Animator anim, int count) {
            this.mItemView = itemView;
            this.mPositon = adapterPos;
            this.anim = anim;
            this.count = count;
            this.anim.addListener(this);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            start();
        }

        public void start() {
            //Logger.i("AnimateHelper","start","count = " + count + ", hash = " + this.hashCode());
            if (count <= 0) {
                setCanceled(true);
                onAnimateEnd(mItemView, mPositon);
                if (mCallback != null) {
                    mCallback.onAnimateEnd(mItemView, mPositon);
                }
                return; //end
            }
            //start once now
           /* Logger.d("AnimateHelper","start_before_start","count = " + count +
                    ",already started ?=" + anim.isStarted());*/
            --count;
            anim.start();
        }
    }

    public interface AnimateCallback {
        void onAnimateEnd(View itemView, int position);
    }

}