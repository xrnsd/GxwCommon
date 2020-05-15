package com.wgx.common.exception;

import android.app.Application;

public interface IGlobalExceptionControl {
    /**
     * action:打印调试log<br/>
     * remark:<br/>
     * created: wgx 2018-9-20<br/>
     */
    static final int POLICY_DEBUG_LOG = 0x0001;

    /**
     * action:开启全局异常捕获<br/>
     * remark:<br/>
     * created: wgx 2018-9-20<br/>
     */
    static final int POLICY_PRACTICE_GLOBAL_EXCEPTION_CAPTURE = 0x0002;
    /**
     * action:屏蔽停止工作等异常提示<br/>
     * remark:前提：开启全局异常捕获<br/>
     * created: wgx 2018-9-20<br/>
     */
    static final int POLICY_PRACTICE_GLOBAL_EXCEPTION_PROMPT = 0x0004;
    /**
     * action:异常捕获后，清除模块数据<br/>
     * remark:前提：开启全局异常捕获<br/>
     * created: created by wgx 2018-9-20<br/>
     */
    static final int POLICY_CLEAR_TAIL_CLEAR = 0x0008;
    /**
     * action:异常捕获后，重启模块<br/>
     * created: created by wgx 2018-9-20<br/>
     */
    static final int POLICY_CLEAR_TAIL_RESTART = 0x0010;
    /**
     * action:异常捕获后，关闭模块<br/>
     * remark:前提：未添加 POLICY_CLEAR_TAIL_RESTART 或 POLICY_CLEAR_TAIL_CLEAR <br/>
     * created: created by wgx 2018-9-20<br/>
     */
    static final int POLICY_CLEAR_TAIL_KILLMYSELF = 0x0020;
    /**
     * action:异常捕获后，保存异常信息到log文件中<br/>
     * remark:前提：开启全局异常捕获<br/>
     * created: created by wgx 2018-9-20<br/>
     */
    static final int POLICY_CLEAR_TAIL_AUTO_PERSISTENCE = 0x0040;

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
    public int getPolicys();
}
