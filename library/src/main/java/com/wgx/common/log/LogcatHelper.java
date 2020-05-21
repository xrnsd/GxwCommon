package com.wgx.common.log;

import java.io.File;

import android.Manifest;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.wgx.common.Permission.PermissionUtil;
import com.wgx.common.R;
import com.wgx.common.base.AdbUtils;
import com.wgx.common.base.CommonUtils;
import com.wgx.common.file.FileUtils;

/**
 * <p>
 * action : APP的log持久化工具<br/>
 * class: LogcatHelper <br/>
 * package: com.wgx.common.log <br/>
 * author: wuguoxian <br/>
 * date: 20200521 <br/>
 * version:V0.1<br/>
 */
public class LogcatHelper {
    private static final String TAG = "LogcatHelper";

    private static final String FILE_NAME_BASE_DEF = "BDMsg";
    private static final String FILE_NAME_END = ".txt";
    private static final String DIR_PATH_KU_LOG = "KuYou";

    private static LogcatHelper INSTANCE = null;
    private static String mDirPathSaveLog = null;

    private LogDumper mLogDumper = null;
    private Context mContext;
    private int mPId;
    private FileUtils mFileUtils;

    private LogcatHelper(Context context) {
        mContext = context;
        mPId = android.os.Process.myPid();
        mFileUtils = FileUtils.getInstance(context);
    }

    public static LogcatHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper(context);
        }
        return INSTANCE;
    }

    /**
     * <p>
     * action : 设定基本的log的相对路径<br/>
     * author: wuguoxian <br/>
     * date: 20200519 <br/>
     * remark:<br/>
     * &nbsp 示例：/xx/yy1/yy2/yy3/zz.aa  <br/>
     * &nbsp 示例说明：xx代表存储位置为工具自动设定，yy们对应dirPath <br/>
     *
     * @param dirPath log的相对路径
     */
    public LogcatHelper setSaveLogDirPath(String dirPath) {
        mDirPathSaveLog = dirPath;
        if (null != mFileUtils)
            mFileUtils.createDirPath(mDirPathSaveLog);
        Log.w(TAG, "setSaveLogDirPath > mDirPathSaveLog=" + mDirPathSaveLog);
        return this;
    }

    public void start() {
        String cmd = null;

        // cmd = "logcat *:e *:w | grep \"(" + mPID + ")\"";
        // cmd = "logcat | grep \"(" + mPID + ")\"";//打印所有日志信息
        // cmd = "logcat -s way";//打印标签过滤信息
        // cmd = "logcat | grep \"(" + mPId + ")\"";
        // cmd = "logcat  -s 12345677777778899";
        cmd = "logcat --pid=" + mPId;

        start(cmd);
    }

    public void start(String cmd) {
        if (Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) <= 0) { //release版本未开usb调试不运行
            Log.d(TAG, "start > cancel");
            return;
        }
        Log.d(TAG, "start");

        if (mLogDumper == null) {
            mLogDumper = new LogDumper(String.valueOf(mPId), cmd, mFileUtils);
        }
        if (PermissionUtil.checkSelfPermission(mContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            mLogDumper.start();
        } else {
            Log.w(TAG, new StringBuilder(mContext.getString(R.string.miss_permissions_config_title))
                    .append(": WRITE_EXTERNAL_STORAGE , READ_EXTERNAL_STORAGE").toString());
        }
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private static class LogDumper extends Thread {
        String cmds = null;
        private String mPID;
        private int autoCreateOldFileCount = -1;

        private FileUtils fileUtils;
        private FileUtils.Flag mRunning = new FileUtils.Flag(true);

        public LogDumper(String pid, String cmd, FileUtils fu) {
            autoCreateOldFileCount = 0;
            mPID = pid;
            if (null == cmd || cmd.length() < 1)
                cmds = "logcat | grep \"(" + mPID + ")\"";
            else
                cmds = cmd;
            fileUtils = fu;
        }

        public void stopLogs() {
            mRunning.setValue(false);
        }

        @Override
        public void run() {
            if (null == fileUtils)
                return;
            final String logFilePath = new StringBuilder(mDirPathSaveLog)
                    .append(File.separator)
                    .append(CommonUtils.formatLocalTimeByMilSecond(System.currentTimeMillis(), "yyyyMMdd_HHmmss")).append(FILE_NAME_END)
                    .toString();
            if (null == fileUtils.createFile(logFilePath))
                return;

            Log.d(TAG, "cmds=" + cmds);
            Process logcatProc = AdbUtils.runCmdByRuntime(cmds);
            if (null != logcatProc) {
                fileUtils.writeLogFromInput(logFilePath, logcatProc.getInputStream(), mRunning);
                logcatProc.destroy();
            }
        }
    }
}