
package com.yaohuola.task;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.library.task.BaseTask;
import com.library.uitls.DialogUtils;
import com.library.uitls.HttpUtils;
import com.library.uitls.NetUtils;
import com.library.uitls.SmartLog;
import com.yaohuola.constants.UrlConstants;
import com.yaohuola.data.cache.LocalCache;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

public class HttpTask extends BaseTask {

	public static int GET = 0;
	public static int POST = 1;
	public static int PUT = 2;
	public static int DELETE = 3;
	public Context context;// 上下文
	protected String error_message;
	private Map<String, String> map;
	private String func;
	private int type;// 0是get，1是post 2是put 3是DELETE
	private int error_code;
	protected Dialog dialog;
	public HttpTask(Context context, int type, String func, Map<String, String> map) {
		dialog = DialogUtils.createLoadingDialog(context);
		if (map == null) {
			map = new HashMap<String, String>();
		}
		this.context = context;
		this.func = func;
		this.map = map;
		this.type = type;
	}

	@Override
	protected String doInBackground(Void... params) {
		String jsonString = null;
		if (NetUtils.isConnected(context)) {
			if (type == 0) {
				jsonString = HttpUtils.sendGet2(UrlConstants.SERVERAPI + func, map);
			} else if (type == 1) {
				jsonString = HttpUtils.sendPost2(UrlConstants.SERVERAPI + func, map);
			} else if (type == 2) {
				jsonString = HttpUtils.sendPut2(UrlConstants.SERVERAPI + func, map);
			} else if (type == 3) {
				jsonString = HttpUtils.sendDelete2(UrlConstants.SERVERAPI + func, map);
			}
			if (!TextUtils.isEmpty(jsonString)) {
				try {
					JSONObject jsonObject = new JSONObject(jsonString);
					error_code = jsonObject.optInt("result", -1);
					error_message = jsonObject.optString("errmsg", "");
					if (error_code != 0) {
						publishProgress();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return jsonString;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		switch (error_code) {
		case 2:
			LocalCache.getInstance(context).clearToken();
			break;
		default:
			if (!TextUtils.isEmpty(error_message)) {
				Toast.makeText(context, error_message, Toast.LENGTH_SHORT).show();
				SmartLog.i("HttpTask", "错误信息是：" + error_message);
			}
			break;
		}
	}

}
