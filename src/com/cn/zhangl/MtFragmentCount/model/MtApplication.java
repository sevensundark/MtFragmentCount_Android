package com.cn.zhangl.MtFragmentCount.model;

import android.app.Application;
import android.view.WindowManager;

/**
 * Created with IntelliJ IDEA.
 * User: zhangl
 * Date: 13/05/31
 * Time: 11:14
 * To change this template use File | Settings | File Templates.
 */
public class MtApplication extends Application {
    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getMtwmParams() {
        return  wmParams;
    }
}
