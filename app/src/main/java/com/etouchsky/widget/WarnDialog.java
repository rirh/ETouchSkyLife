package com.etouchsky.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import com.ddclient.jnisdk.InfoDevice;
import com.etouchsky.wisdom.R;

import java.util.ArrayList;

public class WarnDialog {

	public static void applicationBroughtWarn(Context context,
			Activity activity, ArrayList<InfoDevice> warnlist, boolean flag) {
	}

	public static void showDialog(Context context, ProgressDialog progress,
			int errorCode) {
		if (progress != null) {
			progress.dismiss();
		}

		try {
			new AlertDialog.Builder(context)
					.setTitle(context.getString(R.string.warn))
					.setIcon(android.R.drawable.ic_dialog_alert)

					.setMessage(
							context.getString(R.string.mistake) + "("
									+ context.getString(R.string.errorCode)
									+ errorCode + ")").setCancelable(false)
					.setPositiveButton(context.getString(R.string.ok), null)
					.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
