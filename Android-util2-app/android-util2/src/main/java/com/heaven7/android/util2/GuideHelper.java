package com.heaven7.android.util2;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.heaven7.core.util.Logger;
import com.heaven7.java.base.util.ArrayUtils;
import com.heaven7.java.base.util.Throwables;

import static com.heaven7.android.util2.ViewUtil.getStatusHeight;

/**
 * the class help we handle the guide.
 * Created by heaven7 on 2017/8/9 0009.
 * @since 1.0.4
 */

public final class GuideHelper {

    /**
     * align direction: left
     */
    public static final byte ALIGN_LEFT = 1;
    /**
     * align direction: right
     */
    public static final byte ALIGN_RIGHT = 2;
    /**
     * align direction: top
     */
    public static final byte ALIGN_TOP   = 3;
    /**
     * align direction: bottom
     */
    public static final byte ALIGN_BOTTOM = 4;

    /**
     * indicate absolute value.
     */
    public static final byte ABSOLUTE        = 11;
    /**
     * indicate relative to tip view of guide.
     */
    public static final byte RELATIVE_TIP    = 12;
    /**
     * indicate relative to anchor view of guide.
     */
    public static final byte RELATIVE_ANCHOR = 14;

    /**
     * the guide callback.
     * @since 1.0.4
     */
    public static abstract class GuideCallback{

        /**
         * called when click the root view.
         * @param root the root view
         * @return true if handled this event. false otherwise.
         */
        public boolean handleClickRoot(View root){
            return false;
        }
        /**
         * called when click the tip view.
         * @param tip the tip view
         * @return true if handled this event. false otherwise.
         */
        public boolean handleClickTip(View tip){
            return false;
        }
        /**
         * called when click the Anchor view.
         * @param copyOfAnchor the copy of Anchor view
         * @return true if handled this event. false otherwise.
         */
        public boolean handleClickAnchor(View copyOfAnchor){
            return false;
        }

        /**
         * called on bind data for anchor view.
         * @param copyOfAnchor the copy of anchor view
         * @since 1.0.5
         */
        public void onBindData(View copyOfAnchor) {

        }

        /**
         * called on show the guide
         */
        public void onShow() {

        }
        /**
         * called on dismiss the guide
         */
        public void onDismiss() {

        }
    }

    private final AndroidSmartReference<Activity> mAsr_activity;
    private final InternalWindow mWindow;
    private final int mStateBarHeight;
    private final int mLayoutId;

    private GuideCallback mCallback;

    /**
     * create the guide helper.
     * @param context the activity.
     * @param layoutId the layout id of activity.
     */
    public GuideHelper(Activity context, @LayoutRes int layoutId) {
        this.mAsr_activity = new AndroidSmartReference<Activity>(context);
        this.mLayoutId = layoutId;
        this.mWindow = new InternalWindow(context);
        this.mStateBarHeight = getStatusHeight(context);

        WindowManager.LayoutParams wlp = BaseWindow.createDefault(context);
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

        IWindow.WindowConfig config = new IWindow.WindowConfig();
        config.wlp = wlp;
        config.duration = IWindow.WindowConfig.DURATION_INFINITE;

        mWindow.setDefaultWindowConfig(config);
        mWindow.layout(getGuideLayoutId(), null, null);
    }

    public static String alignToString(byte align) {
        switch (align) {
            case ALIGN_BOTTOM:
                return "ALIGN_BOTTOM";
            case ALIGN_LEFT:
                return "ALIGN_LEFT";
            case ALIGN_RIGHT:
                return "ALIGN_RIGHT";
            case ALIGN_TOP:
                return "ALIGN_TOP";
        }
        return null;
    }


    /**
     * show the guide for target components.
     * @param gc the guide component
     * @param callback the guide callback.
     */
    public void show(GuideComponent gc, final GuideCallback callback) {
        show(ArrayUtils.toArray(gc), callback);
    }

    /**
     * show the guide for target components.
     * @param gcs the guide components
     * @param callback the guide callback.
     */
    public void show(GuideComponent[] gcs, final GuideCallback callback) {
        Throwables.checkNull(callback);
        Throwables.checkEmpty(gcs);
        this.mCallback = callback;
        clearChildren();
        mWindow.bindView(new GuideViewBinder(gcs))
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        callback.onShow();
                    }
                })
                .show();
    }

    /**
     * cancel the guide and will not notify callback of dismiss.
     * this method is unlike the {@linkplain #dismiss()}.
     * @see #dismiss()
     * @since 1.0.5
     */
    public void cancel(){
        dismissInternal(false);
    }
    /**
     * dismiss the guide window and notify dismiss if need.
     * this method is unlike the {@linkplain #cancel()} ()}.
     * @see #cancel()
     */
    public void dismiss() {
        dismissInternal(true);
    }
    /**
     * get the guide layout id  which will be used as root view
     * @return the guide layout id.
     */
    protected @LayoutRes int getGuideLayoutId(){
        return R.layout.heaven7_view_guide_mask;
    }

    //======================= start private ===============

    private void dismissInternal(boolean callback) {
        mWindow.cancel();
        clearChildren();
        if(callback && mCallback != null) {
            mCallback.onDismiss();
        }
        mCallback = null;
    }

    private void clearChildren() {
        ViewGroup vg = (ViewGroup) mWindow.getWindowView();
        vg.removeAllViews();
    }

    /**
     * the guide component
     * @since 1.0.4
     */
    public static class GuideComponent {
        /**
         * the anchor view which is already showed on screen
         */
        private View anchor;
        /**
         * the tip view which will show for target anchor
         */
        private View tip;
        /**
         * the show info of relative to anchor
         */
        private RelativeLocation rp;

        /**
         * get the anchor view id.
         * @return the anchor view id
         */
        public int getAnchorViewId(){
            return anchor.getId();
        }
        /**
         * get the anchor view.
         * @return the anchor view
         */
        public View getAnchor() {
            return anchor;
        }

        /**
         * get tip view. may be null
         * @return tip view
         */
        public View getTip() {
            return tip;
        }

        /**
         * get the relative location.
         * @return the relative location
         */
        public RelativeLocation getRelativeLocation() {
            return rp;
        }

        /**
         * the guide component builder.
         */
        public static class Builder{

            private final GuideComponent gc = new GuideComponent();

            /**
             * assign the anchor view
             * @param anchor the anchor view
             * @return this.
             */
            public Builder anchor(View anchor){
                gc.anchor = anchor;
                return this;
            }
            /**
             * assign the tip view, may be null.
             * @param tip the tip view
             * @return this.
             */
            public Builder tip(View tip){
                gc.tip = tip;
                return this;
            }
            /**
             * assign the relative location of tip view.
             * @param rp the relative location
             * @return this.
             */
            public Builder location(RelativeLocation rp){
                gc.rp = rp;
                return this;
            }

            /**
             * build the component
             * @return the guide component.
             */
            public GuideComponent build(){
                Throwables.checkNull(gc.anchor);
                Throwables.checkNull(gc.rp);
                return gc;
            }
        }
    }

    /**
     * the relative location.
     * @since 1.0.4
     */
    public static class RelativeLocation {
        final byte align;
        final int margin; //align margin

        final byte relativeType;
        /**
         * offset of center, top and left is positive, right bottom is negative.
         * may be percent of sth.
         * @see  #ABSOLUTE
         * @see  #RELATIVE_ANCHOR
         * @see  #RELATIVE_TIP
         */
        final float offsetValue;

        /**
         * create relative location with relative type ({@linkplain #ABSOLUTE}).
         * @param align the align type. see {@linkplain #ALIGN_BOTTOM} and etc.
         * @param margin the margin of align
         * @param offset the absolute offset value .
         * @see #ALIGN_BOTTOM
         * @see #ALIGN_LEFT
         * @see #ALIGN_RIGHT
         * @see #ALIGN_TOP
         */
        public RelativeLocation(byte align, int margin, int offset) {
            this(align, margin, ABSOLUTE, offset);
        }

        /**
         * create relative location.
         * @param align the align type. see {@linkplain #ALIGN_BOTTOM} and etc.
         * @param margin the margin of align
         * @param relativeType the relative type . see {@link #ABSOLUTE} and etc.
         * @param offsetValue the offset value. may be absolute value of relative value.
         * @see #ABSOLUTE
         * @see #RELATIVE_TIP
         * @see #RELATIVE_ANCHOR
         */
        public RelativeLocation(byte align, int margin, byte relativeType, float offsetValue) {
            this.align = align;
            this.margin = margin;
            this.relativeType = relativeType;
            this.offsetValue = offsetValue;
        }

        /*package*/ int getOffSet(int anchorW, int anchorH, int tipW, int tipH){
            switch (relativeType){
                case ABSOLUTE:
                    return (int) offsetValue;
                case RELATIVE_ANCHOR: {
                    switch (align){
                        case ALIGN_BOTTOM:
                        case ALIGN_TOP:
                            return (int) (anchorW* offsetValue);

                        case ALIGN_LEFT:
                        case ALIGN_RIGHT:
                            return (int) (anchorH * offsetValue);
                    }
                }
                case RELATIVE_TIP: {
                    switch (align){
                        case ALIGN_BOTTOM:
                        case ALIGN_TOP:
                            return (int) (tipW * offsetValue);

                        case ALIGN_LEFT:
                        case ALIGN_RIGHT:
                            return (int) (tipH * offsetValue);
                    }
                }
                default:
                    Logger.w("RelativeLocation","getOffSet","wrong relative type = " + relativeType);
            }
            return 0;
        }
    }

    private class GuideViewBinder implements IWindow.IViewBinder{

        final GuideComponent[] mGcs;
        final boolean mFullScreen;

        public GuideViewBinder(GuideComponent[] mGcs) {
            this.mGcs = mGcs;
            //fullscreen may change. so dynamic get.
            this.mFullScreen = (mAsr_activity.get().getWindow()
                    .getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) != 0;
        }

        @Override
        public void onBind(View view) {
            //use AsyncInflater may cause some problem
            View root = LayoutInflater.from(view.getContext()).inflate(mLayoutId, null);
            if(mAsr_activity.get() != null) {
                for (GuideComponent component : mGcs) {
                    bindImpl(root, component);
                }
            }
        }

        private void bindImpl(View root, GuideComponent component){
            final View copy = root.findViewById(component.getAnchorViewId());
            ((ViewGroup) copy.getParent()).removeView(copy);

            ViewGroup vg = (ViewGroup) mWindow.getWindowView();
            final View anchor = component.getAnchor();

            final int[] cors = new int[2];
            anchor.getLocationOnScreen(cors);
            final int x = cors[0];
            final int y = !mFullScreen ? cors[1] - mStateBarHeight : cors[1];
            int anchorCenterX = x + anchor.getWidth() / 2;
            int anchorCenterY = y + anchor.getHeight() / 2;

            FrameLayout.MarginLayoutParams mlp = new FrameLayout.MarginLayoutParams(
                    anchor.getWidth(), anchor.getHeight());
            mlp.leftMargin = x;
            mlp.topMargin = y;
            vg.addView(copy, mlp);
            mCallback.onBindData(copy);
            //tip may be null.
            final View tip = component.getTip();
            if(tip != null) {
                final RelativeLocation rp = component.getRelativeLocation();
                if(tip.getParent() != null){
                    ((ViewGroup)tip.getParent()).removeView(tip);
                }
                //tip must be wrap_content or determinate
                int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                tip.measure(spec, spec);
                final int tipMeaWidth = tip.getMeasuredWidth();
                final int tipMeaHeight = tip.getMeasuredHeight();
                //offset
                final int offSet = rp.getOffSet(anchor.getWidth(), anchor.getHeight(), tipMeaWidth, tipMeaHeight);
                mlp = new FrameLayout.MarginLayoutParams(tipMeaWidth, tipMeaHeight);
                switch (rp.align) {
                    case ALIGN_TOP:
                        mlp.topMargin = y - rp.margin - tipMeaHeight;
                        mlp.leftMargin = anchorCenterX - tipMeaWidth / 2 - offSet;
                        break;

                    case ALIGN_LEFT:
                        mlp.topMargin = anchorCenterY - tipMeaHeight / 2 - offSet;
                        mlp.leftMargin = x - rp.margin - tipMeaWidth;
                        break;

                    case ALIGN_RIGHT:
                        mlp.topMargin = anchorCenterY - tipMeaHeight / 2 - offSet;
                        mlp.leftMargin = x + anchor.getWidth() + rp.margin;
                        break;

                    case ALIGN_BOTTOM:
                        mlp.leftMargin = anchorCenterX - tipMeaWidth / 2 - offSet;
                        mlp.topMargin = y + anchor.getHeight() + rp.margin;
                        break;

                    default:
                        throw new UnsupportedOperationException();
                }
                vg.addView(tip, mlp);
                tip.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (!mCallback.handleClickTip(v)) {
                            dismiss();
                        }
                    }
                });
            }
           //listeners
            vg.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(!mCallback.handleClickRoot(v)){
                        dismiss();
                    }
                }
            });
            copy.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(!mCallback.handleClickAnchor(v)){
                        dismiss();
                    }
                }
            });
        }
    }

    private static class InternalWindow extends BaseWindow {

        protected InternalWindow(Context context) {
            super(context);
        }

        @Override
        public void show(String msg) {

        }
    }
}
