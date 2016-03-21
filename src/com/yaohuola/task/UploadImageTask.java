package com.yaohuola.task;

import java.util.Map;

import com.library.interfaces.UploadPicturesCallBack;
import com.library.uitls.AppUtils;
import com.library.uitls.PictureProcessingUtils;
import com.library.uitls.SmartLog;
import com.yaohuola.constants.UrlConstants;
import com.yaohuola.interfaces.UploadImageListenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

public class UploadImageTask {
	public static void uploadImageFile(final Activity context,String imagePath,final UploadImageListenter listenter,Map<String, String> params) {
		if (AppUtils.isFastClick()) {
			return;
		}
		try {
			PictureProcessingUtils.uploadImageFile(UrlConstants.UPLOADPICTURES,imagePath, params,new UploadPicturesCallBack() {
						private ProgressDialog dialog;
						@Override
						public void onUploadSuccess(String result) {
							dialog.dismiss();
							Toast.makeText(context, "提交成功",
									Toast.LENGTH_SHORT).show();
							listenter.onUploadSuccess(result);
						}
						@Override
						public void onUploadStart() {
							dialog = new ProgressDialog(context);
							dialog.setMessage("正在提交信息...");
							dialog.setCancelable(false);
							dialog.show();
						}
						@Override
						public void onUploadFailure() {
							dialog.dismiss();
							Toast.makeText(context, "提交失败",
									Toast.LENGTH_SHORT).show();
						}
					});
		} catch (Exception e) {
			SmartLog.w("UploadImageTask", "上传文件失败", e);
		}
	}
}
