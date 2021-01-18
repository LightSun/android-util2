package com.heaven7.android.util2.demo.sample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.heaven7.android.util2.demo.R;

/**
 * 测试点击放大
 */
public class TestClickScaleImage extends AppCompatActivity {

  // 持有这个动画的引用，让他可以在动画执行中途取消
  private Animator mCurrentAnimator;

  private int mShortAnimationDuration;

  private View imageView1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.ac_test_click_scaleimage);
    initView();

    imageView1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        zoomImageFromThumb(imageView1, R.drawable.icon);
      }
    });

    // 系统默认的短动画执行时间 200
    mShortAnimationDuration = getResources().getInteger(
        android.R.integer.config_shortAnimTime);
  }

  private void initView() {
    imageView1 = (ImageView) findViewById(R.id.imageView1);
  }
  private void zoomImageFromThumb(final View thumbView, int imageResId) {
    // 如果有动画正在运行，取消这个动画
    if (mCurrentAnimator != null) {
      mCurrentAnimator.cancel();
    }

    // 加载显示大图的ImageView
    final ImageView expandedImageView = (ImageView) findViewById(
        R.id.expanded_image);
    expandedImageView.setImageResource(imageResId);

    // 计算初始小图的边界位置和最终大图的边界位置。
    final Rect startBounds = new Rect();
    final Rect finalBounds = new Rect();
    final Point globalOffset = new Point();

    // 小图的边界就是小ImageView的边界，大图的边界因为是铺满全屏的，所以就是整个布局的边界。
    // 然后根据偏移量得到正确的坐标。
    thumbView.getGlobalVisibleRect(startBounds);
    findViewById(R.id.imageView1).getGlobalVisibleRect(finalBounds, globalOffset);
    startBounds.offset(-globalOffset.x, -globalOffset.y);
    finalBounds.offset(-globalOffset.x, -globalOffset.y);

    // 计算初始的缩放比例。最终的缩放比例为1。并调整缩放方向，使看着协调。
    float startScale=0;
    if ((float) finalBounds.width() / finalBounds.height()
        > (float) startBounds.width() / startBounds.height()) {
      // 横向缩放
      float startWidth = startScale * finalBounds.width();
      float deltaWidth = (startWidth - startBounds.width()) / 2;
      startBounds.left -= deltaWidth;
      startBounds.right += deltaWidth;
    } else {
      // 竖向缩放
      float startHeight = startScale * finalBounds.height();
      float deltaHeight = (startHeight - startBounds.height()) / 2;
      startBounds.top -= deltaHeight;
      startBounds.bottom += deltaHeight;
    }

    // 隐藏小图，并显示大图
    thumbView.setAlpha(0f);
    expandedImageView.setVisibility(View.VISIBLE);

    // 将大图的缩放中心点移到左上角。默认是从中心缩放
    expandedImageView.setPivotX(0f);
    expandedImageView.setPivotY(0f);

    //对大图进行缩放动画
    AnimatorSet set = new AnimatorSet();
    set.play(ObjectAnimator.ofFloat(expandedImageView, View.X, startBounds.left, finalBounds.left))
        .with(ObjectAnimator.ofFloat(expandedImageView, View.Y, startBounds.top, finalBounds.top))
        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X, startScale, 1f))
        .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_Y, startScale, 1f));
    set.setDuration(mShortAnimationDuration);
    set.setInterpolator(new DecelerateInterpolator());
    set.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        mCurrentAnimator = null;
      }

      @Override
      public void onAnimationCancel(Animator animation) {
        mCurrentAnimator = null;
      }
    });
    set.start();
    mCurrentAnimator = set;

    // 点击大图时，反向缩放大图，然后隐藏大图，显示小图。
    final float startScaleFinal = startScale;
    expandedImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mCurrentAnimator != null) {
          mCurrentAnimator.cancel();
        }

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator
            .ofFloat(expandedImageView, View.X, startBounds.left))
            .with(ObjectAnimator
                .ofFloat(expandedImageView,
                    View.Y,startBounds.top))
            .with(ObjectAnimator
                .ofFloat(expandedImageView,
                    View.SCALE_X, startScaleFinal))
            .with(ObjectAnimator
                .ofFloat(expandedImageView,
                    View.SCALE_Y, startScaleFinal));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            thumbView.setAlpha(1f);
            expandedImageView.setVisibility(View.GONE);
            mCurrentAnimator = null;
          }

          @Override
          public void onAnimationCancel(Animator animation) {
            thumbView.setAlpha(1f);
            expandedImageView.setVisibility(View.GONE);
            mCurrentAnimator = null;
          }
        });
        set.start();
        mCurrentAnimator = set;
      }
    });
  }
}

