package com.heaven7.android.util2;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * the toast delegate.
 * @author heaven7
 */
public interface IToast {

    /** indicate the toast is debug */
    byte TYPE_DEBUG = 1;
    /** indicate the toast is normal */
    byte TYPE_NORMAL = 2;
    /** indicate the toast is warn */
    byte TYPE_WARN = 3;
    /** indicate the toast is error */
    byte TYPE_ERROR = 4;

    /**
     * set the default window params.
     * @param config the config of toast.
     * @return this.
     */
    IToast setDefaultToastConfig(ToastConfig config);

    /**
     * reset the toast config to default. which is assigned by {@linkplain #setDefaultToastConfig(ToastConfig)}.
     * @return this.
     */
    IToast reset();

    /**
     * get the current toast view
     * @return the toast view.
     */
    View getToastView();

    /**
     * get the current type
     * @return the toast type.
     */
    int getType();

    /**
     * assigned the toast type.
     * @param type the type.
     * @return this.
     */
    IToast type(byte type);
    /**
     * assigned the gravity.
     * @param gravity the gravity.
     * @return this.
     */
    IToast gravity(int gravity);
    /**
     * set a runnable which will run on start show the toast.
     * @param action the action.
     * @return this.
     */
    IToast withStartAction(Runnable action);
    /**
     * set a runnable which will run on cancel the toast.
     * @param action the action.
     * @return this.
     */
    IToast withEndAction(Runnable action);

    /**
     * enable click or not.
     * @param enable true to enable.
     * @return this
     */
    IToast enableClick(boolean enable);

    /**
     * assigned a new layout of toast.
     * @param layout the layout id.
     * @param parent the parent
     * @param binder  the view binder
     * @return this
     */
    IToast layout(@LayoutRes int layout, @Nullable ViewGroup parent, IViewBinder binder);

    /**
     * assigned the animate style.
     * @param animStyle the animate style
     * @return this.
     */
    IToast animateStyle(@StyleRes int animStyle);

    /**
     * position the toast to target x and y .
     * @param x the x position in pixes.
     * @param y the y position in pixes.
     * @return this.
     */
    IToast position(int x, int y);

    /**
     * show the toast.
     * @param msg the message.
     */
    void show(String msg);
    /**
     * show the toast.
     * @param resId the message id.
     */
    void show(int resId);

    /**
     * indicate the toast is showing or not
     * @return true if is showing.
     */
    boolean isShowing();

    /**
     * cancel the toast.
     */
    void cancel();

    /**
     * the view binder
     */
    interface IViewBinder {
        /**
         * called on bind view
         * @param view the view of the toast layout.
         */
        void onBind(View view);
    }
    class ToastConfig{
        public WindowManager.LayoutParams wlp;
        public byte type = TYPE_NORMAL;

        public ToastConfig(ToastConfig src) {
            this.wlp = src.wlp;
            this.type = src.type;
        }
        public ToastConfig() {
        }
    }
}
