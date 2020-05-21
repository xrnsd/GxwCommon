package com.wgx.common.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * action : HandlerThread相关封装<br/>
 * class: HandlerThreadManager <br/>
 * package: com.wgx.common.thread <br/>
 * author: wuguoxian <br/>
 * date: 20200518 <br/>
 * version:0.1<br/>
 * </p>
 */
public class HandlerThreadManager {

    static HandlerThreadManager sMain;
    private HandlerThreadManager(){

    }
    public static HandlerThreadManager getInstance(){
        if(null==sMain){
            sMain=new HandlerThreadManager();
        }
        return sMain;
    }

    List<String> mThreadTagList =new ArrayList<>();
    List<HandlerThread> mThreadList =new ArrayList<>();

    public Looper getFeederLooperByTag(String tag){
        if(!mThreadTagList.contains(tag)){
            HandlerThread handlerThread=new HandlerThread(tag);
            handlerThread.start();
            mThreadList.add(handlerThread);
            mThreadTagList.add(tag);
            return handlerThread.getLooper();
        }
        return mThreadList.get(mThreadTagList.indexOf(tag)).getLooper();
    }

    public Handler getFeederHandlerByTag(String tag){
        return new Handler(getFeederLooperByTag(tag));
    }

}
