package com.wgx.common.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.wgx.common.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtils {

    //protected final String TAG = this.getClass().getSimpleName();
    private static final String TAG = "CommonUtils";
    public final static boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);


    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        Log.d("123456 Launcher.getStatusBarHeight>", " statusBarHeight =" + height);
        return height;
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null)
            return null;
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        if (bitmap == null)
            return null;

        return new BitmapDrawable(bitmap);
    }

    public static int str2Color(String color) {
        if (color == null)
            return -1;
        return Color.parseColor(color);
    }

    public static Drawable color2Drawable(String color) {
        if (color == null)
            return null;
        return color2Drawable(Color.parseColor(color));
    }

    public static Drawable color2Drawable(int color) {
        if (color <= 1)
            return null;
        return new ColorDrawable(color).getCurrent();
    }

    public static int dip2px(Context context, int dipValue) {
        return dip2px(context, (float) dipValue);
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int[] getScreenInfo(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return new int[]{(int) dm.widthPixels, (int) dm.heightPixels};
    }

    /**
     * action: 时间戳转UTC时间 <br/>
     */
    public static String formatUTCTimeByMilSecond(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * action: 时间戳转设备本地时间 <br/>
     */
    public static String formatLocalTimeByMilSecond(long timeInMillis, String pattern) {
        if (0 < timeInMillis)
            Calendar.getInstance().setTimeInMillis(timeInMillis);
        return new SimpleDateFormat(pattern).format(Calendar.getInstance().getTime());
    }

    private static BaseDialog sBaseDialog;

    public static void showDialog(Activity context, DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel, String... contentList) {
        if (context.isFinishing()) {
            if (null != sBaseDialog)
                sBaseDialog.dismiss();
            sBaseDialog = null;
            return;
        }
        if (null != sBaseDialog) {
            if (sBaseDialog.getActivityContext().isFinishing()) {
                sBaseDialog = null;
            } else if (sBaseDialog.getActivityContext() != context) {
                sBaseDialog = null;
            } else if (null != sBaseDialog.getMessage()
                    && null != contentList[0]
                    && !sBaseDialog.getMessage().equals(contentList[0])) {
                sBaseDialog = null;
            }
        }
        if (null == sBaseDialog) {
            if (null != contentList
                    && contentList.length > 1
                    && null != contentList[0]
                    && null != contentList[1])
                sBaseDialog = new BaseDialog.Builder(context)
                        .isVertical(false).setTitle(contentList[0])
                        .setLeftButton(R.string.cancel, cancel)
                        .setTitle(R.string.tips_title)
                        .setMessage(contentList[1])
                        .setRightButton(context.getString(R.string.sure), ok)
                        .create();
            sBaseDialog.setContext(context);
        }
        try {
            sBaseDialog.show();
        } catch (WindowManager.BadTokenException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            sBaseDialog = null;
        }
    }

}
