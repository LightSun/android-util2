package com.heaven7.android.util2;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * the mini window delegate.
 * @author heaven7
 */
public interface IWindow {

    /** indicate the window is debug */
    byte TYPE_DEBUG = 1;
    /** indicate the window is normal */
    byte TYPE_NORMAL = 2;
    /** indicate the window is warn */
    byte TYPE_WARN = 3;
    /** indicate the window is error */
    byte TYPE_ERROR = 4;

    /**
     * set the default window config.
     * @param config the config of window.
     * @return this.
     */
    IWindow setDefaultWindowConfig(WindowConfig config);

    /**
     * reset the window config to default. which is assigned by {@linkplain #setDefaultWindowConfig(WindowConfig)}.
     * @return this.
     */
    IWindow reset();

    /**
     * get the context of this window.
     * @return the context.
     */
    Context getContext();
    /**
     * get the current window view
     * @return the window view.
     */
    View getWindowView();

    /**
     * get the current type
     * @return the window type.
     */
    int getType();

    /**
     * assigned the window type.
     * @param type the type.
     * @return this.
     */
    IWindow type(byte type);

    /**
     * assigned the duration of window.
     * @param duration the duration.
     * @return this.
     */
    IWindow duration(long duration);
    /**
     * assigned the gravity.
     * @param gravity the gravity.
     * @return this.
     */
    IWindow gravity(int gravity);
    /**
     * set a runnable which will run on start show the window.
     * @param action the action.
     * @return this.
     */
    IWindow withStartAction(Runnable action);
    /**
     * set a runnable which will run on cancel the window.
     * @param action the action.
     * @return this.
     */
    IWindow withEndAction(Runnable action);

    /**
     * enable click or not.
     * @param enable true to enable.
     * @return this
     */
    IWindow enableClick(boolean enable);

    /**
     * assigned a new layout of window.
     * @param layout the layout id.
     * @param parent the parent
     * @param binder  the view binder
     * @return this
     */
    IWindow layout(@LayoutRes int layout, @Nullable ViewGroup parent, IViewBinder binder);

    /**
     * bind the view by the {@linkplain IViewBinder}
     * @param binder the binder
     * @return this
     */
    IWindow bindView(@NonNull IViewBinder binder);
    /**
     * assigned the animate style.
     * @param animStyle the animate style
     * @return this.
     */
    IWindow animateStyle(@StyleRes int animStyle);

    /**
     * position the window to target x and y .
     * @param x the x position in pixes.
     * @param y the y position in pixes.
     * @return this.
     */
    IWindow position(int x, int y);

    /**
     * show the window.
     * @param msg the message.
     */
    void show(String msg);
    /**
     * show the window.
     * @param resId the message id.
     */
    void show(int resId);

    /**
     * indicate the window is showing or not
     * @return true if is showing.
     */
    boolean isShowing();

    /**
     * show the window by default setting.
     */
    void show();

    /**
     * cancel the window.
     */
    void cancel();

    /**
     * the view binder
     */
    interface IViewBinder {
        /**
         * called on bind view
         * @param view the view of the window layout.
         */
        void onBind(View view);
    }

    class WindowConfig {
        public static final long DURATION_INFINITE = -1;
        public WindowManager.LayoutParams wlp;
        public byte type      = TYPE_NORMAL;
        public long duration  = 2000;

        public WindowConfig(WindowConfig src) {
            copy(src);
        }
        public WindowConfig() {
        }

        public void copy(WindowConfig src) {
            this.wlp = src.wlp;
            this.type = src.type;
            this.duration = src.duration;
        }
    }
}
