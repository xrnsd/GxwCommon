package com.wgx.app;

import android.app.Application;

import com.wgx.common.exception.IGlobalExceptionControl;
import com.wgx.common.exception.UncaughtExceptionManager;
import com.wgx.common.file.FileUtils;
import com.wgx.common.log.LogcatHelper;

import java.io.File;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        LogcatHelper.getInstance(this)
                .setSaveLogDirPath(new StringBuilder()
                        .append("KuYou").append(File.separator)
                        .append("GxwCommonTestDemo").append(File.separator)
                        .append("Log")
                        .toString())
                .start();

        UncaughtExceptionManager
                .getInstance(new IGlobalExceptionControl() {
                            @Override
                            public Application getApplication() {
                                return BaseApplication.this;
                            }

                            @Override
                            public int getPolicy() {
                                int policy = 0;
                                policy |= IGlobalExceptionControl.POLICY_ENABLE_DEBUG_LOG;
                                policy |= IGlobalExceptionControl.POLICY_ENABLE_CRASH_PROMPT;
                                return policy;
                            }
                        })
                .setSaveExceptionLogDirPath(new StringBuilder()
                        .append("KuYou").append(File.separator)
                        .append("GxwCommonTestDemo").append(File.separator)
                        .append("ExceptionLog")
                        .toString());
    }
}
