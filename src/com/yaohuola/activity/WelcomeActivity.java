package com.yaohuola.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.task.HttpTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class WelcomeActivity extends Activity implements AnimationListener {

	private ImageView imageView = null;
	private Animation alphaAnimation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView();
	}

	public void setContentView() {
		// requestWindowFeature(Window.FEATURE_NO_TITLE); // 无title
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏
		// 初始化布局文件
		setContentView(R.layout.activity_welcome);
		initView();
	}

	public void initView() {
		imageView = (ImageView) findViewById(R.id.welcome_image_view);
		alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.welcome_alpha);
		alphaAnimation.setFillEnabled(true);
		alphaAnimation.setFillAfter(true);
		imageView.setAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(this);

	}

	/**
	 * 动画结束时
	 */
	@Override
	public void onAnimationEnd(Animation arg0) {
		boolean isFirst = LocalCache.getInstance(getApplicationContext()).getIsFirst();
		if (!isFirst) {
			startActivity(new Intent(this, GuidePageActivity.class));
		} else {
			startActivity(new Intent(this, MainActivity.class));
		}
		finish();

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		String token = LocalCache.getInstance(this).getToken();
		if (TextUtils.isEmpty(token)) {
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		new HttpTask(this, HttpTask.POST, "users/token", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				} else {
					LocalCache.getInstance(WelcomeActivity.this).clearToken();
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						String token = jsonObject.optString("token", "");
						float price = (float) jsonObject.optDouble("send_price", 0);
						String kefutell = jsonObject.optString("phone_num", "");
						LocalCache.getInstance(WelcomeActivity.this).setToken(token);
						LocalCache.getInstance(WelcomeActivity.this).setSendPrice(price);
						LocalCache.getInstance(WelcomeActivity.this).setKeFuTell(kefutell);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();
	}
}
