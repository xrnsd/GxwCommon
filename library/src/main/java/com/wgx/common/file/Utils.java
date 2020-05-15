package com.wgx.common.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

public class Utils {

    public static String readFileByPath(String filenPath) throws IOException {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            android.util.Log.d("123456 UncaughtExceptionHandlerLz>readFromSD> ", " fail  ");
            return "";
        }
        StringBuilder sb = new StringBuilder("");
        FileInputStream input = new FileInputStream(filenPath);
        byte[] temp = new byte[1024];

        int len = 0;
        while ((len = input.read(temp)) > 0) {
            sb.append(new String(temp, 0, len));
        }
        input.close();
        android.util.Log.d("123456 UncaughtExceptionHandlerLz>readFromSD> ", " finish  ");
        return sb.toString();
    }

    public static void writeFileByPath(Context context, String filenPath, String filecontent) throws Exception {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            android.util.Log.d("123456 UncaughtExceptionHandlerLz>savaFileToSD> ", " fail  ");
            return;
        }
        FileOutputStream output = new FileOutputStream(filenPath);
        output.write(filecontent.getBytes());
        output.close();
        android.util.Log.d("123456 UncaughtExceptionHandlerLz>writeFile> ", " finish  ");
    }

}
