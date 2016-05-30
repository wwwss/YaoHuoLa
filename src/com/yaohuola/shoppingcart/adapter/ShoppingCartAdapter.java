package com.yaohuola.shoppingcart.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.yaohuola.YaoHuoLaApplication;
import com.yaohuola.adapter.BaseAdapter;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.data.entity.ShoppingCartEntity;
import com.yaohuola.my.activity.LoginActivity;
import com.yaohuola.task.HttpTask;
import com.yaohuola.view.DeleteDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShoppingCartAdapter extends BaseAdapter<ShoppingCartEntity> {
	private Handler handler;
//	public boolean isDelect;
	//private int oldPosition;
	// 字母位置记录
	private Map<String, Integer> indexMap;

//	public void setDelect(boolean isDelect) {
//		this.isDelect = isDelect;
//	}

	public ShoppingCartAdapter(Context context, List<ShoppingCartEntity> list, Handler handler) {
		super(context, list);
		this.handler = handler;
		indexMap = new HashMap<String, Integer>();
	}

	/**
	 * 记录导航字母位置
	 */
	public void recordIndex() {
		indexMap.clear();
		for (int i = 0; i < list.size(); i++) {
			ShoppingCartEntity shoppingCartEntity = list.get(i);
			if (indexMap.containsKey(shoppingCartEntity.getCategoryName())) {
				continue;
			}
			indexMap.put(shoppingCartEntity.getCategoryName(), i);
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_shopping_cart, null);
			ItemCache itemCache = new ItemCache();
			itemCache.iv_isSelected = (ImageView) convertView.findViewById(R.id.isSelected);
			itemCache.iv_pic = (ImageView) convertView.findViewById(R.id.pic);
			itemCache.tv_productName = (TextView) convertView.findViewById(R.id.name);
			itemCache.tv_categoryName = (TextView) convertView.findViewById(R.id.categoryName);
			itemCache.tv_productDescription = (TextView) convertView.findViewById(R.id.description);
			itemCache.tv_productNumber = (TextView) convertView.findViewById(R.id.number);
			itemCache.tv_stockNumber = (TextView) convertView.findViewById(R.id.stockNumber);
			itemCache.iv_jian = (ImageView) convertView.findViewById(R.id.jian);
			itemCache.iv_jia = (ImageView) convertView.findViewById(R.id.jia);
			itemCache.tv_price = (TextView) convertView.findViewById(R.id.price);
			itemCache.llIsSelected = (LinearLayout) convertView.findViewById(R.id.llIsSelected);
			convertView.setTag(itemCache);

		}
		final ItemCache itemCache = (ItemCache) convertView.getTag();
		final ShoppingCartEntity shoppingCartEntity = list.get(position);
		final ProductEntity productEntity = shoppingCartEntity.getProductEntity();
		// 设置头部是否显示
		int index = indexMap.get(shoppingCartEntity.getCategoryName());
		if (index == position) {
			// 设置头部文字
			itemCache.tv_categoryName.setVisibility(View.VISIBLE);
			itemCache.tv_categoryName.setText(shoppingCartEntity.getCategoryName());
		} else {
			// 隐藏头部信息
			itemCache.tv_categoryName.setVisibility(View.GONE);
		}
		if (TextUtils.isEmpty(productEntity.getPic())) {
			itemCache.iv_pic.setImageResource(R.drawable.default_product_icon);
		} else {
			YaoHuoLaApplication.disPlayFromUrl(productEntity.getPic(), itemCache.iv_pic,
					R.drawable.default_product_icon);
		}
		itemCache.tv_productName.setText(productEntity.getName());
		itemCache.tv_productDescription.setText(productEntity.getDescription());
		itemCache.tv_productNumber.setText(productEntity.getNumber() + "");
		itemCache.tv_price.setText("¥" + productEntity.getPrice());
		if (shoppingCartEntity.isSelecteIsShow()) {
			itemCache.iv_isSelected.setVisibility(View.VISIBLE);
			if (shoppingCartEntity.isSelected()) {
				itemCache.iv_isSelected.setSelected(true);
			} else {
				itemCache.iv_isSelected.setSelected(false);
			}
		} else {
			itemCache.iv_isSelected.setVisibility(View.INVISIBLE);
		}
		itemCache.iv_jian.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int count = productEntity.getNumber();
				if (count > 1) {
					count--;
					updateCartItemNumber(position, shoppingCartEntity.getShoppingcartId(), count);
				} else {
					final DeleteDialog alertDialog = new DeleteDialog(context).builder();
					alertDialog.setTitle("您确定要删除这些商品吗？").setPositiveButton("是", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							alertDialog.dismiss();
							String token = LocalCache.getInstance(context).getToken();
							if (TextUtils.isEmpty(token)) {
								return;
							}
							Map<String, String> map = new HashMap<String, String>();
							map.put("token", token);
							JSONArray jsonArray = new JSONArray();
							jsonArray.put(shoppingCartEntity.getShoppingcartId());
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
											LocalCache.getInstance(context).setCartTotalNum(cart_total_num);
											list.remove(position);
											notifyDataSetChanged();
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}

								};
							}.run();
						}
					}).setNegativeButton("否", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							alertDialog.dismiss();
						}
					});
					alertDialog.show();
				}
			}
		});
		itemCache.iv_jia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int count = productEntity.getNumber();
				if (count < productEntity.getStock_num()) {
					count++;
					updateCartItemNumber(position, shoppingCartEntity.getShoppingcartId(), count);

				}
			}
		});
		itemCache.llIsSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int count = productEntity.getNumber();
				if (count > productEntity.getStock_num()) {
					return;
				}
				if (shoppingCartEntity.isSelected()) {
					shoppingCartEntity.setSelected(false);
				} else {
					shoppingCartEntity.setSelected(true);
				}
				notifyDataSetChanged();
			}
		});
		itemCache.tv_productNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateCartItemNumberShow(position, productEntity.getNumber(), productEntity.getStock_num());
			}
		});
		if (productEntity.getNumber() > productEntity.getStock_num()) {
			itemCache.tv_stockNumber.setVisibility(View.VISIBLE);
			itemCache.tv_stockNumber.setText("库存" + productEntity.getStock_num() + "");
		} else {
			itemCache.tv_stockNumber.setVisibility(View.GONE);
		}
		//oldPosition = position;
		return convertView;
	}

	public class ItemCache {
		private ImageView iv_isSelected;
		private ImageView iv_pic;
		private TextView tv_categoryName;
		private TextView tv_productName;
		private TextView tv_stockNumber;
		private TextView tv_productDescription;// 产品描述
		private ImageView iv_jian;// 减
		private ImageView iv_jia;// 加
		private TextView tv_productNumber;// 数量
		private TextView tv_price;// 价格
		private LinearLayout llIsSelected;

	}

	@Override
	public void notifyDataSetChanged() {
		Message message = new Message();
		double total = 0;
		for (int i = 0; i < list.size(); i++) {
			ShoppingCartEntity shoppingCartEntity = list.get(i);
			final ProductEntity productEntity = shoppingCartEntity.getProductEntity();
			if (shoppingCartEntity.isSelected()) {
				total += productEntity.getNumber() * productEntity.getPrice();
			}
		}

		message.what = 1001;
		message.obj = total;
		handler.sendMessage(message);
		super.notifyDataSetChanged();
	}

	/**
	 * 修改购物车产品数量方法
	 */
	private void updateCartItemNumber(final int position, String unique_id, final int product_num) {
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			context.startActivity(new Intent(context, LoginActivity.class));
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("unique_id", unique_id);
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
						LocalCache.getInstance(context).setCartTotalNum(cart_total_num);
						list.get(position).getProductEntity().setNumber(product_num);
						notifyDataSetChanged();
					}  
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();

	}

	/**
	 * 修改数量
	 */
	private void updateCartItemNumberShow(final int position, int number, final int stock_num) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.show();
		final Window window = alertDialog.getWindow();
		window.clearFlags(
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		window.setContentView(R.layout.dialog_update_item_num);
		TextView tv_jian = (TextView) window.findViewById(R.id.jian);
		TextView tv_jia = (TextView) window.findViewById(R.id.jia);
		final EditText et_number = (EditText) window.findViewById(R.id.number);
		et_number.setText(number + "");
		tv_jian.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(et_number.getText().toString()))
					return;
				int number = Integer.valueOf(et_number.getText().toString());
				if (number > 1) {
					number--;
					et_number.setText(number + "");
				}

			}
		});
		tv_jia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(et_number.getText().toString()))
					return;
				int number = Integer.valueOf(et_number.getText().toString());
				if (number < stock_num) {
					number++;
					et_number.setText(number + "");
				}

			}
		});
		window.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialog.dismiss();

			}
		});
		window.findViewById(R.id.confirm).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(et_number.getText().toString()))
					return;
				int number = Integer.valueOf(et_number.getText().toString());
				if (number > 0 && number <= stock_num) {
					updateCartItemNumber(position, list.get(position).getShoppingcartId(), number);
					alertDialog.dismiss();
				}
			}
		});
		et_number.setFocusable(true);
		et_number.setFocusableInTouchMode(true);
		et_number.requestFocus();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() { // 让软键盘延时弹出，以更好的加载Activity

			public void run() {
				InputMethodManager inputManager = (InputMethodManager) et_number.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(et_number, 0);
			}

		}, 300);

	}
}
