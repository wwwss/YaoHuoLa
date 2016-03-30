package com.yaohuola.classification.activity;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.library.view.photoview.PhotoView;
import com.yaohuola.YaoHuoLaApplication;

import android.view.View;

public class PicturePreviewActivity extends BaseActivity{
	
	private String imageUrl;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_picture_preview);

	}

	@Override
	public void initView() {
		imageUrl=getIntent().getStringExtra("imageUrl");
		PhotoView imageView=(PhotoView) findViewById(R.id.photoView);
		YaoHuoLaApplication.disPlayFromUrl(imageUrl, imageView, R.drawable.default_banner_icon);
		imageView.setOnClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
	}

	@Override
	public void refreshData() {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.photoView:
			finish();
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}

	}

}
