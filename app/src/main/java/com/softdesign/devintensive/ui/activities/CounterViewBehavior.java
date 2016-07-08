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

        return true;
    }

}