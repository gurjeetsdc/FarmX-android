package com.sdei.farmx.customview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

public class CustomPager extends ViewPager {

    private boolean isSwipeEnabled;
    private ScrollerCustomDuration mScroller = null;

    public CustomPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.isSwipeEnabled = true;
        postInitViewPager();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isSwipeEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        try {
            return this.isSwipeEnabled && super.onInterceptTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }

    /**
     * Override the Scroller instance with our own class so we can change the
     * duration
     */
    private void postInitViewPager() {
        try {

            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            mScroller = new ScrollerCustomDuration(getContext(), new AccelerateInterpolator());
            scroller.set(this, mScroller);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setScrollDuration() {
        mScroller.setDuration();
    }

    /**
     * Custom method to enable or disable swipe
     *
     * @param isSwipeEnabled true to enable swipe, false otherwise
     */
    public void setPagingEnabled(boolean isSwipeEnabled) {
        this.isSwipeEnabled = isSwipeEnabled;
    }

    class ScrollerCustomDuration extends Scroller {

        private int mDuration = 1000;

        public ScrollerCustomDuration(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            // Ignore received duration, use fixed one instead
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, mDuration);
            mDuration = 0;
        }

        public void setDuration() {
            mDuration = 250;
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if(h > height) height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
