package com.softdesign.devintensive.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    GestureDetector mGestureDetector;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                UiHelper.writeLog("onSingleTapUp");
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                UiHelper.writeLog("onLongPress");
                super.onLongPress(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                UiHelper.writeLog("onScroll");
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                UiHelper.writeLog("onFling");
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public void onShowPress(MotionEvent e) {
                UiHelper.writeLog("onShowPress");
                super.onShowPress(e);
            }

            @Override
            public boolean onDown(MotionEvent e) {
                UiHelper.writeLog("onDown");
                return super.onDown(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                UiHelper.writeLog("onDoubleTap");
                return super.onDoubleTap(e);
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                UiHelper.writeLog("onDoubleTapEvent");
                return super.onDoubleTapEvent(e);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                UiHelper.writeLog("onSingleTapConfirmed");
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onContextClick(MotionEvent e) {
                UiHelper.writeLog("onContextClick");
                return super.onContextClick(e);
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
        }
        //UiHelper.writeLog("onInterceptTouchEvent");
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        UiHelper.writeLog("onTouchEvent");
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        //UiHelper.writeLog("onRequestDisallowInterceptTouchEvent");

    }
}