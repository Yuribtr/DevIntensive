package com.softdesign.devintensive.utils;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.support.design.BuildConfig;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Array;

public class UiHelper {
    private static Context mContext = DevintensiveApplication.getContext();
    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static int getStatusBarHeight(){
        int result = 0;
        try {
            int resourceID = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceID > 0) {
                result = mContext.getResources().getDimensionPixelSize(resourceID);
            }
        } catch (Exception e){

        }
        return result;
    }

    public static int getActionBarHeight(){
        int result = 0;
        TypedValue tv = new TypedValue();
        if (mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            result=TypedValue.complexToDimensionPixelOffset(tv.data, mContext.getResources().getDisplayMetrics());
        }
        return result;
    }

    public static int lerp (int start, int end, float friction){
        return (int) (start+(end-start)*friction);
    }

    public static float currentFriction (int start, int end, int currentValue){
        return (float) (currentValue-start)/(end-start);
    }

    public static void writeLog(String message){
        if (ConstantManager.DEBUG_MODE) {
            Log.d(ConstantManager.TAG_PREFIX, message);
        }
    }

    public static void writeLog(String tag, String message){
        if (ConstantManager.DEBUG_MODE) {
            Log.d(ConstantManager.TAG_PREFIX + tag, message);
//            if (Thread.currentThread().getStackTrace()!=null && Thread.currentThread().getStackTrace().length>14){
//                Log.d(ConstantManager.TAG_PREFIX, Thread.currentThread().getStackTrace()[13].getMethodName()+" "+ message);
//            } else {
//                Log.d(ConstantManager.TAG_PREFIX, message);
//            }
        }
    }

}
