package com.etouchsky.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ddclient.dongsdk.AbstractDongCallbackProxy.DongAccountCallbackImp;
import com.ddclient.dongsdk.DeviceInfo;
import com.ddclient.dongsdk.DongSDKProxy;
import com.ddclient.jnisdk.InfoUser;
import com.etouchsky.adapter.AuthorizedAccountListAdapter;
import com.etouchsky.wisdom.R;
import com.gViewerX.util.LogUtils;

import java.util.ArrayList;

public class AuthorizedAccountActivity extends Activity implements
		OnClickListener {

	private LinearLayout mLlBack;
	private Button mBtAuthoriz;
	private EditText mEtAccount;
	private ListView mListView;
	private AuthorizedAccountListAdapter mListAdapter;
	private DeviceInfo mDeviceInfo;
	private ArrayList<InfoUser> mInfoUsers;
	private ProgressDialog mProgress;
	private TextView mTvBack;
	private InfoUser mInfoUser;
	private String mAccountName = "";

	// private int mUserid = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authorized_account);
		mDeviceInfo = (DeviceInfo) getIntent().getSerializableExtra(
				SettingDeviceActivity.DEVICEINFO_KEY);

		mLlBack = (LinearLayout) findViewById(R.id.ll_back);
		mBtAuthoriz = (Button) findViewById(R.id.bt_authoriz);
		mEtAccount = (EditText) findViewById(R.id.et_account);
		mListView = (ListView) findViewById(R.id.listview);
		mTvBack = (TextView) findViewById(R.id.tv_back);

		mListAdapter = new AuthorizedAccountListAdapter(this);
		mListView.setAdapter(mListAdapter);
		mLlBack.setOnClickListener(this);
		mBtAuthoriz.setOnClickListener(this);
		mListView.setOnItemClickListener(mListViewItemOnClick);
		mTvBack.setText(mDeviceInfo.deviceName);

		mAccountProxy = new AuthorizedAccountActivityDongAccountProxy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		DongSDKProxy.registerAccountCallback(mAccountProxy);
		DongSDKProxy.requestGetDeviceAuthorizeAccounts(mDeviceInfo.dwDeviceID);
		LogUtils.i("AuthorizedAccountActivity.clazz--->>>onResume........mDeviceInfo.dwDeviceID:"
				+ mDeviceInfo.dwDeviceID);
	}

	@Override
	protected void onPause() {
		super.onPause();
		DongSDKProxy.unRegisterAccountCallback(mAccountProxy);
	}

	private OnItemClickListener mListViewItemOnClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			mInfoUser = (InfoUser) mListAdapter.getItem(arg2);

			AlertDialog.Builder mDialog = new AlertDialog.Builder(
					AuthorizedAccountActivity.this);
			mDialog.setTitle(AuthorizedAccountActivity.this
					.getString(R.string.warn));
			mDialog.setMessage(AuthorizedAccountActivity.this
					.getString(R.string.deleteAuthorization));
			mDialog.setPositiveButton(getString(R.string.ok),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mProgress = ProgressDialog.show(
									AuthorizedAccountActivity.this, "",
									AuthorizedAccountActivity.this
											.getString(R.string.wait), true,
									true);
							DongSDKProxy.requestDeleteDevice(mInfoUser.userID,
									mDeviceInfo.dwDeviceID);
							LogUtils.i("AuthorizedAccountActivity.clazz--->>>onItemClick........mInfoUser:"
									+ mInfoUser);
						}
					});
			mDialog.setNegativeButton(getString(R.string.cancel), null);
			mDialog.show();
		}
	};
	private AuthorizedAccountActivityDongAccountProxy mAccountProxy;

	private class AuthorizedAccountActivityDongAccountProxy extends
			DongAccountCallbackImp {

		@Override
		public int onAuthenticate(InfoUser tInfo) {
			LogUtils.i("AuthorizedAccountActivity.clazz--->>>onAuthenticate........tInfo:"
					+ tInfo);
			return 0;
		}

		@Override
		public int onDelDevice(int nReason) {
			if (nReason == 0) {
				Toast.makeText(AuthorizedAccountActivity.this,
						AuthorizedAccountActivity.this.getString(R.string.suc),
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(
						AuthorizedAccountActivity.this,
						AuthorizedAccountActivity.this.getString(R.string.fail),
						Toast.LENGTH_SHORT).show();
			}
			if (mProgress != null) {
				mProgress.dismiss();
			}
			LogUtils.i("AuthorizedAccountActivity.clazz--->>>onDelDevice........nReason:"
					+ nReason);
			return 0;
		}

		@Override
		public int onSetDeviceName(int nReason) {
			LogUtils.i("AuthorizedAccountActivity.clazz--->>>onSetDeviceName........nReason:"
					+ nReason);
			return 0;
		}

		@Override
		public int onGetDeviceUserInfo(ArrayList<InfoUser> list) {
			mInfoUsers = list;
			if (mProgress != null) {
				mProgress.dismiss();
			}
			mListAdapter.setData(mInfoUsers);
			mListAdapter.notifyDataSetChanged();
			LogUtils.i("AuthorizedAccountActivity.clazz--->>>onGetDeviceUserInfo........list:"
					+ list);
			return 0;
		}

		@Override
		public int onAddDeviceUser(int nReason, int userid) {
			// mUserid = userid;
			if (nReason == 0) {
				if (mProgress != null) {
					mProgress.dismiss();
				}
				mInfoUsers.add(new InfoUser(userid, mAccountName, new byte[1],
						""));
				mListAdapter.notifyDataSetChanged();
				Toast.makeText(AuthorizedAccountActivity.this,
						AuthorizedAccountActivity.this.getString(R.string.suc),
						Toast.LENGTH_SHORT).show();
			} else {
				if (mProgress != null) {
					mProgress.dismiss();
				}
				Toast.makeText(
						AuthorizedAccountActivity.this,
						AuthorizedAccountActivity.this.getString(R.string.fail),
						Toast.LENGTH_SHORT).show();
			}
			LogUtils.i("AuthorizedAccountActivity.clazz--->>>onAddDeviceUser........nReason:"
					+ nReason + ";userid:" + userid);
			return 0;
		}

		@Override
		public int onUserError(int nErrNo) {
			LogUtils.i("LoginActivity.clazz--->>>onUserError........nErrNo:"
					+ nErrNo);
			return 0;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.ll_back:
			finish();
			break;
		case R.id.bt_authoriz:
			mAccountName = mEtAccount.getText().toString();
			if (mAccountName.equals("")) {
				Toast.makeText(this, this.getString(R.string.empty),
						Toast.LENGTH_SHORT).show();
				return;
			}
			for (InfoUser infoUser : mInfoUsers) {
				if (infoUser.userName.equals(mAccountName)) {
					Toast.makeText(this, this.getString(R.string.yesOrNo),
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
			mProgress = ProgressDialog.show(this, "",
					this.getString(R.string.wait), true, true);
			DongSDKProxy.requestAuthorize(mAccountName, mDeviceInfo.dwDeviceID);
			break;
		}
	}
}
