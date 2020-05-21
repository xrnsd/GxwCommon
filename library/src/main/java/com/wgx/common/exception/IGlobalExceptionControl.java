package com.wgx.common.exception;

import android.app.Application;

public interface IGlobalExceptionControl {
    /**
     * action:打印调试log<br/>
     * remark:<br/>
     * created: wgx 2018-9-20<br/>
     */
    static final int POLICY_ENABLE_DEBUG_LOG = (1 << 0);
    /**
     * action:关闭全局异常捕获<br/>
     * remark:<br/>
     * created: wgx 2018-9-20<br/>
     */
    static final int POLICY_DISABLE_GLOBAL_EXCEPTION_CAPTURE = (1 << 1);
    /**
     * action:显示停止工作等异常提示<br/>
     * created: wgx 2018-9-20<br/>
     */
    static final int POLICY_ENABLE_CRASH_PROMPT = (1 << 2);
    /**
     * action:异常捕获后，清除模块数据<br/>
     * created: created by wgx 2018-9-20<br/>
     */
    static final int POLICY_ENABLE_CLEAR_DATA = (1 << 3);
    /**
     * action:异常捕获后，重启模块<br/>
     * created: created by wgx 2018-9-20<br/>
     * remark:<br/>
     * &nbsp 01  前提：显示停止工作等异常提示  <br/>
     * &nbsp 02  此策略开启会导致 POLICY_ENABLE_EXIT_APP 无效 <br/>
     * &nbsp 03  此策略不能连续重启模块 <br/>
     */
    static final int POLICY_ENABLE_RESTART_APP = (1 << 4);
    /**
     * action:异常捕获后，禁止保存异常信息到log文件中<br/>
     * created: created by wgx 2018-9-20<br/>
     */
    static final int POLICY_DISABLE_SAVE_LOG = (1 << 5);
    /**
     * action:异常捕获后，退出APP<br/>
     * remark:前提：没有显示停止工作等异常提示<br/>
     * created: created by wgx 2018-9-20<br/>
     */
    static final int POLICY_ENABLE_EXIT_APP = (1 << 6);

    /**
     * action:返回Application<br/>
     * 使用示例: return Application.this;<br/>
     * created: wgx 2018-12-07<br/>
     */
    public Application getApplication();

    /**
     * action:配置异常处理行为<br/>
     * 使用示例:<br/>
                int flags=0;<br/>
                flags |= IGlobalExceptionControl.POLICY_XXX;<br/>
                flags |= IGlobalExceptionControl.POLICY_XXX;<br/>
                return flags;<br/>
     *
     * @param
     * @return int<br/>
     * created: wgx 2018-12-07<br/>
     */
    public int getPolicy();
}
