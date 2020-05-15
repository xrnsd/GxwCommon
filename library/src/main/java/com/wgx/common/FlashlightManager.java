package com.wgx.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class FlashlightManager {

    private static final String TAG = "FlashlightManager";

    private static final String  ACTION_FLASHLIGHT_ON="action.flashlight.on";
    private static final String  ACTION_FLASHLIGHT_OFF="action.flashlight.off";
    private static final String  ACTION_FLASHLIGHT_ON_OTHERS="action.flashlight.others.on";
    private static final String  ACTION_FLASHLIGHT_OFF_OTHERS="action.flashlight.others.off";

    static FlashlightManager sManager;
    Context mContext;
    IFlashlightStateListener mFlashlightStateListener;

    public void on(Context context) {
        android.util.Log.d("123456 FlashlightManager>", "on");
        Intent intent = new Intent(ACTION_FLASHLIGHT_ON);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.sendBroadcast(intent);
    }

    public void off(Context context) {
        android.util.Log.d("123456 FlashlightManager>", "off");
        Intent intent = new Intent(ACTION_FLASHLIGHT_OFF);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.sendBroadcast(intent);
    }

    public void on( ) {
        on(mContext);
    }

    public void off() {
        off(mContext);
    }

    public boolean isFlashlightStateOn(Context context){
        final int stateOn=1,stateOff=0,stateNUll=-1;
        final String key="config.flashlight.enale.state";
        boolean enable = android.provider.Settings.Global.getInt(context.getContentResolver(), key, stateNUll)==stateOn;
        android.util.Log.d("123456 FlashlightManager>", "getFlashlightState>enabled  ="+enable);
        return enable;
    }

    private FlashlightManager(Context context){
        mContext=context;
        initializeReceiver(context);
    }

    public static FlashlightManager getInstance(Context context) {
        if(sManager==null){
            sManager=new FlashlightManager(context);
        }
        return sManager;
    }

    public void setListener(IFlashlightStateListener listener){
        mFlashlightStateListener = listener;
    }

    public void unregisterReceiver(){
        try {
            mContext.unregisterReceiver(mFlashlightOtherReceiver);
        } catch (Exception e) {
            android.util.Log.d("123456 FlashlightManager>unregisterReceiver>", " "+e);
        }
    }
    
    public static void exit(){
        if(null==sManager)
            return;
        sManager.unregisterReceiver();
        sManager=null;
        android.util.Log.d("123456 FlashlightManager>exit> ", "   ");
    }

    private void initializeReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        //@{ added by wgx Usefulness:
        filter.addAction(ACTION_FLASHLIGHT_ON_OTHERS);
        filter.addAction(ACTION_FLASHLIGHT_OFF_OTHERS);
        //}@ end wgx
        context.registerReceiver(mFlashlightOtherReceiver, filter);
    }

    private final BroadcastReceiver mFlashlightOtherReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent!=null
                &&intent.getAction()!=null){
                switch (intent.getAction()) {
                    case ACTION_FLASHLIGHT_ON_OTHERS:
                        android.util.Log.d("123456 FlashlightManager>FlashlightOtherReceiver>", "onReceive>  ACTION_FLASHLIGHT_ON_OTHERS");
                        if(mFlashlightStateListener!=null)
                            mFlashlightStateListener.onFlashlightOnByOthers();
                        return;

                    case ACTION_FLASHLIGHT_OFF_OTHERS:
                        android.util.Log.d("123456 FlashlightManager>FlashlightOtherReceiver>", "onReceive>  ACTION_FLASHLIGHT_OFF_OTHERS");
                        if(mFlashlightStateListener!=null)
                            mFlashlightStateListener.onFlashlightOffByOthers();
                        return;
                }
            }else
                android.util.Log.d("123456 FlashlightManager>mFlashlightOtherReceiver> ", " intent=  "+intent);
        }
    };

    public interface IFlashlightStateListener{
        public void onFlashlightOffByOthers();
        public void onFlashlightOnByOthers();
    }

}
