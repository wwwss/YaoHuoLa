package com.yaohuola.classification.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.library.view.SAGridView;
import com.yaohuola.adapter.BaseAdapter;
import com.yaohuola.classification.activity.ProductAitivity;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.data.entity.SmallClassifyEntity;
import com.yaohuola.task.HttpTask;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/**
 * 
 * @author admin 全部分类适配器
 */
public class SmallClassifyAdapter extends BaseAdapter<SmallClassifyEntity> {

	private ProductAdapter adapter;

	public SmallClassifyAdapter(Context context, List<SmallClassifyEntity> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_small_classify, null);
			ItemCache itemCache = new ItemCache();
			itemCache.tvName = (TextView) convertView.findViewById(R.id.name);
			itemCache.tvAll = (TextView) convertView.findViewById(R.id.all);
			itemCache.productGridView = (SAGridView) convertView.findViewById(R.id.productGridView);
			convertView.setTag(itemCache);
		}
		ItemCache itemCache = (ItemCache) convertView.getTag();
		final SmallClassifyEntity smallClassifyEntity = list.get(position);
		itemCache.tvName.setText(smallClassifyEntity.getTitle());
		adapter = new ProductAdapter(context, smallClassifyEntity.getProductEntities());
		itemCache.productGridView.setAdapter(adapter);
		itemCache.productGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent intent = new Intent(context, ProductAitivity.class);
				intent.putExtra("smallClassifyEntity", smallClassifyEntity);
				intent.putExtra("index", position);
				context.startActivity(intent);
			}
		});
		itemCache.tvAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getSmallClassify(smallClassifyEntity.getId(), smallClassifyEntity.getTitle());
			}
		});

		return convertView;
	}

	private class ItemCache {
		private TextView tvName;// 名称
		private TextView tvAll;// 全部
		private SAGridView productGridView;// 产品

	}

	/**
	 * 全部分类
	 */
	public void getSmallClassify(String unique_id, final String title) {
		new HttpTask(context, HttpTask.GET, "products/sub_category/" + unique_id, null) {
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
						smallClassifyEntity.setTitle(title);
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
							productEntities.add(productEntity);
						}
						smallClassifyEntity.setProductEntities(productEntities);
						Intent intent = new Intent(context, ProductAitivity.class);
						intent.putExtra("smallClassifyEntity", smallClassifyEntity);
						intent.putExtra("title", title);
						intent.putExtra("index", -1);
						context.startActivity(intent);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();

	}

}
