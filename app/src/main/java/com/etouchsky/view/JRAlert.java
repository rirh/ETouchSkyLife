package com.etouchsky.view;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class JRAlert
{
	public JRAlert()
	{
		;
	}
	private static Toast toast;
	static TextView tv;
	static ImageView imageCodeProject;
	static LinearLayout toastView;
	static LinearLayout.LayoutParams params;
	public static void showMessage(Context context, String message)
	{
		Toast toast = Toast.makeText(context, message, 1000);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
		// JRTools.toast(context, message, "1000");
	}

	public static void alertText(Context context, String title, String content, DialogInterface.OnClickListener onClick)
	{
		new AlertDialog.Builder(context).setTitle(title).setMessage(content).setPositiveButton(android.R.string.yes, onClick).setNegativeButton(android.R.string.cancel, onClick).show();
	}

	public static void alertText1(Context context, String title, String content, DialogInterface.OnClickListener onClick)
	{
		new AlertDialog.Builder(context).setTitle(title).setMessage(content).setPositiveButton(android.R.string.yes, onClick).show();
	}

	public static void alertText(Context context, String title, String content, String positiveButton, DialogInterface.OnClickListener onClick)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(content);
		if (onClick != null)
		{
			builder.setPositiveButton(positiveButton, onClick);
		}
		else
		{
			builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
		}
		builder.show();
	}




	public static void alertChoice(Context context, String title, List<String> names, int checkedIndex, String positiveButton, String neutralButton, String negativeButton, DialogInterface.OnClickListener onClick)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		String[] names1 = new String[names.size()];

		for (int i = 0; i < names1.length; i++)
		{
			names1[i] = names.get(i);
		}

		builder.setSingleChoiceItems(names1, checkedIndex, onClick);
		if (title != null && !"".equals(title))
		{
			builder.setTitle(title);
		}
		if (positiveButton != null && !"".equals(positiveButton))
		{
			builder.setPositiveButton(positiveButton, onClick);
		}

		if (neutralButton != null && !"".equals(neutralButton))
		{
			builder.setNeutralButton(neutralButton, onClick);
		}

		if (negativeButton != null && !"".equals(negativeButton))
		{
			builder.setNegativeButton(negativeButton, onClick);
		}

		builder.show();
	}

	public static void startGauge(Context context, String text)
	{
		JRProgress.show(context, text);
	}

	public static void stopGauge()
	{
		JRProgress.dismiss();
	}

	private static Handler mHandler = new Handler();
	private static Runnable r = new Runnable() {
		public void run() {
			toast.cancel();
		}
	};


}

class JRProgress
{
	private static ProgressDialog gauge;

	public JRProgress()
	{
		;
	}

	static void show(Context context, String text)
	{
		// 创建ProgressDialog对象
		gauge = new ProgressDialog(context);
		// 设置进度条风格，风格为圆形，旋转的
		gauge.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置ProgressDialog 的进度条是否不明确
		gauge.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		gauge.setCancelable(false);
		// 设置ProgressDialog 提示信息
		gauge.setMessage(text);
		// 让ProgressDialog显示
		gauge.show();
	}

	static void dismiss()
	{
		if (gauge != null)
		{
			gauge.dismiss();
			gauge = null;
		}
	}
}