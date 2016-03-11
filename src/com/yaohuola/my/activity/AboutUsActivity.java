package com.yaohuola.my.activity;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;

import android.view.View;

/**
 * 
 * @author admin
 * 关于我们
 */
public class AboutUsActivity extends BaseActivity {

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_about_us);

	}

	@Override
	public void initView() {
		findViewById(R.id.back).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}

	}

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub
		
	}

}
