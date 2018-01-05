package com.etouchsky.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.etouchsky.wisdom.R;

public class VerificationCodeActivity extends Activity implements
		OnClickListener {
	private Button mBtContinue;
	private EditText mEtVerificationCode;
	private int mRandomCode;
	private LinearLayout mLlBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verification_code);

		mRandomCode = getIntent().getIntExtra("randomCode", 0);

		mBtContinue = (Button) findViewById(R.id.bt_continue);
		mLlBack = (LinearLayout) findViewById(R.id.ll_back);
		mEtVerificationCode = (EditText) findViewById(R.id.et_verification_code);

		mBtContinue.setOnClickListener(this);
		mLlBack.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.bt_continue:
			String number = mEtVerificationCode.getText().toString();
			if (TextUtils.isEmpty(number)) {
				Toast.makeText(this, this.getString(R.string.empty),
						Toast.LENGTH_SHORT).show();
				return;
			}
			if (mRandomCode == Integer.parseInt(number)) {
				startActivity(new Intent(this, SetPwdActivity.class));
				finish();
			} else {
				Toast.makeText(this,
						this.getString(R.string.verificationError),
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.ll_back:
			finish();
			break;
		}
	}
}
