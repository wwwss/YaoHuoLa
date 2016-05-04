package com.yaohuola.my.activity;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.TextView;

/**
 * 
 * @author admin 关于我们
 */
public class AboutUsActivity extends BaseActivity {
	private TextView tv_appName;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_about_us);

	}

	@Override
	public void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		tv_appName = (TextView) findViewById(R.id.appName);
		tv_appName.setText("要货啦" + getVersion());

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

	/**
	 * 获取版本号的方法
	 * 
	 * @return 当前应用的版本号 4
	 */
	public String getVersion() {
		try {
			PackageManager manager = getPackageManager();
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
