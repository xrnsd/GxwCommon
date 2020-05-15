package com.wgx.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;

public class Utils {
    private static final String TAG = "Utils";
    public final static boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        android.util.Log.d("123456 Launcher.getStatusBarHeight>"," statusBarHeight ="+height);
        return height;
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if(drawable==null)
            return null;
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        if(bitmap==null)
            return null;
        
        return new BitmapDrawable(bitmap);
    }

    public static int str2Color(String color) {
        if(color==null)
            return -1;
        return Color.parseColor(color);
    }

    public static Drawable color2Drawable(String color) {
        if(color==null)
            return null;
        return color2Drawable(Color.parseColor(color));
    }
    
    public static Drawable color2Drawable(int color) {
        if(color<=1)
            return null;
        return new ColorDrawable(color).getCurrent();
    }

   public static int dip2px(Context context, int dipValue) {
         return dip2px(context,(float)dipValue);
    }
    
   public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
   }

   public static int[] getScreenInfo(Context context) {
       Resources resources = context.getResources();
       DisplayMetrics dm = resources.getDisplayMetrics();
    return new int[]{(int)dm.widthPixels,(int)dm.heightPixels};
   }

}
