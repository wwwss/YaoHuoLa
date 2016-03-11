package com.yaohuola.my.activity;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.yaohuola.constants.UrlConstants;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class XieYiActivity extends BaseActivity {
	private WebView webView;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_xieyi);
	}

	@Override
	public void initView() {
		webView = (WebView) findViewById(R.id.webView);
		webView.loadUrl(UrlConstants.AGREEMENT_ADDRESS);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setUseWideViewPort(false);// 设置此属性，可任意比例缩放
		webView.getSettings().setLoadWithOverviewMode(true);
		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
				view.loadUrl(url);
				return true;
			}
		});
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
	public void onDestroy() {
		webView.destroy();
		super.onDestroy();
	}

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub
		
	}
}
