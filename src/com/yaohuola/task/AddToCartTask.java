package com.yaohuola.task;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.my.activity.LoginActivity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

public class AddToCartTask {
	/**
	 * 加入购物车的方法
	 */
	public static void addToCart(Context context, String unique_id) {
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			context.startActivity(new Intent(context, LoginActivity.class));
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("pro_unique_id", unique_id);
		map.put("product_num", "1");
		new HttpTask(context, HttpTask.POST, "v1/cart_items", map) {
			protected void onPreExecute() {
				dialog.show();
			};

			protected void onPostExecute(String result) {
				dialog.dismiss();
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						Toast.makeText(context, "加入购物车成功", Toast.LENGTH_SHORT).show();
					} else if (code == 3) {
						Toast.makeText(context, "库存不足", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "加入购物车失败", Toast.LENGTH_SHORT).show();

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();

	}
}
