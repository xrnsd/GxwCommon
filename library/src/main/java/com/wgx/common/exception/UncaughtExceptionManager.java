package com.wgx.common.exception;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.wgx.common.base.Utils;

/**
 * action:全局异常管理器，异常的信息保存和处理<br/>
 * <br/>
 * Package: com.wgx.common.exception<br/>
 * ClassName: UncaughtExceptionManager<br/>
 * created:wgx<br/>
 * date: 2018-12-10 上午9:54:43<br/>
 * version:1.0<br/>
 * 使用示例：<br/>
 * UncaughtExceptionManager.initInstance(new IGlobalExceptionControl(){
 * ...
 * };
 */
public class UncaughtExceptionManager implements UncaughtExceptionHandler {
    private final String TAG = "123456 UncaughtExceptionManager";
    private final String DIR_PATH_SDCARD = "/storage/sdcard0/lzlog";

    private Context mContext;
    private Application mApplication;
    private IGlobalExceptionControl mGlobalExceptionControl;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public static void initInstance(IGlobalExceptionControl gec) {
        if ((gec.getPolicys() & IGlobalExceptionControl.POLICY_PRACTICE_GLOBAL_EXCEPTION_CAPTURE) == 0)
            return;
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionManager(gec));
    }

    private UncaughtExceptionManager(IGlobalExceptionControl gec) {
        mGlobalExceptionControl = gec;
        mApplication = gec.getApplication();
        mContext = mApplication.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if (isEnableAutoPersistence()) {
            saveLogInfoByException(mContext,DIR_PATH_SDCARD,ex);
        }

        if (!isDisiableGlobalExceptionPrompt()) {
            if (isDebug())
                android.util.Log.d(TAG+">", "uncaughtException>disiable global exception prompt");
            mDefaultHandler.uncaughtException(thread, ex);
        }
        if (isEnableAutoReset()) {
            if (isDebug())
                android.util.Log.d(TAG+">uncaughtException>", "clear " + mContext.getPackageName());
            Utils.runCommand("pm", "clear", mContext.getPackageName());
        }
        if (isEnableReStart()) {
            reStart(1000);
        } else if (isEnablekillMySelf() && !isEnableAutoReset()) {
            killMySelf();
        }
    }

    /**
     * action:是否打印调试log<br/>
     * remark:<br/>
     * @param
     * @return boolean<br/>
     * created:created by wgx in 2019-4-18<br/>
     */
    private boolean isDebug() {
        return (mGlobalExceptionControl.getPolicys() & IGlobalExceptionControl.POLICY_DEBUG_LOG) != 0;
    }

    /**
     * action:是否屏蔽停止工作等异常提示<br/>
     * created:created by wgx in 2018-12-8<br/>
     * remark:<br/>
     * 
     * @param
     * @return boolean<br/>
     */
    public boolean isDisiableGlobalExceptionPrompt() {
        final int policys = mGlobalExceptionControl.getPolicys();
        return (policys & IGlobalExceptionControl.POLICY_PRACTICE_GLOBAL_EXCEPTION_PROMPT) != 0;
    }

    /**
     * action:是否清理模块数据<br/>
     * created:created by wgx in 2018-12-8<br/>
     * remark:<br/>
     * 
     * @param
     * @return boolean<br/>
     */
    public boolean isEnableAutoReset() {
        final int policys = mGlobalExceptionControl.getPolicys();
        return (policys & IGlobalExceptionControl.POLICY_CLEAR_TAIL_CLEAR) != 0;
    }

    /**
     * action:是否保存异常信息<br/>
     * created:created by wgx in 2018-12-8<br/>
     * remark:<br/>
     * 
     * @param
     * @return boolean<br/>
     */
    public boolean isEnableAutoPersistence() {
        final int policys = mGlobalExceptionControl.getPolicys();
        return (policys & IGlobalExceptionControl.POLICY_CLEAR_TAIL_AUTO_PERSISTENCE) != 0;
    }

    /**
     * action:是否重启模块<br/>
     * created:created by wgx in 2018-12-8<br/>
     * remark:<br/>
     * 
     * @param
     * @return boolean<br/>
     */
    public boolean isEnableReStart() {
        final int policys = mGlobalExceptionControl.getPolicys();
        return (policys & IGlobalExceptionControl.POLICY_CLEAR_TAIL_RESTART) != 0;
    }

    /**
     * action:是否关闭模块<br/>
     * created:created by wgx in 2018-12-8<br/>
     */
    public boolean isEnablekillMySelf() {
        final int policys = mGlobalExceptionControl.getPolicys();
        return (policys & IGlobalExceptionControl.POLICY_CLEAR_TAIL_KILLMYSELF) != 0;
    }

    /**
     * action:关闭模块<br/>
     * created:created by wgx in 2018-12-8<br/>
     */
    public void killMySelf() {
        if (isDebug()) android.util.Log.d(TAG, " > killMySelf > ");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * action:重启模块<br/>
     * created:created by wgx in 2018-12-8<br/>
     */
    public void reStart(final int delayMillis) {
        if (isDebug()) android.util.Log.d(TAG," > reStart > "+"   ");
        AlarmManager mgr = (AlarmManager) mApplication.getSystemService(Context.ALARM_SERVICE);
        try {
            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent restartIntent = PendingIntent.getActivity(mApplication, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + delayMillis, restartIntent);
        } catch (Exception e) {
            android.util.Log.d(TAG," > reStart > "+e.toString());
        }
    }

    /**
     * action:保存异常信息到本地<br/>
     * created:created by wgx in 2018-12-8<br/>
     */
    private void saveLogInfoByException(Context context ,String path,Throwable ex) {
        if (isDebug())
            android.util.Log.d(TAG+">", "saveLogInfoByException");
        File dirPath = new File(path);
        if (!dirPath.exists())
            dirPath.mkdir();
        if (!dirPath.exists()) {
            if (isDebug()) android.util.Log.d(TAG, "> saveLogInfoByException >  log目录无法创建：" + path);
            return;
        }
        String fileNameLog = "exception_" + getSystemDate() + ".log";
        String filePathLog = path + "/" + fileNameLog;
        String logContent = getLogInfoByThrowable(ex);
        try {
            com.wgx.common.file.Utils.writeFileByPath(context, filePathLog, logContent);
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.d(TAG, " > saveLogInfoByException > " + e.toString());
        }
    }

    private String getSystemDate() {
        final String format = "yyyyMMdd_HHmmss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    private String getLogInfoByThrowable(Throwable paramThrowable) {
        StringWriter localStringWriter = new StringWriter();
        PrintWriter localPrintWriter = new PrintWriter(localStringWriter);
        paramThrowable.printStackTrace(localPrintWriter);
        for (Throwable localThrowable = paramThrowable.getCause(); localThrowable != null; localThrowable = localThrowable.getCause()) {
            localThrowable.printStackTrace(localPrintWriter);
        }
        String str = localStringWriter.toString();
        localPrintWriter.close();
        return str;
    }
}
