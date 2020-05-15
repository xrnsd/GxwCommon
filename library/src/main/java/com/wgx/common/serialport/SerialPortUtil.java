package com.wgx.common.serialport;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 串口操作类
 *
 * @author Jerome
 */
public class SerialPortUtil {
    protected final String TAG = this.getClass().getSimpleName();

    private SerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;
    private ReadThread mReadThread;
    private String path = "/dev/ttyMT1";
    private int baudrate = 115200;
    private static SerialPortUtil portUtil;
    private OnDataReceiveListener onDataReceiveListener = null;
    private boolean isStop = false;

    public interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer, int size);
    }

    public String getSerialPortPath() {
        return path;
    }

    public int getSerialPortBaudrate() {
        return baudrate;
    }

    public void setOnDataReceiveListener(
            OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }

    public static SerialPortUtil getInstance(String path, int baudrate) {
        if (null != portUtil
                && null != path
                && !portUtil.path.equals(path)) {
            SerialPortUtil serialPortUtil = new SerialPortUtil();
            serialPortUtil.path = path;
            serialPortUtil.baudrate = baudrate;
            serialPortUtil.onCreate();
            return serialPortUtil;
        }
        if (null == portUtil) {
            portUtil = new SerialPortUtil();
            portUtil.path = path;
            portUtil.baudrate = baudrate;
            portUtil.onCreate();
        }
        return portUtil;
    }

    public static SerialPortUtil getInstance() {
        if (null == portUtil) {
            portUtil = new SerialPortUtil();
            portUtil.onCreate();
        }
        return portUtil;
    }

    /**
     * 初始化串口信息
     */
    public void onCreate() {
        try {
            mSerialPort = new SerialPort(new File(path), baudrate, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            mReadThread = new ReadThread();
            isStop = false;
            mReadThread.start();
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        //initBle();
    }

    /**
     * 发送指令到串口
     *
     * @param cmd
     * @return
     */
    public boolean send(String data) {
        boolean result = true;
        //byte[] mBuffer = (cmd+"\r\n").getBytes();
        byte[] mBuffer = hextoBytes(data);
        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBuffer);
            } else {
                result = false;
            }
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            result = false;
        }
        return result;
    }

    /**
     * 发送指令到串口
     *
     * @param cmd
     * @return
     */
    public boolean sendCmds(byte[] cmdByte) {
        boolean result = true;

        byte[] CFGDYNC_BYTE = new byte[cmdByte.length + 2];
        System.arraycopy(cmdByte, 0, CFGDYNC_BYTE, 0, cmdByte.length);
        CFGDYNC_BYTE[cmdByte.length] = 0x0D;
        CFGDYNC_BYTE[cmdByte.length + 1] = 0x0A;

        try {
            if (mOutputStream != null) {
                mOutputStream.write(CFGDYNC_BYTE);
            } else {
                result = false;
            }
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            result = false;
        }
        return result;
    }

    public boolean sendBuffer(byte[] mBuffer) {
        boolean result = true;
        String tail = "\r\n";
        byte[] tailBuffer = tail.getBytes();
        byte[] mBufferTemp = new byte[mBuffer.length + tailBuffer.length];
        System.arraycopy(mBuffer, 0, mBufferTemp, 0, mBuffer.length);
        System.arraycopy(tailBuffer, 0, mBufferTemp, mBuffer.length, tailBuffer.length);

        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBufferTemp);
            } else {
                result = false;
            }
        } catch (IOException e) {
            Log.e(TAG, Log.getStackTraceString(e));
            result = false;
        }
        return result;
    }

    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                while (!isStop && !isInterrupted()) {
                    int size;
                    Log.e(TAG, "read data");
                    if (mInputStream == null) {
                        Log.e(TAG, "mInputStream is null");
                        return;
                    }
                    byte[] buffer = new byte[15];
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        if (null != onDataReceiveListener) {
                            onDataReceiveListener.onDataReceive(buffer, size);
                        } else {
                            Log.e(TAG, "onDataReceiveListener is null");
                        }
                    } else {
                        Log.e(TAG, "mInputStream`s data is null");
                    }
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
                return;
            }
        }
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        //sendShellCommond1();
        isStop = true;
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        if (mSerialPort != null) {
            mSerialPort.close();
        }
    }

    // ============================  扩展部分  ==========================================
    private FileOutputStream mDevicePowerOutputStream;

    private boolean writeInternalAntennaDevice(final int value, final String dev_path) {
        synchronized (new Object()) {
            BufferedOutputStream bos = null;
            File gpsAntSwitch = new File(dev_path);
            byte[] buffer = new byte[2];
            try {
                mDevicePowerOutputStream = new FileOutputStream(gpsAntSwitch);
                bos = new BufferedOutputStream(mDevicePowerOutputStream, buffer.length);
            } catch (FileNotFoundException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            try {
                if (value == 1) {
                    buffer[0] = '1';
                } else {
                    buffer[0] = '0';
                }
                if (null != bos) {
                    bos.write(buffer, 0, 1);
                    bos.flush();
                    bos.close();
                    Log.d(TAG, "write val success");
                }
                if (null != mDevicePowerOutputStream) {
                    mDevicePowerOutputStream.close();
                }
                return true;
            } catch (IOException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return false;
    }

    /**
     * <p>
     * action : 设备上电<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     * </p>
     */
    public void openDevice(String path) {
        boolean result = writeInternalAntennaDevice(1, path);
        Log.d(TAG, "openDevice " + (result ? "success" : "fail"));
    }

    /**
     * <p>
     * action : 设备下电<br/>
     * author: wuguoxian <br/>
     * date: 20200514 <br/>
     * </p>
     */
    public void closeDevice(String path) {
        boolean result = writeInternalAntennaDevice(0, path);
        Log.d(TAG, "closeDevice " + (result ? "success" : "fail"));
    }

    public static String byte2hex(byte[] buffer) {
        String h = "";

        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + " " + temp;
        }
        return h;
    }

    public static String string2HexString(String s) {
        String r = bytes2HexString(string2Bytes(s));
        return r;
    }

    /*
     * 字符串转字节数组
     */
    public static byte[] string2Bytes(String s) {
        byte[] r = s.getBytes();
        return r;
    }

    /*
     * 字节数组转16进制字符串
     */
    public static String bytes2HexString(byte[] b) {
        String r = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }
        return r;
    }

    public static byte[] hextoBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    public static byte fletcherChecksum(byte[] data) {
        byte Xor = 0;
        for (byte d : data) {
            Xor = (byte) (Xor ^ d);
        }
        return (byte) (0xFF & Xor);
    }
}
