package com.softdesign.devintensive.ui.activities;

import android.content.Context;
import com.softdesign.devintensive.R;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class CounterViewBehavior extends CoordinatorLayout.Behavior<LinearLayout>{
    private Context mContext;
    private float mMaxDependencyHeight;
    private int mChildHeight;

    public CounterViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {

        Snackbar.make(parent, "getTranslationY: "+String.valueOf(dependency.getTranslationY()) +", getHeight: "+String.valueOf(dependency.getHeight()), Snackbar.LENGTH_LONG).show();

        child.setY(dependency.getBottom());
        float dependencyHeight = dependency.getBottom() - getActionBarHeight() - getStatusBarHeight();

        if (mChildHeight == 0) {
            mMaxDependencyHeight = dependencyHeight;
        }

        float minChildHeight = mContext.getResources().getDimensionPixelOffset(R.dimen.max_heigth_score_v) / 2;
        float diffHeight = dependencyHeight / mMaxDependencyHeight;

        if (diffHeight > 1){
            diffHeight = 1;
        }

        mChildHeight = (int) (minChildHeight + minChildHeight * diffHeight);
        child.setMinimumHeight(mChildHeight);

        return true;
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private int getActionBarHeight() {
        final TypedArray styledAttributes = mContext.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return actionBarHeight;
    }
}