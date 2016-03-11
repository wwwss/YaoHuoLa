
package com.yaohuola.task;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.library.task.BaseTask;
import com.library.uitls.HttpUtils;
import com.library.uitls.NetUtils;
import com.yaohuola.constants.UrlConstants;
import com.yaohuola.data.cache.LocalCache;

import android.content.Context;
import android.text.TextUtils;

public class HttpTask extends BaseTask {

	public static int GET = 0;
	public static int POST = 1;
	public static int PUT = 2;
	public static int DELETE = 3;
	public Context context;// 上下文
	// protected String error_message;
	private Map<String, String> map;
	private String func;
	// private int error_type;// 1001是服务器异常，1002是请求失败
	private int type;// 0是get，1是post 2是put

	public HttpTask(Context context, int type, String func, Map<String, String> map) {
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
					int code = jsonObject.optInt("result", -1);
					if (code == 2) {
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
//		Intent intent = new Intent(new Intent(context, LoginActivity.class));
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(intent);
		LocalCache.getInstance(context).clearToken();
	}

}
