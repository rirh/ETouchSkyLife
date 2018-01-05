package com.etouchsky.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.etouchsky.GViewerXApplication;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;

import java.lang.reflect.Field;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class Config {

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public static void setNoTheme(Activity c){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {  
        	Window window = c.getWindow();
      	  window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      	  window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
      	  | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
      	  window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      	  window.setStatusBarColor(Color.TRANSPARENT);//calculateStatusColor(Color.WHITE, (int) alphaValue)  
        }
	}
	public static HttpUtils http = new HttpUtils();
	public static Gson gsong=new Gson();
	/*
	判断app是否在运行
	 */
	public static boolean getAppIsRun(Context context){
		// Returns a list of application processes that are running on the      // device
		ActivityManager activityManager = (ActivityManager) context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
		String packageName = context.getApplicationContext().getPackageName();
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)&& appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}
		return false;

		/*ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runnings = am.getRunningAppProcesses();
		for(ActivityManager.RunningAppProcessInfo running : runnings){
			if(running.processName.equals(context.getPackageName())){
				if(running.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
						|| running.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE){
					//前台显示
					return true;
				}else{
					//后台显示
					return false;
				}
				}
			}
			return false;*/
	}

	/**
	 * 判断某个activity是否在运行
	 * @param context
	 * @return
	 */
	public static boolean getCurrentActIsRun(Context context,String activityName) {
		final ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		String name = am.getRunningTasks(1).get(0).topActivity.getClassName();
		if (activityName.equals(name)) {
			return true;
		}  else {
			return false;
		}

	}

	/**
	 * 获取显示规格
	 *
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		((WindowManager) GViewerXApplication.context().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displaymetrics);
		return displaymetrics;
	}

	public static float getScreenHeight() {
		return getDisplayMetrics().heightPixels;
	}

	public static float getScreenWidth() {
		return getDisplayMetrics().widthPixels;
	}
	public static int getStatusBarHeight(Context context) {
		Class<?> clazz;

		Object obj;

		Field field;

		int x, sBar = 0;

		try {
			clazz = Class.forName("com.android.internal.R$dimen");
			obj = clazz.newInstance();
			field = clazz.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sBar = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return sBar;
	}
}
