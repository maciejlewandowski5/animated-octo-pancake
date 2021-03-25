package com.maaps.expense.helpers;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ImageViewResizeAnimation extends Animation {


    private final View mView;
    private final float mToHeight;
    private final float mFromHeight;

    private final float mToWidth;
    private final float mFromWidth;

    public ImageViewResizeAnimation(View v, float fromWidth, float fromHeight, float toWidth, float toHeight) {
        mToHeight = toHeight;
        mToWidth = toWidth;
        mFromHeight = fromHeight;
        mFromWidth = fromWidth;
        mView = v;
        setDuration(300);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float height =
                (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;

        float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;

        ViewGroup.LayoutParams p = mView.getLayoutParams();
        p.height = (int) height;
        p.width = (int) width;
        mView.requestLayout();
    }
}

