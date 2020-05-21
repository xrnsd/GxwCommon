package com.wgx.common.exception;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.tencent.mmkv.MMKV;
import com.wgx.common.base.AdbUtils;
import com.wgx.common.file.FileUtils;

/**
 * action:全局异常管理器，异常的信息保存和处理<br/>
 * <br/>
 * Package: com.wgx.common.exception<br/>
 * ClassName: UncaughtExceptionManager<br/>
 * created:wgx<br/>
 * date: 2018-12-10 上午9:54:43<br/>
 * version:2.0<br/>
 * 使用示例：<br/>
 * UncaughtExceptionManager.getInstance(new IGlobalExceptionControl(){
 * ...
 * };
 */
public class UncaughtExceptionManager implements UncaughtExceptionHandler {
    private final String TAG = "UncaughtExceptionManager";
    private static UncaughtExceptionManager sMian;

    public static UncaughtExceptionManager getInstance(final IGlobalExceptionControl gec) {
        if (null != sMian)
            return sMian;
        sMian = new UncaughtExceptionManager(gec);
        if (sMian.isHasPolicy(IGlobalExceptionControl.POLICY_DISABLE_GLOBAL_EXCEPTION_CAPTURE))
            return null;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                while (!sMian.addReExceptionTimes()) {
                    try {
                        Looper.loop();//主线程的异常会从这里抛出
                    } catch (Throwable e) {
                        sMian.saveLogInfoByException(gec.getApplication().getApplicationContext(), e);
                    }
                }
            }
        });
        Thread.setDefaultUncaughtExceptionHandler(sMian);
        return sMian;
    }

    private Context mContext;
    private Application mApplication;
    private IGlobalExceptionControl mGlobalExceptionControl;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private FileUtils mFileUtils;
    private int mPolicys;

    private UncaughtExceptionManager(IGlobalExceptionControl gec) {
        mGlobalExceptionControl = gec;
        mApplication = gec.getApplication();
        mContext = mApplication.getApplicationContext();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        mFileUtils = FileUtils.getInstance(mApplication.getApplicationContext());
        initActivityManager(mApplication);
        checkPolicy(gec);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (addReExceptionTimes()) {
            Log.e(TAG, "addReExceptionTimes > uncaughtException exit ==============================");
            Thread.setDefaultUncaughtExceptionHandler(null);
            return;
        }

        if (!isHasPolicy(IGlobalExceptionControl.POLICY_DISABLE_SAVE_LOG)) {
            saveLogInfoByException(mContext, ex);
        }

        if (isHasPolicy(IGlobalExceptionControl.POLICY_ENABLE_CLEAR_DATA)) {
            if (isHasPolicy(IGlobalExceptionControl.POLICY_ENABLE_DEBUG_LOG))
                android.util.Log.d(TAG, "uncaughtException> clear " + mContext.getPackageName());
            AdbUtils.runCmdByProcess("pm", "clear", mContext.getPackageName());
        }

        if (isHasPolicy(IGlobalExceptionControl.POLICY_ENABLE_CRASH_PROMPT)) {
            if (isHasPolicy(IGlobalExceptionControl.POLICY_ENABLE_DEBUG_LOG))
                android.util.Log.d(TAG, "uncaughtException > disiable global exception prompt");
            if (isHasPolicy(IGlobalExceptionControl.POLICY_ENABLE_RESTART_APP)) {
                reStartApplication(1000);
            }
            mDefaultHandler.uncaughtException(thread, ex);
            return;
        } else if (isHasPolicy(IGlobalExceptionControl.POLICY_ENABLE_EXIT_APP)) {
            killMySelf();
        }
    }

    private void checkPolicy(IGlobalExceptionControl gec) {
        mPolicys = gec.getPolicy();

        //重启和退出都存在就去掉退出
        if (isHasPolicy(IGlobalExceptionControl.POLICY_ENABLE_RESTART_APP)
                && isHasPolicy(IGlobalExceptionControl.POLICY_ENABLE_EXIT_APP)) {
            mPolicys &= ~IGlobalExceptionControl.POLICY_ENABLE_EXIT_APP;
        }

        //显示异常时，会自动退出就去掉退出
        if (isHasPolicy(IGlobalExceptionControl.POLICY_ENABLE_CRASH_PROMPT)
                && isHasPolicy(IGlobalExceptionControl.POLICY_ENABLE_EXIT_APP)) {
            mPolicys &= ~IGlobalExceptionControl.POLICY_ENABLE_EXIT_APP;
        }
    }

    private boolean isHasPolicy(final int policy) {
        return (mPolicys & policy) != 0;
    }

    //======================   异常信息保存相关  ===============================

    private String mDirPathSaveExceptionLog;

    /**
     * <p>
     * action : 设定异常log的相对路径<br/>
     * author: wuguoxian <br/>
     * date: 20200519 <br/>
     * remark:<br/>
     * &nbsp 示例：/xx/yy1/yy2/yy3/zz.aa  <br/>
     * &nbsp 示例说明：xx代表存储位置为工具自动设定，yy们对应dirPath <br/>
     *
     * @param dirPath log的相对路径
     */
    public UncaughtExceptionManager setSaveExceptionLogDirPath(String dirPath) {
        mDirPathSaveExceptionLog = dirPath;
        if (null != mFileUtils)
            mFileUtils.createDirPath(mDirPathSaveExceptionLog);
        if (isHasPolicy(IGlobalExceptionControl.POLICY_DISABLE_SAVE_LOG))
            mPolicys &= ~IGlobalExceptionControl.POLICY_DISABLE_SAVE_LOG;

        if (isHasPolicy(IGlobalExceptionControl.POLICY_ENABLE_DEBUG_LOG))
            Log.d(TAG, "setSaveLogDirPath > mDirPathSaveExceptionLog=" + mDirPathSaveExceptionLog);
        return this;
    }

    /**
     * action:保存异常信息到本地<br/>
     * created:created by wgx in 2018-12-8<br/>
     */
    private void saveLogInfoByException(Context context, Throwable ex) {
        if (null == mFileUtils) {
            android.util.Log.d(TAG, "saveLogInfoByException fail > mFileUtils is null");
            return;
        }
        if (isHasPolicy(IGlobalExceptionControl.POLICY_ENABLE_DEBUG_LOG))
            android.util.Log.d(TAG, "saveLogInfoByException");
        if (null == mDirPathSaveExceptionLog) {
            mDirPathSaveExceptionLog = new StringBuilder("CrashInfo")
                    .append(File.separator).append("Log")
                    .toString();
        }
        String fileNameLog = new StringBuilder("exception_")
                .append(getSystemDate()).append(".txt")
                .toString();
        mFileUtils.writeFile(getLogInfoByThrowable(ex).getBytes(), mDirPathSaveExceptionLog, fileNameLog);
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

    //======================   重启APP相关  ===============================

    private static final String KEY_RESTART_TIME = "key.restart.time",
            KEY_RESTART_TIMES = "key.restart.times";
    private MMKV mDataBase;

    private void initUncaughtExceptionManagerDataBase(Context context) {
        if (null != mDataBase)
            return;
        String rootDir = MMKV.initialize(context);
        mDataBase = MMKV.mmkvWithID("UncaughtExceptionManager.database");
    }

    private boolean addReExceptionTimes() {
        initUncaughtExceptionManagerDataBase(mContext);

        final long timeNow = System.currentTimeMillis(),
                timeRestartOld = mDataBase.getLong(KEY_RESTART_TIME, 0);
        int reStartTimesNow = 1;
        //1分钟内连续重启次数
        if (0 != timeRestartOld && timeNow - timeRestartOld < 60 * 1000) {
            reStartTimesNow = mDataBase.getInt(KEY_RESTART_TIMES, 0) + 1;
        }
        if (reStartTimesNow > 3) //用于连续多次崩溃之后，强制退出
            return true;

        Log.d(TAG, "addReExceptionTimes > reStartTimesNow=" + reStartTimesNow);

        MMKV.Editor editor = mDataBase.edit();
        editor.putInt(KEY_RESTART_TIMES, reStartTimesNow);
        editor.putLong(KEY_RESTART_TIME, timeNow);

        return false;
    }

    /**
     * action:重启模块<br/>
     * created:created by wgx in 2018-12-8<br/>
     */
    public void reStartApplication(final int delayMillis) {
        if (isHasPolicy(IGlobalExceptionControl.POLICY_ENABLE_DEBUG_LOG))
            android.util.Log.d(TAG, "reStartApplication");
        try {
            AlarmManager mgr = (AlarmManager) mApplication.getSystemService(Context.ALARM_SERVICE);
            Intent intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent restartIntent = PendingIntent.getActivity(mApplication, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + delayMillis, restartIntent);
        } catch (Exception e) {
            Log.e(TAG, android.util.Log.getStackTraceString(e));
        }
    }

    // ==================  退出APP相关 =============================
    AppExitTool mAppExitTool;

    private void initActivityManager(Application app) {
        mAppExitTool = new AppExitTool();
        app.registerActivityLifecycleCallbacks(mAppExitTool);
    }

    /**
     * action:关闭模块<br/>
     * created:created by wgx in 2018-12-8<br/>
     */
    public void killMySelf() {
        if (isHasPolicy(IGlobalExceptionControl.POLICY_ENABLE_DEBUG_LOG))
            android.util.Log.d(TAG, "killMySelf");
        //android.os.Process.killProcess(android.os.Process.myPid());
        mAppExitTool.exitApp();
    }

    public class AppExitTool implements Application.ActivityLifecycleCallbacks {
        private ArrayList<WeakReference<Activity>> sActivity = new ArrayList();

        private void add(Activity activity) {
            if (activity != null) {
                WeakReference<Activity> activityWeakReference = new WeakReference<>(activity);
                sActivity.add(activityWeakReference);
            }
        }

        private void remove(Activity activity) {
            for (int i = 0, size = sActivity.size(); i < size; i++) {
                WeakReference<Activity> activityWeakReference = sActivity.get(i);
                Activity innerActivity = activityWeakReference.get();
                if (innerActivity == activity) {
                    sActivity.remove(activityWeakReference);
                    break;
                }
            }
        }

        /**
         * finish掉所有Activity
         */
        public void exitApp() {
            WeakReference<Activity> activityWeakReference;
            Activity activity;
            for (int i = 0, size = sActivity.size(); i < size; i++) {
                activityWeakReference = sActivity.get(i);
                activity = activityWeakReference.get();
                if (null != activity && !activity.isFinishing()) {
                    activity.finish();
                }
            }
            if (sActivity != null && sActivity.size() > 0) {
                sActivity.clear();
            }
        }


        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            //activity  create时，添加activity
            add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            //activity  start时，可以做一些操作，例如记录此activity回到前台的时间等

        }

        @Override
        public void onActivityResumed(final Activity activity) {
            //activity  resume时，可以做一些操作，例如让一些后台任务重新开启，或者app切换到前台的时间等
        }

        @Override
        public void onActivityPaused(Activity activity) {
            //activity  pause时，可以做一些操作，例如暂停一些后台任务
        }

        @Override
        public void onActivityStopped(Activity activity) {
            //activity  stop时，可以做一些操作，例如记录app切换到后台的时间等
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            //保存状态
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            //activity  destroy时，移除activity
            remove(activity);
        }
    }


}
