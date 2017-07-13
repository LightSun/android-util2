package com.heaven7.android.util2;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.AnyThread;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.annotation.UiThread;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.heaven7.core.util.MainWorker;
import com.heaven7.java.base.anno.IntDef;
import com.heaven7.java.base.util.Throwables;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * the common window view . can be used as window and etc.
 * Created by heaven7
 */
public abstract class BaseWindow implements IWindow {

    private final WindowManager mWM;
    private final Context mContext;

    private final Runnable mCancelRun = new Runnable() {
        @Override
        public void run() {
            cancel();
            if (mEnd != null) {
                mEnd.run();
                mEnd = null;
            }
        }
    };
    private boolean mShowing;
    private Runnable mStart;
    private Runnable mEnd;
    private final WindowConfig mDefaultConfig;
    private final WindowConfig mUsingConfig;
    private View mWindowView;

    @IntDef({
            TYPE_DEBUG,
            TYPE_NORMAL,
            TYPE_WARN,
            TYPE_ERROR,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface WindowType {
    }

    protected BaseWindow(Context context) {
        this.mContext = context;
        this.mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mUsingConfig = new WindowConfig();
        mUsingConfig.wlp = createDefault(context);
        mUsingConfig.type = TYPE_NORMAL;
        mDefaultConfig = new WindowConfig(mUsingConfig);
    }

    @Override
    public abstract void show(String msg);

    @Override
    public void show(int resId) {
        show(getContext().getString(resId));
    }

    //======================================================//

    @Override
    public
    @WindowType
    int getType() {
        return mUsingConfig.type;
    }

    @Override
    public View getWindowView() {
        return mWindowView;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public IWindow setDefaultWindowConfig(WindowConfig config) {
        Throwables.checkNull(config);
        Throwables.checkNull(config.wlp);
        applyGravity(config.wlp.gravity, config.wlp);
        this.mDefaultConfig.copy(config);
        return reset();
    }

    @Override
    public IWindow reset() {
        this.mUsingConfig.copy(mDefaultConfig);
        return this;
    }

    @Override
    public IWindow type(@WindowType byte type) {
        mUsingConfig.type = type;
        return this;
    }

    @Override
    public IWindow duration(long duration) {
        mUsingConfig.duration = duration;
        return this;
    }

    @Override
    public IWindow gravity(int gravity) {
        applyGravity(gravity, mUsingConfig.wlp);
        return this;
    }

    @Override
    public BaseWindow withStartAction(Runnable action) {
        this.mStart = action;
        return this;
    }

    @Override
    public BaseWindow withEndAction(Runnable action) {
        this.mEnd = action;
        return this;
    }

    @Override
    public BaseWindow position(int x, int y) {
        mUsingConfig.wlp.x = x;
        mUsingConfig.wlp.y = y;
        return this;
    }

    @Override
    public IWindow bindView(@NonNull IViewBinder binder) {
        Throwables.checkNull(binder);
        binder.onBind(getWindowView());
        return this;
    }

    @Override
    public BaseWindow enableClick(boolean enable) {
        if (enable) {
            mUsingConfig.wlp.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        } else {
            mUsingConfig.wlp.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }
        return this;
    }

    @Override
    public BaseWindow layout(@LayoutRes int layout, @Nullable ViewGroup parent, @Nullable IViewBinder binder) {
        mWindowView = LayoutInflater.from(mContext).inflate(layout, parent);
        if (binder != null) {
            binder.onBind(mWindowView);
        }
        return this;
    }

    @Override
    public BaseWindow animateStyle(@StyleRes int animStyle) {
        mUsingConfig.wlp.windowAnimations = animStyle;
        return this;
    }

    @AnyThread
    @Override
    public void show() {
        MainWorker.post(new Runnable() {
            @Override
            public void run() {
                showImpl();
            }
        });
    }

    @UiThread
    private void showImpl() {
        if (mContext instanceof Activity) {
            Activity ac = ((Activity) mContext);
            if (ac.isFinishing()) {
                //ignore
                return;
            }
            if (Build.VERSION.SDK_INT >= 17 && ac.isDestroyed()) {
                //ignore
                return;
            }
        }
        if (mStart != null) {
            mStart.run();
            mStart = null;
        }
        if (mShowing) {
            cancel();
        }
        mShowing = true;
        //mWindowView.setY(-mWindowView.getMeasuredHeight());
        mWM.addView(mWindowView, mUsingConfig.wlp);
        //duration < 0, means until cancel.
        if (mUsingConfig.duration > 0) {
            MainWorker.postDelay(mUsingConfig.duration, mCancelRun);
        }
    }

    @UiThread
    public void cancel() {
        MainWorker.remove(mCancelRun);
        if (mShowing) {
            if (mWindowView.getParent() != null) {
                mWM.removeView(mWindowView);
            }
            mShowing = false;
        }
    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }

    /**
     * apply the gravity for window params.
     * @param expectGravity the expect gravity
     * @param applyWlp the window layout params.
     */
    private void applyGravity(int expectGravity, WindowManager.LayoutParams applyWlp) {
        if(Build.VERSION.SDK_INT >= 17){
            final Configuration configuration = getContext().getResources().getConfiguration();
            final int gravity = Gravity.getAbsoluteGravity(expectGravity, configuration.getLayoutDirection());
            applyWlp.gravity = gravity;
            if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == Gravity.FILL_HORIZONTAL) {
                applyWlp.horizontalWeight = 1.0f;
            }
            if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == Gravity.FILL_VERTICAL) {
                applyWlp.verticalWeight = 1.0f;
            }
        }else{
            applyWlp.gravity = expectGravity;
        }
    }

    private WindowManager.LayoutParams createDefault(Context context) {
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.windowAnimations = 0;
        //mParams.windowAnimations = R.style.topicAnim; //anim
        mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION; //window need permission
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        mParams.x = 0;
        mParams.y = 150;
        mParams.setTitle("window");
        // mParams.token = ((Activity)context)
        return mParams;
    }

}
