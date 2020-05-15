package com.wgx.common.gestures;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.OrientationEventListener;

import com.wgx.common.base.Utils;
/**
 * action:TODO
 * 
 * project: MemoryInfoDemo
 * Package: com.edl.memory
 * ClassName: TouchGestureManager
 * created:wgx
 * date: 2018年2月27日 上午10:14:40
 * version:0.1
 * remark: <pre>
<b>使用示例：</b>
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
      TouchGestureManager mGesturesEventListener=null;
      if(mGesturesEventListener==null){
                  mGesturesEventListener=com.wgx.common.gestures.TouchGestureManager.getInstance(getApplicationContext(), 
                      new com.wgx.common.gestures.OnTouchGestures(){
                                ..........
                  });
            }
            return mGesturesEventListener.isTouchGestures(getApplicationContext(), ev)
                    ||super.dispatchTouchEvent(ev);
    }
 </pre>
 */
public class TouchGestureManager extends OrientationEventListener {
    private static final String TAG = "TouchGestureManager";

    public final static int THRESHOLDVALUE_TIME=200;
    public static float THRESHOLDVALUE_BOTTOM = 15;
    public static float THRESHOLDVALUE_LEFT = 15;
    public static float THRESHOLDVALUE_RIGHT = THRESHOLDVALUE_LEFT;
    public static float THRESHOLDVALUE_MOVE = 20;

    int mGestureIndex=-1;
    float mTriggerCoordinatesYBottom =0,
            mTriggerCoordinatesXLeft =0,
            mTriggerCoordinatesXRight=0,
            mDownX =0, mDownY = 0,mMoveX=0,mMoveY=0;
    boolean isEnableDown =false;
    boolean isGlobalTouchGestureProcessing=false;

    List<Rect> mRects;
    private OnTouchGestures mTouchGestures;
    static TouchGestureManager sManager=null;

    private TouchGestureManager(Context context) {
        super(context);
        final float scale = context.getResources().getDisplayMetrics().density;
        THRESHOLDVALUE_BOTTOM=(THRESHOLDVALUE_BOTTOM * scale + 0.5f);
        THRESHOLDVALUE_LEFT=THRESHOLDVALUE_RIGHT=THRESHOLDVALUE_BOTTOM;
        THRESHOLDVALUE_MOVE=(THRESHOLDVALUE_MOVE * scale + 0.5f);
        mTriggerCoordinatesXLeft=THRESHOLDVALUE_LEFT;
        int[] screenInfo = Utils.getScreenInfo(context);
        mTriggerCoordinatesXRight=screenInfo[0]-THRESHOLDVALUE_RIGHT;
        mTriggerCoordinatesYBottom=screenInfo[1]-THRESHOLDVALUE_BOTTOM;
    }

    public static TouchGestureManager getInstance(Context context,OnTouchGestures handling){
        if(sManager==null){
            if(handling==null)
                throw new NullPointerException("OnTouchGestures is null");
            sManager=new TouchGestureManager(context);
            sManager.setTouchGestures(handling);
            sManager.onOrientationChanged(0);
        }
        return sManager;
    }

    public TouchGestureManager(Context context,OnTouchGestures handling){
       this(context);
       setTouchGestures(handling);
       onOrientationChanged(0);
    }

    public static void exit(){
        sManager=null;
    }

    public void setTouchGestures(OnTouchGestures handling) {
        mTouchGestures=handling;
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if(mTouchGestures==null){
            android.util.Log.e("123456 GlobalTouchGestureManager>", "onOrientationChanged>  mTouchGestures is null");
            return;
        }
        final int count=100;
        if(mRects==null){
            mRects=new ArrayList<>();
        }else mRects.clear();

        Rect rect=null;
        for (int index = 0;index < count; index++) {
            rect=mTouchGestures.getGestureRectByIndex(orientation,index);
            if(rect==null)
                continue;
            mRects.add(rect);
        }
        android.util.Log.d("123456 TouchGestureManager>", "onOrientationChanged>  ="+orientation);
    }

    private int getGestureIndexByDownPoint(final float down_x,final float down_y ){
        if(mRects==null){
            android.util.Log.e("123456 GlobalTouchGestureManager>", "getGestureIndexByDownPoint>  mRects is null");
            return -1;
        }
        int x=(int)down_x,y=(int)down_y;
        for (int index = 0,length=mRects.size();index < length; index++) {
            if(mRects.get(index).contains(x, y)){
                android.util.Log.d("123456 GlobalTouchGestureManager>", "getGestureIndexByDownPoint>  rect ="+mRects.get(index)+"   index="+index);
                return index;
            }
        }
        return -1;
    }

    public boolean isTouchGestures(Context context, MotionEvent arg1) {
        if(isGlobalTouchGestureProcessing&&arg1.getAction()!=MotionEvent.ACTION_UP)
            return true;//舍弃触发了手势之后到抬起手指的全部触摸操作
        switch (arg1.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = arg1.getRawX();
                mDownY = arg1.getRawY();
                mGestureIndex = getGestureIndexByDownPoint(mDownX,mDownY);
                android.util.Log.d("123456 TouchGestureManager>", "isTouchGestures>"+
                    "  [  "+mDownX+" , "+mDownY+"  ] >>  "+mGestureIndex);
                break;

            case MotionEvent.ACTION_MOVE:
                if(mGestureIndex>0){
                    mMoveX = Math.abs(mDownX-arg1.getRawX());
                    mMoveY = mDownY-arg1.getRawY();
                    if(arg1.getEventTime()-arg1.getDownTime()<THRESHOLDVALUE_TIME
                            &&(mMoveX>THRESHOLDVALUE_MOVE||mMoveY>THRESHOLDVALUE_MOVE)){
                        isEnableDown=false;
                        if(mTouchGestures.isTouchGesturesEffective(mGestureIndex, mMoveX, mMoveY)){
                            mTouchGestures.OnTouchGesturesRegional(mGestureIndex);
                            isGlobalTouchGestureProcessing=true;
                            return true;
                        }
                    }
                    final int[] enableIndex=mTouchGestures.getEnableGestureRectIndex();
                    if(enableIndex!=null
                        &&enableIndex.length>0){
                        for (int index : enableIndex) {
                            if(index == mGestureIndex)
                                return true;
                        }
                    }
                }
                break;

                case MotionEvent.ACTION_UP:
                    isGlobalTouchGestureProcessing=false;
                    mMoveX=0;
                    mMoveY=0;
                    mGestureIndex=-1;
                break;
        }
        return false;
    }
}