package com.wgx.common.theme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.wallpaper.WallpaperService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class Theme extends Activity implements Itheme{

    private static final String TAG = "Theme";

    private List<String> mThemePackageList;
    private String mPackageNameThemeSel=null;

    private RadioButton[] mRadioButtons=null;
    
    private ThemeUtils mThemeUtils;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void UpdateLauncherThemeById(Context context,String theme_packagename) {
        if(theme_packagename==null){
            android.util.Log.d("123456 ", "Theme>UpdateLauncherThemeById=null");
            return;
        }
        int theme_id=Integer.valueOf(theme_packagename.replaceAll(THEME_PACKAGE_NAME_BASE, ""));
        UpdateLauncherThemeById(context,theme_id);
    }

    public void UpdateLauncherThemeById(Context context,int theme_id) {
        android.util.Log.d("123456 "+TAG, "Theme>UpdateLauncherUIByThemeId="+theme_id);
        //wait
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Wallpaper settings …");
        dialog.show();
        
        //save id android reset theme context
        mThemeUtils.setThemeId(context,theme_id);

        //update wallpaper
//        try {
//            int wallpaperResId=mThemeUtils.getThemeResourceByName("drawable", FILENAME_THEME);
//            WallpaperManager   wm=(WallpaperManager)context.getSystemService(WallpaperService.WALLPAPER_SERVICE);
//            wm.setBitmap(BitmapFactory.decodeResource(mThemeUtils.getLocalThemeResources(),wallpaperResId));
//            android.util.Log.d("123456 ", "Theme>UpdateLauncherThemeById update wallpaper");
//        } catch (IOException e) {
//            e.printStackTrace();
//            android.util.Log.d("123456 ", "Theme>UpdateLauncherThemeById update wallpaper fail="+e);
//        }


        //reboot launcher
        Intent intent=new Intent(ACTION);
        intent.putExtra(KEY, theme_id);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.sendBroadcast(intent);
    }
}
