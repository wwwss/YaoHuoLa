package com.yaohuola.my.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.yaohuola.YaoHuoLaApplication;
import com.yaohuola.adapter.BaseAdapter;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.OrderEntity;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.my.activity.LoginActivity;
import com.yaohuola.task.HttpTask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderProductListAdapter extends BaseAdapter<ProductEntity> {

	public OrderProductListAdapter(Context context, List<ProductEntity> list) {
		super(context, list);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_order_product_list, null);
			ItemCache itemCache = new ItemCache();
			itemCache.iv_pic = (ImageView) convertView.findViewById(R.id.pic);
			itemCache.tv_productName = (TextView) convertView.findViewById(R.id.name);
			itemCache.tv_productDescription = (TextView) convertView.findViewById(R.id.description);
			itemCache.tv_productPrcieAndNumber = (TextView) convertView.findViewById(R.id.prcieAndNumber);
			itemCache.tv_againToBuy = (TextView) convertView.findViewById(R.id.againToBuy);
			convertView.setTag(itemCache);

		}
		final ItemCache itemCache = (ItemCache) convertView.getTag();
		final ProductEntity productEntity = list.get(position);
		if (TextUtils.isEmpty(productEntity.getPic())) {
			itemCache.iv_pic.setImageResource(R.drawable.default_product_icon);
		} else {
			YaoHuoLaApplication.disPlayFromUrl(productEntity.getPic(), itemCache.iv_pic,
					R.drawable.default_product_icon);
		}
		itemCache.tv_productName.setText(productEntity.getName());
		itemCache.tv_productDescription.setText(productEntity.getDescription());
		itemCache.tv_productPrcieAndNumber.setText("¥" + productEntity.getPrice() + "  x" + productEntity.getNumber());		
		itemCache.tv_againToBuy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (productEntity.getStock_num() > 0) {
					addToCart(productEntity.getId(), productEntity.getNumber());
				}
			}
		});
		if (productEntity.getStock_num() > 0) {
			itemCache.tv_againToBuy.setBackgroundColor(Color.parseColor("#ff3434"));
		} else {
			itemCache.tv_againToBuy.setBackgroundColor(Color.parseColor("#eeeeee"));
		}

		return convertView;
	}

	public class ItemCache {
		private ImageView iv_pic;
		private TextView tv_productName;
		private TextView tv_productDescription;// 产品描述
		private TextView tv_productPrcieAndNumber;// 价格和数量
		private TextView tv_againToBuy;

	}

	public interface CancelListener {
		void cancelSuccess();
	}

	public List<OrderEntity> JsonFromList(JSONObject jsonObject) {
		List<OrderEntity> list = new ArrayList<OrderEntity>();
		JSONArray jsonArray = jsonObject.optJSONArray("list");
		if (jsonArray != null) {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject2 = jsonArray.optJSONObject(i);
				if (jsonObject2 == null) {
					continue;
				}
				OrderEntity order = new OrderEntity();

				list.add(order);
			}
		}
		return list;

	}

	/**
	 * 加入购物车的方法
	 */
	private void addToCart(String unique_id, int product_num) {
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			context.startActivity(new Intent(context, LoginActivity.class));
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("pro_unique_id", unique_id);
		map.put("product_num", product_num + "");
		new HttpTask(context, HttpTask.POST, "v1/cart_items", map) {
			protected void onPostExecute(String result) {
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
