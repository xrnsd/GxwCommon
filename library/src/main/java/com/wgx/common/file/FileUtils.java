package com.wgx.common.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

/**
 * <p>
 * action : 文件操作封装工具<br/>
 * class: FileUtils <br/>
 * package: com.wgx.common.file <br/>
 * author: wuguoxian <br/>
 * date: 20200521 <br/>
 * version:V0.2<br/>
 */
public class FileUtils {
    private static final String TAG = "FileUtils";
    private List<File> mFileCacheList = new ArrayList<>();

    private String SDCardRoot;

    /**
     * ----------------注意权限的添加----------------
     */
    private FileUtils(Context context) {
        SDCardRoot = getSdRootDirectory(context);
    }

    public static FileUtils getInstance(Context context) {
        if (!com.wgx.common.Permission.PermissionUtil.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            com.wgx.common.Permission.PermissionUtil.
                    showToastMissPermissionsAndManually(context,
                            "Manifest.permission.READ_EXTERNAL_STORAGE",
                            "Manifest.permission.WRITE_EXTERNAL_STORAGE");
            return null;
        }
        return new FileUtils(context.getApplicationContext());
    }

    private String getSdRootDirectory(Context context) {
        String dirPath = context.getFilesDir().getAbsolutePath();//本应用目录
        //外置存储的路径
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        Log.d(TAG, "getSdRootDirectory > dirPath=" + dirPath);
        return dirPath;
    }

    /**
     * 创建文件
     *
     * @param fileRelativePath 文件相对路径
     */
    public File createFile(String... items) {
        return createFile(getFileByRelativePath(items));

    }

    /**
     * 创建文件
     */
    public File createFile(File file) {
        File dir = new File(file.getParent());
        try {
            if (!dir.exists())
                dir.mkdirs();
            if (null != file && !file.exists())
                file.createNewFile();
            if (null != file && !file.exists()) {
                Log.e(TAG, "createFile fail : " + file.getPath() + " is create fail");
                return null;
            }
            return file;
        } catch (FileNotFoundException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dir 目录路径，相当于文件夹
     * @return
     */
    public File createDirPath(String dir) {
        File dirFile = getFileByRelativePath(dir);
        if (null != dirFile && !dirFile.exists()) {
            Log.d(TAG, "createDirPath > create new dirs");
            dirFile.mkdirs();
        } else
            Log.d(TAG, "createDirPath > is no exists");
        return dirFile;
    }

    /**
     * 根据相对路径创建File，不需要关心具体存放位置
     *
     * @param items 路径列表，方法会自动拼接成一个路径
     * @return 一个file对象，路径为：以SDCardRoot为开头加上items
     */
    public File getFileByRelativePath(String... items) {
        if (null == items
                || items.length < 1)
            return null;
        StringBuilder pathBuilder = new StringBuilder(SDCardRoot).append(File.separator);
        for (String item : items) {
            pathBuilder.append(item).append(File.separator);
        }
        synchronized (mFileCacheList) {
            String path = pathBuilder.toString();
            for (File file : mFileCacheList) {
                if (null != file && file.getPath().equals(path))
                    return file;
            }
            File file = new File(path);
            mFileCacheList.add(file);
            return file;
        }
    }

    /**
     * 判断SD卡上的文件夹是否存在
     *
     * @param fileRelativePath 文件相对路径
     * @return
     */
    public boolean isFileExist(String fileRelativePath) {
        File file = getFileByRelativePath(fileRelativePath);
        return null != file && file.exists();
    }

    /**
     * 将一个字节数组数据写入到文件中
     *
     * @param items 文件相对路径
     * @param bytes 待写入的数据
     * @return 是否写入成功
     */
    public boolean writeFile(byte[] bytes, String... items) {
        if (bytes == null) {
            return false;
        }
        OutputStream outputStream = null;
        try {
            File file = createFile(items);
            if (null == file) {
                return false;
            }
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new BufferedOutputStream(new FileOutputStream(
                    file));
            outputStream.write(bytes);
            outputStream.flush();
            return true;
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }
        return false;
    }

    /**
     * 将一个InputStream里面的数据写入到文件中
     *
     * @param fileRelativePath 文件相对路径
     * @param input            待写入的流
     * @return 是否写入成功
     */
    public boolean writeFromInput(String fileRelativePath, InputStream input) {
        OutputStream output = null;
        try {
            File file = createFile(fileRelativePath);
            if (null == file) {
                return false;
            }
            output = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            int temp;
            while ((temp = input.read(buffer)) != -1) {
                output.write(buffer, 0, temp);
            }
            output.flush();
            return true;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return false;
    }

    public static class Flag {
        public Flag(boolean val) {
            setValue(val);
        }

        private boolean value;

        public boolean getValue() {
            return value;
        }

        public void setValue(boolean val) {
            value = val;
        }
    }

    public boolean writeFromInput(String fileRelativePath, InputStream input, Flag flag) {
        int autoCreateOldFileCount = -1;
        BufferedReader reader = null;
        FileOutputStream out = null;
        try {
            File currentFile = createFile(fileRelativePath);
            if (null == currentFile) {
                return false;
            }
            out = new FileOutputStream(currentFile);
            reader = new BufferedReader(new InputStreamReader(input), 1024);
            String line = null;
            while (flag.getValue() && (line = reader.readLine()) != null) {
                if (!flag.getValue()) {
                    break;
                }
                if (line.length() == 0) {
                    continue;
                }
                if (!currentFile.exists()
                        && autoCreateOldFileCount < 5) {
                    currentFile.createNewFile();
                    out = new FileOutputStream(currentFile);
                    autoCreateOldFileCount += 1;
                }
                if (out != null) {
                    out.write((line + "\n").getBytes());
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
                out = null;
            }
        }
        return false;
    }

    public boolean writeLogFromInput(String fileRelativePath, InputStream input, Flag flag) {
        int autoCreateOldFileCount = -1;
        BufferedReader reader = null;
        FileOutputStream out = null;
        try {
            File currentFile = createFile(fileRelativePath);
            if (null == currentFile) {
                return false;
            }
            out = new FileOutputStream(currentFile);
            reader = new BufferedReader(new InputStreamReader(input), 1024);
            String line = null;
            int lineCount = 0;
            while (flag.getValue() && (line = reader.readLine()) != null) {
                if (!flag.getValue()) {
                    break;
                }
                if (line.length() == 0) {
                    continue;
                }
                if (!currentFile.exists()
                        && autoCreateOldFileCount < 5) {
                    currentFile.createNewFile();
                    out = new FileOutputStream(currentFile);
                    autoCreateOldFileCount += 1;
                }
                if (out != null) {
                    out.write((line + "\n").getBytes());
                }
                lineCount += 1;
                if (lineCount > 5000) { //log文件大小控制
                    String filePath = currentFile.getPath();
                    String filrPathBase = filePath.substring(0, filePath.lastIndexOf("."));
                    String filrPathSuffix = filePath.substring(filePath.lastIndexOf(".") + 1);
                    currentFile = createFile(new StringBuilder(filrPathBase).append("_e.").append(filrPathSuffix).toString());
                    if (null == currentFile) {
                        return false;
                    }
                    out = new FileOutputStream(currentFile);
                    reader = new BufferedReader(new InputStreamReader(input), 1024);
                    line = null;
                    lineCount = 0;
                }
            }
            return true;
        } catch (FileNotFoundException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    reader = null;
                } catch (IOException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
                out = null;
            }
        }
        return false;
    }

    /**
     * 读取文件
     *
     * @param fileRelativePath 文件相对路径
     * @return 文件的数据
     */
    public String readData(String fileRelativePath) {
        File file = getFileByRelativePath(fileRelativePath);
        if (null == file || !file.exists()) {
            return null;
        }
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            return new String(data);
        } catch (FileNotFoundException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        }
        return null;
    }

    /**
     * 使用FileWriter在文件末尾添加内容
     *
     * @param fileRelativePath 文件相对路径
     * @param content          追加数据
     */
    public void appendContent(String fileRelativePath, String content) {
        OutputStream outputStream = null;
        try {
            File file = getFileByRelativePath(fileRelativePath);
            outputStream = new FileOutputStream(file, true);
            byte[] enter = new byte[2];
            enter[0] = 0x0d;
            enter[1] = 0x0a;// 用于输入换行符的字节码
            String finalString = new String(enter);// 将该字节码转化为字符串类型
            content = content + finalString;
            outputStream.write(content.getBytes());
            outputStream.flush();
        } catch (FileNotFoundException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }

    }
}