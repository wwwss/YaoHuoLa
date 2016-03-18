package com.yaohuola.classification.view;

import com.android.yaohuola.R;
import com.library.view.photoview.PhotoView;
import com.yaohuola.YaoHuoLaApplication;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

public class PicturePreviewAlertDialog extends AlertDialog {
	private String imageUrl;

	public PicturePreviewAlertDialog(Context context,String imageUrl) {
		super(context);
		this.imageUrl=imageUrl;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_preview);
		PhotoView imageView=(PhotoView) findViewById(R.id.photoView);
		YaoHuoLaApplication.disPlayFromUrl(imageUrl, imageView, R.drawable.default_banner_icon);
	}

	
}
