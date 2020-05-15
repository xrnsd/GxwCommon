package com.wgx.common.theme;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class ThemeUtils implements Itheme{
    private static final String TAG = "ThemeUtils";
    private static final  String SP_FLAG="launcher_theme";

    private static ThemeUtils sThemeUtils;
    private static int sThemeId=THEME_ID_NULL;

    private Resources mThemeResources;
    private Resources mBaseResources;
    private String mThemePackageName;
    private String mBasePackageName;
    private Context mBaseContext;
    private Context mThemeContext;
    private SharedPreferences mSharedPreferencesTheme;

    private ThemeUtils(Context context,int theme_id){
        setThemeId(context, theme_id);
    }

    private ThemeUtils(Context context){
        setThemeId(context, getThemeId(context));
    }

    public static ThemeUtils getInstance(Context context){
        if(sThemeUtils==null)
            sThemeUtils=new ThemeUtils(context);
        return sThemeUtils;
    }

    public static ThemeUtils getInstance(Context context,int theme_id){
        if(sThemeUtils==null
            ||sThemeId!=theme_id)
            sThemeUtils=new ThemeUtils(context,theme_id);
        return sThemeUtils;
    }

    public int getThemeId(Context context) {
        if(mSharedPreferencesTheme==null){
            mSharedPreferencesTheme = context.getSharedPreferences(SP_FLAG, 0);
        }
        sThemeId = mSharedPreferencesTheme.getInt(KEY,THEME_ID_DEFAULT);
        android.util.Log.d("123456 ThemeUtils>getThemeId> ", "  ThemeId = "+sThemeId);

        return sThemeId;
    }

    public void setThemeId(Context baseContext,int theme_id) {
        if(mSharedPreferencesTheme==null){
            mSharedPreferencesTheme = baseContext.getSharedPreferences(SP_FLAG, 0);
        }
        Editor editor=mSharedPreferencesTheme.edit();
        editor.putInt(KEY, theme_id);
        editor.commit();

        final String themePackageName = createThemePackagename(baseContext,theme_id);

        resetLocalThemePackageName(themePackageName);
        resetLocalThemeContext(baseContext,themePackageName);
        resetLocalThemeResources(getLocalThemeContext(baseContext,themePackageName));

        mBaseContext=baseContext;
        mBasePackageName=baseContext.getPackageName();
        mBaseResources=baseContext.getResources();
        sThemeId=theme_id;
    }

    protected boolean isAppExist(Context context,String pkgName) {
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(pkgName, 0);
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            info = null;
        }

        return info != null;
    }

    private String createThemePackagename(Context baseContext,final int theme_id) {
        String packageName=THEME_ID_NULL==theme_id?baseContext.getPackageName():THEME_PACKAGE_NAME_BASE+theme_id;

        if(!isAppExist(baseContext,packageName))
            packageName=baseContext.getPackageName();
        android.util.Log.d("123456 ", "Theme>getThemePackagename="+packageName);
        return packageName;
    }

    private Context createThemeContext(Context baseContext ,String theme_packagename) {
        try {
                return baseContext.createPackageContext(theme_packagename,Context.CONTEXT_IGNORE_SECURITY);
        } catch (NameNotFoundException e) {
            android.util.Log.d("123456", "Utilities>createThemeContext fail ="+e);
        }
        return baseContext;
    }

    private String getLocalThemePackagename(Context baseContext) {
        int theme_id=getThemeId(baseContext);
        return createThemePackagename(baseContext,theme_id);
    }

   public Context getLocalThemeContext() {
        return mThemeContext;
    }

    public Resources getLocalThemeResources() {
        return mThemeResources;
    }

    private String getThemePackagename(Context baseContext,final int theme_id) {
        String packageName=THEME_ID_NULL==theme_id?baseContext.getPackageName():THEME_PACKAGE_NAME_BASE+theme_id;
        android.util.Log.d("123456 ", "Theme>getThemePackagename="+packageName);
        return packageName;
    }

    private Context getLocalThemeContext(Context baseContext ,String theme_packagename) {
        try {
            if(null==mThemeContext)
                mThemeContext=baseContext.createPackageContext(theme_packagename,Context.CONTEXT_IGNORE_SECURITY);
            return mThemeContext;
        } catch (NameNotFoundException e) {
            android.util.Log.d("123456", "Utilities>getThemeContext fail ="+e);
        }
        return baseContext;
    }

    private void resetLocalThemePackageName(String theme_packagename) {
        android.util.Log.d("123456 Theme>resetLocalThemePackageName> ", "   ");
       mThemePackageName=theme_packagename;
    }

    private void resetLocalThemeContext(Context context ,String theme_packagename) {
        android.util.Log.d("123456 Theme>resetLocalThemeContext> ", "   ");
        try {
            mThemeContext=context.createPackageContext(theme_packagename,Context.CONTEXT_IGNORE_SECURITY);
        } catch (NameNotFoundException e) {
            android.util.Log.d("123456", "Utilities>resetThemeContext fail ="+e);
        }
    }

    private void resetLocalThemeResources(Context themeContext) {
        android.util.Log.d("123456 Theme>resetLocalThemeResources> ", "   ");
        mThemeResources=themeContext.getResources();
    }

    public Drawable getThemeDrawableByName(String type,String res_file_name) {
       return getThemeDrawableByName(type,res_file_name,null);
     }

    public Drawable getThemeDrawableByName(String type,String res_file_name,Drawable def) {
        final int res_id = getThemeResourceByName(type,res_file_name,0);
        if(res_id>0)
            return getLocalThemeResources().getDrawable(res_id);
        return def;
    }

    public int getThemeResourceByName(String type,String res_file_name) {
        return getThemeResourceByName(type,res_file_name,-1);
    }

    public int getThemeResourceByName(String type,String res_file_name,int def) {
        int res_id=mThemeResources.getIdentifier(res_file_name,type,mThemePackageName);
        if(res_id>0)
            return res_id;
        android.util.Log.d("123456 ", "Theme>getThemeResourceByName use def res="+res_file_name);

//        if(res_file_name.equals(FILENAME_THEME))
//            res_id=com.android.internal.R.drawable.default_wallpaper;
//        else
            res_id=mBaseResources.getIdentifier(res_file_name,type,mBasePackageName);

        if(res_id<1){
            if(-1<def){
                return def;
            }
           throw new Resources.NotFoundException("can't find "+res_file_name);
        }

        return res_id;
     }

    public float getThemeDimen(String res_file_name,float def) {
        final String type="dimen";
        int res_id=getThemeResourceByName(type,res_file_name,0);
        if(res_id<1){
            android.util.Log.d("123456 ", "Theme>getThemeResourceByName use def res="+res_file_name);
           return def;
        }

        return Float.parseFloat(mThemeResources.getString(res_id));
     }

    public int getThemeDimen(String res_file_name,int def) {
        final String type="dimen";
        int res_id=getThemeResourceByName(type,res_file_name,0);
        if(res_id<1){
            android.util.Log.d("123456 ", "Theme>getThemeResourceByName use def res="+res_file_name);
           return def;
        }
        return Integer.valueOf(mThemeResources.getString(res_id));
     }
}
