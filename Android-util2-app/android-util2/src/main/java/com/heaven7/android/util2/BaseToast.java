package com.heaven7.android.util2;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.heaven7.core.util.MainWorker;
import com.heaven7.java.base.util.Throwables;

/**
 * the common toast of class100.
 * Created by heaven7 on 2017/5/8 0008.
 */

public abstract class BaseToast implements IToast{

    private static final int TOAST_SHOW_LENGTH = 2000;

    private final WindowManager mWM;
    private final Context mContext;

    private final Runnable mCancelRun = new Runnable() {
        @Override
        public void run() {
            cancel();
            if(mEnd != null){
                mEnd.run();
                mEnd = null;
            }
        }
    };
    private boolean mShowing;
    private Runnable mStart;
    private Runnable mEnd;
    private ToastConfig mDefaultConfig;

    private final ToastConfig mUsingConfig;
    private View mToastView;

    protected BaseToast(Context context) {
        this.mContext = context;
        mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mUsingConfig = new ToastConfig();
        mUsingConfig.wlp = createDefault(context);
        mUsingConfig.type = TYPE_NORMAL;
    }

    @Override
    public abstract void show(String msg);

    @Override
    public abstract void show(int resId);

    //======================================================//

    public int getType(){
        return mUsingConfig.type;
    }
    public View getToastView(){
        return mToastView;
    }

    @Override
    public IToast setDefaultToastConfig(ToastConfig config) {
        Throwables.checkNull(config);
        this.mDefaultConfig = config;
        return reset();
    }

    @Override
    public IToast reset() {
        this.mUsingConfig.wlp = mDefaultConfig.wlp;
        this.mUsingConfig.type = mDefaultConfig.type;
        return this;
    }

    @Override
    public IToast type(byte type) {
        mUsingConfig.type = type;
        return this;
    }

    @Override
    public IToast gravity(int gravity) {
        mUsingConfig.wlp.gravity = gravity;
        return this;
    }
    /**
     * set a runnable run on toast start show.
     * @param action the show action
     * @return this
     */
    public BaseToast withStartAction(Runnable action) {
        this.mStart = action;
        return this;
    }

    /**
     * set a runnable run on toast end(without cancel outside).
     * @param action the end action
     * @return this
     */
    public BaseToast withEndAction(Runnable action) {
        this.mEnd = action;
        return this;
    }

    /**
     * position the toast at the target x, y.
     * @param x the x position in pixes
     * @param y the y position in pixes.
     * @return this.
     */
    public BaseToast position(int x, int y){
        mUsingConfig.wlp.x = x;
        mUsingConfig.wlp.y = y;
        return this;
    }

    /**
     * enable or disable the click of this toast.
     * @param enable true to enable. false to disable.
     * @return this.
     */
    public BaseToast enableClick(boolean enable) {
        if (enable) {
            mUsingConfig.wlp.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        } else {
            mUsingConfig.wlp.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }
        return this;
    }

    /**
     * set the toast view to another view which is assigned by the layout.
     * @param layout the layout id.
     * @param parent the parent of layout. can be null.
     * @param binder the view binder.
     * @return this.
     */
    public BaseToast layout(@LayoutRes int layout,@Nullable ViewGroup parent,@Nullable IViewBinder binder) {
        mToastView = LayoutInflater.from(mContext).inflate(layout, parent);
        if(binder != null){
            binder.onBind(mToastView);
        }
        return this;
    }

    /**
     * assign the animation style.
     * @param animStyle the anim style resource id
     * @return this.
     */
    public BaseToast animateStyle(@StyleRes int animStyle) {
        mUsingConfig.wlp.windowAnimations = animStyle;
        return this;
    }

    /**
     * show the toast.
     */
    public void show() {
        MainWorker.post(new Runnable() {
            @Override
            public void run() {
                showImpl();
            }
        });
    }

    private void showImpl() {
        if(mContext instanceof Activity){
            Activity ac = ((Activity) mContext);
            if(ac.isFinishing()){
                //ignore
                return;
            }
            if(Build.VERSION.SDK_INT >= 17 && ac.isDestroyed()){
                //ignore
                return;
            }
        }
        if(mStart != null){
            mStart.run();
            mStart = null;
        }
        cancel();
        mShowing = true;
        //mToastView.setY(-mToastView.getMeasuredHeight());
        mWM.addView(mToastView, mUsingConfig.wlp);
        MainWorker.postDelay(TOAST_SHOW_LENGTH, mCancelRun);
    }

    /**
     * cancel the showing toast.
     */
    public void cancel() {
        MainWorker.remove(mCancelRun);
        if (mShowing) {
            mWM.removeView(mToastView);
            mShowing = false;
        }
    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }


    private WindowManager.LayoutParams createDefault(Context context) {
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.windowAnimations = 0;
        //mParams.windowAnimations = R.style.topicAnim; //anim
        mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION; //toast need permission
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        mParams.x = 0;
        mParams.y = 150;
        mParams.setTitle("toast");
        // mParams.token = ((Activity)context)
        return mParams;
    }

}
