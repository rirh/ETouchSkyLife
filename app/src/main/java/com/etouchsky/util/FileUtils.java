package com.etouchsky.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.etouchsky.wisdom.R;

import java.io.File;

public class FileUtils {
	/** 缓存文件目录 */
	private File mCacheDir;
	public FileUtils(Context context, String cacheDir){
		if (android.os.Environment.getExternalStorageState().
				equals(android.os.Environment.MEDIA_MOUNTED))
			mCacheDir = new File(cacheDir);
		else
			mCacheDir = context.getCacheDir();// 如何获取系统内置的缓存存储路径
		if(!mCacheDir.exists())
			mCacheDir.mkdirs();
	}
	public String getCacheDir(){
		return mCacheDir.getAbsolutePath();
	}
	public static void alertText(Context context, String title, String content,
								 DialogInterface.OnClickListener onClick) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(content)
				.setPositiveButton(R.string.ok, onClick)
				.setNegativeButton(R.string.cancel, onClick).show();
	}
	public static void alertTextNoCancel(Context context, String title, String content,
								 DialogInterface.OnClickListener onClick) {
		new AlertDialog.Builder(context).setMessage(content)
				.setPositiveButton(R.string.ok, onClick).show();
	}
}
