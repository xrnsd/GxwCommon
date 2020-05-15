package com.wgx.common.theme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class LauncherThemeBroadcastReceiver extends BroadcastReceiver implements Itheme{

    private static final String TAG = "LauncherThemeBroadcastReceiver";
    private static BroadcastReceiver mBroadcastReceiver;
    private static ILauncherThemeListener mLauncherThemeListener;
    private static ILauncherThemeFinishListener mLauncherThemeFinishListener;
    private static boolean isRegistered=false;

    @Override
    public void onReceive(Context context, Intent intent) {
        android.util.Log.d("123456"+TAG,"onReceive="+intent);
        if(intent.getAction()!=null){
            switch (intent.getAction()) {
            case ACTION:
                if(mLauncherThemeListener!=null){

                    if(intent.hasExtra(KEY))
                        mLauncherThemeListener.onThemeChange(intent.getIntExtra(KEY,THEME_ID_NULL));
                    else
                        android.util.Log.d("123456"+TAG,"onReceive onThemeChange:"+"null themeid");
                }
                break;
            case ACTION_FINISH:
                if(mLauncherThemeFinishListener!=null){
                    if(intent.hasExtra(KEY))
                        mLauncherThemeFinishListener.onThemeChangeFinish(intent.getIntExtra(KEY,THEME_ID_NULL));
                    else
                        android.util.Log.d("123456"+TAG,"onReceive onThemeChangeFinish:"+"null themeid");
                }
                break;
            }
            android.util.Log.d("123456"+TAG,"onReceive onThemeChange:"+ACTION);
        }
    }

    public static void setLauncherThemeListener (ILauncherThemeListener listener){
        mLauncherThemeListener = listener;
    }

    public static void setLauncherThemeFinishListener (ILauncherThemeFinishListener listener){
        mLauncherThemeFinishListener = listener;
    }

    public static ILauncherThemeFinishListener getLauncherThemeFinishListener(){
        return mLauncherThemeFinishListener;
    }
    

    public static void registerLauncherThemeReceiver(Context context){
        if(isRegistered)
            unregisterLauncherThemeReceiver(context);

        mBroadcastReceiver=new LauncherThemeBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        context.registerReceiver(mBroadcastReceiver,intentFilter);
        isRegistered=true;
    }

    public static void unregisterLauncherThemeReceiver(Context context){
        try {
            if(isRegistered&&mBroadcastReceiver!=null){
                context.unregisterReceiver(mBroadcastReceiver);
                isRegistered=false;
                mBroadcastReceiver=null;
            }
        } catch (Exception e) {
            android.util.Log.e("123456"+TAG,"unregisterLauncherThemeReceiver="+e);
        }
    }
    
    public static interface ILauncherThemeListener{
        public abstract void onThemeChange(int themeid);
    }

    public static interface ILauncherThemeFinishListener{
        public abstract void onThemeChangeFinish(int themeid);
    }

}
