package com.wgx.common.gestures;

import android.content.Context;
import android.graphics.Rect;

/**
 * action:TODO
 * 
 * Package: com.wgx.common.gestures
 * ClassName: IGlobalTouchGesturesEventHandling
 * created:wgx
 * version:0.2
 * 
 */
public interface OnTouchGestures {
    /**
     * action:需要吃掉事件的手势区域id组[默认最大100个id]<br/>
     * remark:<br/>
     * @param
     * @return int[]<br/>
     */
    public int[] getEnableGestureRectIndex();

    /**
     * action:设定手势的区域范围<br/>
     * remark:手机方向.手势1`11id<br/>
     * @param
     * @return Rect<br/>
     */
    public Rect getGestureRectByIndex(int orientation,int index);

    /**
     * action:手势触发对应的操作<br/>
     * remark:<br/>
     * @param
     * @return void<br/>
     */
    public void OnTouchGesturesRegional(int index);

    /**
     * action:手势是否生效的判断[速度为自动判断]<br/>
     * remark:<br/>
     * @param
     * @return boolean<br/>
     */
    public boolean isTouchGesturesEffective(int index,float moveX,float moveY);
}