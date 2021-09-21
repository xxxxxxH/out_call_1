package net.entity;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Copyright (C) 2021,2021/7/28, a Tencent company. All rights reserved.
 * <p>
 * User : v_xhangxie
 * <p>
 * Desc :
 */
public class ScreenUtils {
    public static int[] getScreenSize(Activity activity){
        int[] result = new int[2];
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        result[0] = height;
        result[1] = width;
        return result;
    }
}
