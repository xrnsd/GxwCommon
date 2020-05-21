package com.wgx.common.base;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AdbUtils {
    private static final String TAG = "AdbUtils";

    public static Process runCmdByRuntime(final String cmd){
        Process proc=null;
        try {
            proc = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return proc;
    }

    public static void runCmdByProcess(final String... cmds) {
        Log.d(TAG, "runCmdByProcess > " + cmds);

        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        String result = "";

        try {
            process = new ProcessBuilder().command(cmds).start();
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
            android.util.Log.e(TAG, android.util.Log.getStackTraceString(e));
            result = e.getMessage();
        }
        Log.d(TAG, "runCmdByProcess > result=" + result);
    }
}
