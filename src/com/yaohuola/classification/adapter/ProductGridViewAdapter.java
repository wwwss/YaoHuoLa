package com.yaohuola.classification.adapter;

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
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.interfaces.AddShoppingCartListener;
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

public class ProductGridViewAdapter extends BaseAdapter<ProductEntity> {

	private AddShoppingCartListener listener;

	public ProductGridViewAdapter(Context context, List<ProductEntity> list, AddShoppingCartListener listener) {
		super(context, list);
		this.listener = listener;
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
		final ProductEntity productEntity = list.get(position);
		if (TextUtils.isEmpty(productEntity.getPic())) {
			itemCache.ivPic.setImageResource(R.drawable.default_product_icon);
		} else {
			YaoHuoLaApplication.disPlayFromUrl(productEntity.getPic(), itemCache.ivPic,
					R.drawable.default_product_icon);
		}
		itemCache.tvName.setText(productEntity.getName());
		itemCache.tvDescription.setText(productEntity.getSpec());
		itemCache.tvPrice.setText("¥" + productEntity.getPrice());
		itemCache.ivAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (productEntity.getStock_num() > 0) {
					if (productEntity.getNumber() == 0) {
						addToCart(context, productEntity, itemCache);
					} else {
						updateCartItemNumber(productEntity.getNumber() + 1, productEntity, itemCache);
					}
				}
			}
		});
		itemCache.ivSubtract.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (productEntity.getNumber() > 1) {
					updateCartItemNumber(productEntity.getNumber() - 1, productEntity, itemCache);
				} else if (productEntity.getNumber() == 1) {
					delete(productEntity, itemCache);
				}
			}
		});
		if (productEntity.getStock_num() > 0) {
			itemCache.ivAdd.setImageResource(R.drawable.add_icon);
		} else {
			itemCache.ivAdd.setImageResource(R.drawable.shopping_cart_grey);
		}
		if (productEntity.getNumber() > 0) {
			itemCache.tvNumber.setText(productEntity.getNumber() + "");
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
	private void addToCart(Context context, final ProductEntity productEntity, final ItemCache itemCache) {
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			context.startActivity(new Intent(context, LoginActivity.class));
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("pro_unique_id", productEntity.getId());
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
					int cart_total_num = jsonObject.optInt("cart_total_num", -1);
					if (code == 0) {
						productEntity.setNumber(1);
						productEntity.setCart_item_unique_id(jsonObject.optString("unique_id", ""));
						updateItem(productEntity, itemCache,cart_total_num);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();
	}

	/**
	 * 修改购物车产品数量的方法
	 */
	private void updateCartItemNumber(final int product_num, final ProductEntity productEntity,
			final ItemCache itemCache) {
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			context.startActivity(new Intent(context, LoginActivity.class));
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("unique_id", productEntity.getCart_item_unique_id());
		map.put("product_num", product_num + "");
		new HttpTask(context, HttpTask.POST, "v1/cart_items/edit_product_num", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					int cart_total_num = jsonObject.optInt("cart_total_num", -1);
					if (code == 0) {
						productEntity.setNumber(product_num);
						updateItem(productEntity, itemCache,cart_total_num);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();

	}

	private void delete(final ProductEntity productEntity, final ItemCache itemCache) {
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			context.startActivity(new Intent(context, LoginActivity.class));
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(productEntity.getCart_item_unique_id());
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
					int cart_total_num = jsonObject.optInt("cart_total_num", -1);
					if (code == 0) {
						productEntity.setNumber(0);
						updateItem(productEntity, itemCache,cart_total_num);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			};
		}.run();
	}

	/**
	 * 刷新item
	 */
	private void updateItem(ProductEntity productEntity, ItemCache itemCache,int cart_total_num) {
		int old_cart_total_num=LocalCache.getInstance(context).getCartTotalNum();
		if (cart_total_num>old_cart_total_num) {
			listener.addSucceed(productEntity.getNumber());
		}
		LocalCache.getInstance(context).setCartTotalNum(cart_total_num);
		if (productEntity.getNumber() > 0) {
			itemCache.tvNumber.setText(productEntity.getNumber() + "");
			itemCache.tvNumber.setVisibility(View.VISIBLE);
			itemCache.ivSubtract.setVisibility(View.VISIBLE);
		} else {
			itemCache.tvNumber.setVisibility(View.GONE);
			itemCache.ivSubtract.setVisibility(View.GONE);
		}
		
	}
}
