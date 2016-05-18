package com.yaohuola.homepage.adapter;

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
import com.yaohuola.data.entity.HotSaleEntity;
import com.yaohuola.my.activity.LoginActivity;
import com.yaohuola.task.HttpTask;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HotSaleAdapter extends BaseAdapter<HotSaleEntity> {

	public HotSaleAdapter(Context context, List<HotSaleEntity> list) {
		super(context, list);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_hot_sale, null);
			ItemCache itemCache = new ItemCache();
			itemCache.tvName = (TextView) convertView.findViewById(R.id.name);
			itemCache.ivPic = (ImageView) convertView.findViewById(R.id.pic);
			itemCache.tvDescription = (TextView) convertView.findViewById(R.id.description);
			itemCache.tvPrice = (TextView) convertView.findViewById(R.id.price);
			itemCache.tvNumber = (TextView) convertView.findViewById(R.id.number);
			itemCache.ivAdd = (ImageView) convertView.findViewById(R.id.addToCart);
			itemCache.ivSubtract = (ImageView) convertView.findViewById(R.id.subtract);
			convertView.setTag(itemCache);
		}
		final ItemCache itemCache = (ItemCache) convertView.getTag();
		final HotSaleEntity hotSaleEntity = list.get(position);
		if (TextUtils.isEmpty(hotSaleEntity.getPic())) {
			itemCache.ivPic.setImageResource(R.drawable.default_product_icon);
		} else {
			YaoHuoLaApplication.disPlayFromUrl(hotSaleEntity.getPic(), itemCache.ivPic,
					R.drawable.default_product_icon);
		}
		itemCache.tvName.setText(hotSaleEntity.getName());
		itemCache.tvDescription.setText(hotSaleEntity.getSpec());
		itemCache.tvPrice.setText("¥" + hotSaleEntity.getPrice());
		itemCache.ivAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (hotSaleEntity.getStock_num() > 0) {
					if (hotSaleEntity.getNumber() == 0) {
						addToCart(context, hotSaleEntity, itemCache);
					} else {
						updateCartItemNumber(hotSaleEntity.getNumber() + 1, hotSaleEntity, itemCache);
					}
				}
			}
		});
		itemCache.ivSubtract.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (hotSaleEntity.getNumber() > 1) {
					updateCartItemNumber(hotSaleEntity.getNumber() - 1, hotSaleEntity, itemCache);
				} else if (hotSaleEntity.getNumber() == 1) {
					delete(hotSaleEntity, itemCache);
				}
			}
		});
		if (hotSaleEntity.getStock_num() > 0) {
			itemCache.ivAdd.setImageResource(R.drawable.add_icon);
		} else {
			itemCache.ivAdd.setImageResource(R.drawable.shopping_cart_grey);
		}
		if (hotSaleEntity.getNumber() > 0) {
			itemCache.tvNumber.setText(hotSaleEntity.getNumber() + "");
			itemCache.tvNumber.setVisibility(View.VISIBLE);
			itemCache.ivSubtract.setVisibility(View.VISIBLE);
		} else {
			itemCache.tvNumber.setVisibility(View.GONE);
			itemCache.ivSubtract.setVisibility(View.GONE);
		}
		return convertView;
	}

	private class ItemCache {
		private ImageView ivPic;// 图片
		private TextView tvName;// 名称
		private TextView tvDescription;// 描述
		private TextView tvPrice;// 价格
		private ImageView ivAdd;// 加入购物车
		private ImageView ivSubtract;// 加入购物车
		private TextView tvNumber;// 数量
	}

	/**
	 * 加入购物车的方法
	 */
	private void addToCart(Context context, final HotSaleEntity hotSaleEntity, final ItemCache itemCache) {
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			context.startActivity(new Intent(context, LoginActivity.class));
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("pro_unique_id", hotSaleEntity.getId());
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
						hotSaleEntity.setNumber(1);
						hotSaleEntity.setCart_item_unique_id(jsonObject.optString("unique_id", ""));
						Toast.makeText(context, "加入购物车成功", Toast.LENGTH_SHORT).show();
						updateItem(hotSaleEntity, itemCache);
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

	/**
	 * 加入购物车的方法
	 */
	private void updateCartItemNumber(final int product_num, final HotSaleEntity hotSaleEntity,
			final ItemCache itemCache) {
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			context.startActivity(new Intent(context, LoginActivity.class));
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("unique_id", hotSaleEntity.getCart_item_unique_id());
		map.put("product_num", product_num + "");
		new HttpTask(context, HttpTask.POST, "v1/cart_items/edit_product_num", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						hotSaleEntity.setNumber(product_num);
						updateItem(hotSaleEntity, itemCache);
					} else if (code == 3) {
						Toast.makeText(context, "库存不足", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "修改数量失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();

	}

	private void delete(final HotSaleEntity hotSaleEntity, final ItemCache itemCache) {
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			context.startActivity(new Intent(context, LoginActivity.class));
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(hotSaleEntity.getCart_item_unique_id());
		if (jsonArray.length() == 0)
			return;
		map.put("unique_ids", jsonArray.toString());
		new HttpTask(context, HttpTask.DELETE, "v1/cart_items", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						hotSaleEntity.setNumber(0);
						updateItem(hotSaleEntity, itemCache);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			};
		}.run();
	}

	/**
	 * 刷新指定item
	 * 
	 * @param index
	 *            item在listview中的位置
	 */
	private void updateItem(HotSaleEntity hotSaleEntity, ItemCache itemCache) {
		if (hotSaleEntity.getNumber() > 0) {
			itemCache.tvNumber.setText(hotSaleEntity.getNumber() + "");
			itemCache.tvNumber.setVisibility(View.VISIBLE);
			itemCache.ivSubtract.setVisibility(View.VISIBLE);
		} else {
			itemCache.tvNumber.setVisibility(View.GONE);
			itemCache.ivSubtract.setVisibility(View.GONE);
		}
	}
}
