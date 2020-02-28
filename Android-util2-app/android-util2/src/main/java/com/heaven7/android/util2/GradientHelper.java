package com.heaven7.android.util2;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.annotation.ColorInt;

import com.heaven7.core.util.ViewCompatUtil;
import com.heaven7.java.base.anno.Nullable;
import com.heaven7.java.base.util.Throwables;

/**
 * this class help we handle GradientDrawable.
 * Created by heaven7 on 2017/7/17 0017.
 * @since 1.0.3
 */

public class GradientHelper {

    private final GradientDrawable mGd;
    private final View mView;

    /**
     * create gradient helper from target view .
     *
     * @param target the target view to apply.
     */
    public GradientHelper(View target) {
        this(target, null);
    }

    /**
     * create gradient helper from target view and GradientDrawable.
     * if GradientDrawable == null && view.getBackground is not GradientDrawable. a new GradientDrawable will be created.
     *
     * @param target the target view to apply.
     * @param gd     the source GradientDrawable. can be null
     */
    public GradientHelper(View target, @Nullable GradientDrawable gd) {
        Throwables.checkNull(target);
        this.mView = target;
        if (gd != null) {
            mGd = gd;
        } else {
            Drawable bg = target.getBackground();
            if (bg != null && bg instanceof GradientDrawable) {
                this.mGd = (GradientDrawable) bg;
            } else {
                this.mGd = new GradientDrawable();
            }
        }
        mGd.mutate();
    }

    /**
     * create gradient helper from target view and GradientDrawable.
     * if GradientDrawable == null && view.getBackground is not GradientDrawable. a new GradientDrawable will be created.
     *
     * @param target the target view to apply.
     * @param gd     the source GradientDrawable. can be null
     */
    public static GradientHelper of(View target, @Nullable GradientDrawable gd) {
        return new GradientHelper(target, gd);
    }

    /**
     *  Make this drawable mutable.
     * @return this.
     * @see Drawable#mutate()
     */
    public GradientHelper mutate(){
        mGd.mutate();
        return this;
    }

    public GradientHelper setAlpha(int alpha) {
        mGd.setAlpha(alpha);
        return this;
    }

    /**
     * <p>Sets the type of shape used to draw the gradient.</p>
     * <p><strong>Note</strong>: changing this property will affect all instances
     * of a drawable loaded from a resource. It is recommended to invoke
     * {@link GradientDrawable#mutate()} before changing this property.</p>
     *
     * @param shape The desired shape for this drawable: {@link GradientDrawable#LINE},
     *              {@link GradientDrawable#OVAL}, {@link GradientDrawable#RECTANGLE} or {@link GradientDrawable#RING}
     * @return this.
     * @see GradientDrawable#mutate()
     */
    public GradientHelper setShape(int shape) {
        mGd.setShape(shape);
        return this;
    }

    /**
     * Changes this drawable to use a single color instead of a gradient.
     * <p>
     * <strong>Note</strong>: changing color will affect all instances of a
     * drawable loaded from a resource. It is recommended to invoke
     * {@link GradientDrawable#mutate()} before changing the color.
     *
     * @param color The color used to fill the shape
     * @return this.
     * @see GradientDrawable#mutate()
     * @see GradientDrawable#setColors(int[])
     * @see GradientDrawable#getColor
     */
    public GradientHelper setSolidColor(@ColorInt int color) {
        mGd.setColor(color != 0 ? color : Color.TRANSPARENT);
        return this;
    }


    /**
     * Sets the colors used to draw the gradient.
     * <p>
     * Each color is specified as an ARGB integer and the array must contain at
     * least 2 colors.
     * <p>
     * <strong>Note</strong>: changing colors will affect all instances of a
     * drawable loaded from a resource. It is recommended to invoke
     * {@link GradientDrawable#mutate()} before changing the colors.
     *
     * @param colors an array containing 2 or more ARGB colors
     * @return this.
     * @see GradientDrawable#mutate()
     * @see GradientDrawable#setColor(int)
     */
    @TargetApi(16)
    public GradientHelper setGradientColors(@ColorInt int[] colors) {
        mGd.setColors(colors);
        return this;
    }

    /**
     * Changes this drawable to use a single color state list instead of a
     * gradient. Calling this method with a null argument will clear the color
     * and is equivalent to calling {@link GradientDrawable#setColor(int)} with the argument
     * {@link Color#TRANSPARENT}.
     * <p>
     * <strong>Note</strong>: changing color will affect all instances of a
     * drawable loaded from a resource. It is recommended to invoke
     * {@link GradientDrawable#mutate()} before changing the color.</p>
     *
     * @param colorStateList The color state list used to fill the shape
     * @return this.
     * @see GradientDrawable#mutate()
     * @see GradientDrawable#getColor
     */
    @TargetApi(21)
    public GradientHelper setSolidColors(@Nullable ColorStateList colorStateList) {
        mGd.setColor(colorStateList);
        return this;
    }

    /**
     * Specifies radii for each of the 4 corners. For each corner, the array
     * contains 2 values, <code>[X_radius, Y_radius]</code>. The corners are
     * ordered top-left, top-right, bottom-right, bottom-left. This property
     * is honored only when the shape is of type {@link GradientDrawable#RECTANGLE}.
     * <p>
     * <strong>Note</strong>: changing this property will affect all instances
     * of a drawable loaded from a resource. It is recommended to invoke
     * {@link GradientDrawable#mutate()} before changing this property.
     *
     * @param radii an array of length >= 8 containing 4 pairs of X and Y
     *              radius for each corner, specified in pixels
     * @return this.
     * @see GradientDrawable#mutate()
     * @see GradientDrawable#setShape(int)
     * @see #setCornerRadius(float)
     */
    public GradientHelper setCornerRadii(@Nullable float[] radii) {
        mGd.setCornerRadii(radii);
        return this;
    }

    /**
     * Specifies the radius for the corners of the gradient. If this is > 0,
     * then the drawable is drawn in a round-rectangle, rather than a
     * rectangle. This property is honored only when the shape is of type
     * {@link GradientDrawable#RECTANGLE}.
     * <p>
     * <strong>Note</strong>: changing this property will affect all instances
     * of a drawable loaded from a resource. It is recommended to invoke
     * {@link GradientDrawable#mutate()} before changing this property.
     *
     * @param radius The radius in pixels of the corners of the rectangle shape
     * @return this.
     * @see GradientDrawable#mutate()
     * @see GradientDrawable#setCornerRadii(float[])
     * @see GradientDrawable#setShape(int)
     */
    public GradientHelper setCornerRadius(float radius) {
        mGd.setCornerRadius(radius);
        return this;
    }


    /**
     * <p>Set the stroke width and color for the drawable. If width is zero,
     * then no stroke is drawn.</p>
     * <p><strong>Note</strong>: changing this property will affect all instances
     * of a drawable loaded from a resource. It is recommended to invoke
     * {@link GradientDrawable#mutate()} before changing this property.</p>
     *
     * @param width The width in pixels of the stroke
     * @param color The color of the stroke
     * @return this.
     * @see GradientDrawable#mutate()
     * @see #setStroke(int, int, float, float)
     */
    public GradientHelper setStroke(int width, @ColorInt int color) {
        setStroke(width, color, 0, 0);
        return this;
    }

    /**
     * <p>Set the stroke width and color for the drawable. If width is zero,
     * then no stroke is drawn. This method can also be used to dash the stroke.</p>
     * <p><strong>Note</strong>: changing this property will affect all instances
     * of a drawable loaded from a resource. It is recommended to invoke
     * {@link GradientDrawable#mutate()} before changing this property.</p>
     *
     * @param width     The width in pixels of the stroke
     * @param color     The color of the stroke
     * @param dashWidth The length in pixels of the dashes, set to 0 to disable dashes
     * @param dashGap   The gap in pixels between dashes
     * @return this.
     * @see GradientDrawable#mutate()
     * @see GradientDrawable#setStroke(int, int)
     */
    public GradientHelper setStroke(int width, @ColorInt int color, float dashWidth, float dashGap) {
        mGd.setStroke(width, color, dashWidth, dashGap);
        return this;
    }

    /**
     * <p>Sets the size of the shape drawn by this drawable.</p>
     * <p><strong>Note</strong>: changing this property will affect all instances
     * of a drawable loaded from a resource. It is recommended to invoke
     * {@link GradientDrawable#mutate()} before changing this property.</p>
     *
     * @param width The width of the shape used by this drawable
     * @param height The height of the shape used by this drawable
     * @return this.
     * @see GradientDrawable#mutate()
     * @see GradientDrawable#setGradientType(int)
     */
    public GradientHelper setSize(int width, int height) {
        mGd.setSize(width, height);
        return this;
    }

    /**
     * Sets the orientation of the gradient defined in this drawable.
     * <p>
     * <strong>Note</strong>: changing orientation will affect all instances
     * of a drawable loaded from a resource. It is recommended to invoke
     * {@link GradientDrawable#mutate()} before changing the orientation.
     *
     * @param orientation the desired orientation (angle) of the gradient
     * @return this.
     * @see GradientDrawable#mutate()
     * @see GradientDrawable#getOrientation()
     */
    @TargetApi(16)
    public GradientHelper setOrientation(GradientDrawable.Orientation orientation) {
        mGd.setOrientation(orientation);
        return this;
    }

    /**
     * apply the gradient drawable to view.
     */
    public void apply() {
        final Drawable gd = mView.getBackground();
        if (gd != null && gd instanceof GradientDrawable && mGd == gd) {
            mView.postInvalidate();
        } else {
            ViewCompatUtil.setBackgroundCompatible(mView, mGd);
        }
    }
}
