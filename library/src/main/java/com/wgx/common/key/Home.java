package com.wgx.common.key;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class Home {
    
    static boolean isregisterReceiver = false;
    static final String ACTION="action.home.keyevent";

    static Home sMain;
    static BroadcastReceiver sHomeKeyEventReceiver;
    static onHomeKeyListener sHomeKeyListener;
    
    private Home(){}
    
    public static Home getInstance(Context context,onHomeKeyListener listener){
        if(listener==null){
            throw new NullPointerException("onHomeKeyListener is null");
        }
        if(sMain==null)
            sMain=new Home();
        sHomeKeyListener=listener;
        if(!isregisterReceiver){
            sHomeKeyEventReceiver=new BroadcastReceiver(){
                @Override
                public void onReceive(Context context, Intent intent) {
                    sHomeKeyListener.onHomeKey(intent.getIntExtra(ACTION, -1));
                }
            };

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION);
            context.registerReceiver(sHomeKeyEventReceiver,intentFilter);
            isregisterReceiver=true;
        }
        return sMain;
    }
    
    public static Home getDefInstance(){
        return sMain;
    }
    
    public static void removeHomeKeyEventListener(Context context){
        if(isregisterReceiver&&sHomeKeyEventReceiver!=null){
            context.unregisterReceiver(sHomeKeyEventReceiver);
            sHomeKeyEventReceiver=null;
        }
    }

    public static void sendHomeKeyEvent(Context context,final int action){
            Intent keyEvent=new Intent(ACTION);
            keyEvent.putExtra(ACTION, action);
            keyEvent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            context.sendBroadcast(keyEvent);
    }
    
    public interface onHomeKeyListener{
        public void onHomeKey(int action);
    }

}
