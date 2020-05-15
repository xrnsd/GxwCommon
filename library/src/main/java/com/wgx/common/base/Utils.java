package com.wgx.common.base;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
import com.wgx.common.base.CommonDialog;

public class Utils {
    private static final String TAG = "123456 Utils";
    public final static boolean DEBUG = Log.isLoggable(TAG, Log.DEBUG);

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        int height = resources.getDimensionPixelSize(resourceId);
        android.util.Log.d("123456 Launcher.getStatusBarHeight>", " statusBarHeight =" + height);
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

    public static void runCommand(final String... command) {
        android.util.Log.d(TAG + "runCommand: ", " " + command);

        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        String result = "";

        try {
            process = new ProcessBuilder().command(command).start();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            result = new String(baos.toByteArray());
            if (inIs != null)
                inIs.close();
            if (errIs != null)
                errIs.close();
            process.destroy();
        } catch (IOException e) {
            Log.e(TAG + "runCommand: ", "IOException:" + e);
            result = e.getMessage();
        }
        Log.d(TAG + "runCommand: ", "result=" + result);
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

    private static CommonDialog commonDialog;

    public static void showSureDialog(Activity context, DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel, String... contentList) {
        if (context.isFinishing()) {
            if (null != commonDialog)
                commonDialog.dismiss();
            commonDialog = null;
            return;
        }
        if (null != commonDialog) {
            if (commonDialog.getActivityContext().isFinishing()) {
                commonDialog = null;
            } else if (commonDialog.getActivityContext() != context) {
                commonDialog = null;
            } else if (null != commonDialog.getMessage()
                    && null != contentList[0]
                    && !commonDialog.getMessage().equals(contentList[0])) {
                commonDialog = null;
            }
        }
        if (null == commonDialog) {
            if (null != contentList
                    && contentList.length > 1
                    && null != contentList[0]
                    && null != contentList[1])
                commonDialog = new CommonDialog.Builder(context)
                        .isVertical(false).setTitle(contentList[0])
                        .setLeftButton(R.string.cancel, cancel)
                        .setTitle(R.string.tips_title)
                        .setMessage(contentList[1])
                        .setRightButton(context.getString(R.string.sure), ok)
                        .create();
            commonDialog.setContext(context);
        }
        try {
            commonDialog.show();
        } catch (WindowManager.BadTokenException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            commonDialog = null;
        }
    }

}
