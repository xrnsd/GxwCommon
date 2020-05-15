package com.wgx.common.theme;

import android.content.SharedPreferences;

public interface Itheme {

    public static final String THEME_PACKAGE_NAME_BASE = "com.lz.launcher.themepackage";

    public static final String ACTION="Intent.action.launcher.theme.chang";
    public static final String ACTION_FINISH="Intent.action.launcher.theme.chang.finish";
    public static final String KEY="launcher.theme.id";
    public static final int THEME_ID_NULL=-1;
    public static final  int THEME_ID_DEFAULT=16;

}
