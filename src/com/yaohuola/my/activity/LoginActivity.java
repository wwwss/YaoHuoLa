package com.yaohuola.my.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.library.uitls.AESUitls;
import com.library.uitls.SmartLog;
import com.library.uitls.TexChangetUtils;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.task.HttpTask;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author admin 登录页面
 */
public class LoginActivity extends BaseActivity implements TextWatcher, Runnable {

	private EditText etPhoneNumber;
	private EditText etVerificationCode;
	private TextView tv_login;
	private TextView tv_yz;
	private TextView tv_xieYi;
	private String phoneNumber;
	private String verificationCode;
	private int time = 30;// 倒计时

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_login);

	}

	@Override
	public void initView() {

		etPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
		etVerificationCode = (EditText) findViewById(R.id.verificationCode);
		tv_yz = (TextView) findViewById(R.id.verification);
		tv_yz.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		tv_xieYi = (TextView) findViewById(R.id.xieYi);
		tv_xieYi.setOnClickListener(this);
		tv_xieYi.setText(TexChangetUtils.updateText("点击-登录 即表示你同意《法律声明及隐私政策》", 0, Color.RED, 12, 23));
		tv_login = (TextView) findViewById(R.id.login);
		tv_login.setOnClickListener(this);
		tv_login.setClickable(false);
		etPhoneNumber.addTextChangedListener(this);
		etVerificationCode.addTextChangedListener(this);

	}

	String AESPhoneNumber;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.verification:
			phoneNumber = etPhoneNumber.getText().toString();
			if (TextUtils.isEmpty(phoneNumber)) {
				ToastShow("手机号码不能为空");
				return;
			}
			AESPhoneNumber = AESUitls.encode(phoneNumber);
			getVerification(AESPhoneNumber);
			break;
		case R.id.login:
			verificationCode = etVerificationCode.getText().toString();
			if (TextUtils.isEmpty(verificationCode)) {
				ToastShow("验证码不能为空");
				return;
			}
			login();
			break;
		case R.id.back:
			finish();
			break;
		case R.id.xieYi:
			startActivity(new Intent(this, XieYiActivity.class));
			break;
		default:
			break;
		}

	}

	/**
	 * 获取验证码
	 * 
	 */
	private void getVerification(String AESPhoneNumber) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("phone_num", AESPhoneNumber);
		new HttpTask(getApplicationContext(), HttpTask.POST, "users/send_sms", map) {

			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						tv_yz.setClickable(false);
						handler.sendEmptyMessage(1002);
						ToastShow("验证码发送成功");
					} else {
						ToastShow("验证码发送失败");
					}
				} catch (JSONException e) {
					SmartLog.w(TAG, "获取验证码失败", e);
					e.printStackTrace();
				}

			};
		}.run();

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	private Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1001:
				if (!TextUtils.isEmpty(etPhoneNumber.getText().toString())
						&& !TextUtils.isEmpty(etVerificationCode.getText().toString())) {
					tv_login.setClickable(true);
					tv_login.setSelected(true);
				}
				break;
			case 1002:
				handler.post(LoginActivity.this);
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void afterTextChanged(Editable s) {
		if (!TextUtils.isEmpty(s.toString())) {
			handler.sendEmptyMessage(1001);
		}

	}

	private void login() {
		Map<String, String> map = new HashMap<String, String>();
		phoneNumber = etPhoneNumber.getText().toString();
		AESPhoneNumber = AESUitls.encode(phoneNumber);
		verificationCode = etVerificationCode.getText().toString();
		map.put("phone_num", AESPhoneNumber);
		map.put("rand_code", verificationCode);
		new HttpTask(getApplicationContext(), HttpTask.POST, "users/sign_in", map) {

			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						String token = jsonObject.optString("token", "");
						float price = (float) jsonObject.optDouble("send_price", 0);
						String kefutell = jsonObject.optString("phone_num", "");
						LocalCache.getInstance(LoginActivity.this).setToken(token);
						LocalCache.getInstance(LoginActivity.this).setSendPrice(price);
						LocalCache.getInstance(LoginActivity.this).setKeFuTell(kefutell);
						ToastShow("登录成功");
						finish();
					}
				} catch (JSONException e) {
					SmartLog.w(TAG, "获取验证码失败", e);
					e.printStackTrace();
				}

			};
		}.run();

	}

	/**
	 * 计时线程重新发送验证码
	 */
	@Override
	public void run() {
		if (time == 0) {
			tv_yz.setText("验证");
			tv_yz.setClickable(true);
			tv_yz.setBackgroundColor(Color.RED);
			time = 30;
			return;
		}
		tv_yz.setText(time + "秒");
		tv_yz.setBackgroundColor(Color.GRAY);
		time--;
		handler.postDelayed(this, 1000L);

	}

	@Override
	public void onDestroy() {
		// 销毁线程
		handler.removeCallbacks(this);
		super.onDestroy();
	}

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub

	}

}
