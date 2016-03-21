package com.yaohuola.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.library.uitls.AppUtils;
import com.yaohuola.classification.activity.ProductAitivity;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.data.entity.SmallClassifyEntity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

public class SearchTask {
	/**
	 * 搜索
	 */
	public static void search(Context context, final String keyWord) {
		if (AppUtils.isFastClick()) {
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("key_word", keyWord);
		new HttpTask(context, HttpTask.POST, "products/search", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						JSONArray jsonArray = jsonObject.optJSONArray("products");
						if (jsonArray == null) {
							return;
						}
						SmallClassifyEntity smallClassifyEntity = new SmallClassifyEntity();
						smallClassifyEntity.setTitle(keyWord);
						List<ProductEntity> productEntities = new ArrayList<ProductEntity>();
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = jsonArray.optJSONObject(i);
							if (jsonObject2 == null) {
								continue;
							}
							ProductEntity productEntity = new ProductEntity();
							productEntity.setId(jsonObject2.optString("unique_id", ""));
							productEntity.setName(jsonObject2.optString("name", ""));
							productEntity.setPic(jsonObject2.optString("image", ""));
							productEntity.setDescription(jsonObject2.optString("desc", ""));
							productEntity.setPrice(jsonObject2.optDouble("price", 0));
							productEntity.setSpec(jsonObject2.optString("spec", ""));
							productEntity.setStock_num(jsonObject2.optInt("stock_num", 0));
							productEntities.add(productEntity);
						}
						smallClassifyEntity.setProductEntities(productEntities);
						Intent intent = new Intent(context, ProductAitivity.class);
						intent.putExtra("smallClassifyEntity", smallClassifyEntity);
						intent.putExtra("title", keyWord);
						intent.putExtra("index", -1);
						context.startActivity(intent);
					} else {
						Toast.makeText(context, "没有找到你要搜索的商品", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();

	}
}
