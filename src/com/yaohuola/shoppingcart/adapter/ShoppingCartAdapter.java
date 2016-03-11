package com.yaohuola.shoppingcart.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.yaohuola.YaoHuoLaApplication;
import com.yaohuola.adapter.BaseAdapter;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.my.activity.LoginActivity;
import com.yaohuola.task.HttpTask;

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
import android.widget.Toast;

public class ShoppingCartAdapter extends BaseAdapter<ProductEntity> {
	private Handler handler;
	public boolean isDelect;

	public void setDelect(boolean isDelect) {
		this.isDelect = isDelect;
	}

	public ShoppingCartAdapter(Context context, List<ProductEntity> list, Handler handler) {
		super(context, list);
		this.handler = handler;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_shopping_cart, null);
			ItemCache itemCache = new ItemCache();
			itemCache.iv_isSelected = (ImageView) convertView.findViewById(R.id.isSelected);
			itemCache.iv_pic = (ImageView) convertView.findViewById(R.id.pic);
			itemCache.tv_productName = (TextView) convertView.findViewById(R.id.name);
			itemCache.tv_productDescription = (TextView) convertView.findViewById(R.id.description);
			itemCache.tv_productNumber = (TextView) convertView.findViewById(R.id.number);
			itemCache.tv_stockNumber = (TextView) convertView.findViewById(R.id.stockNumber);
			itemCache.tv_jian = (TextView) convertView.findViewById(R.id.jian);
			itemCache.tv_jia = (TextView) convertView.findViewById(R.id.jia);
			itemCache.tv_price = (TextView) convertView.findViewById(R.id.price);
			itemCache.llIsSelected = (LinearLayout) convertView.findViewById(R.id.llIsSelected);
			convertView.setTag(itemCache);

		}
		final ItemCache itemCache = (ItemCache) convertView.getTag();
		final ProductEntity productEntity = list.get(position);
		YaoHuoLaApplication.disPlayFromUrl(productEntity.getPic(), itemCache.iv_pic,
				R.drawable.default_order_product_icon);
		itemCache.tv_productName.setText(productEntity.getName());
		itemCache.tv_productDescription.setText(productEntity.getDescription());
		itemCache.tv_productNumber.setText(productEntity.getNumber() + "");
		itemCache.tv_price.setText("¥" + productEntity.getPrice());
		if (productEntity.isSelecteIsShow()) {
			itemCache.iv_isSelected.setVisibility(View.VISIBLE);
			if (productEntity.isSelected()) {
				itemCache.iv_isSelected.setSelected(true);
			} else {

				itemCache.iv_isSelected.setSelected(false);
			}
		} else {
			itemCache.iv_isSelected.setVisibility(View.INVISIBLE);
		}
		itemCache.tv_jian.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int count = productEntity.getNumber();
				if (count > 1) {
					count--;
					updateCartItemNumber(position, productEntity.getId(), count);
				}
			}

		});
		itemCache.tv_jia.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int count = productEntity.getNumber();
				if (count < productEntity.getStock_num()) {
					count++;
					updateCartItemNumber(position, productEntity.getId(), count);

				}
			}
		});
		itemCache.llIsSelected.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!isDelect) {
					int count = productEntity.getNumber();
					if (count > productEntity.getStock_num()) {
						return;
					}
				}
				if (productEntity.isSelected()) {
					productEntity.setSelected(false);
				} else {
					productEntity.setSelected(true);
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

		return convertView;
	}

	public class ItemCache {
		private ImageView iv_isSelected;
		private ImageView iv_pic;
		private TextView tv_productName;
		private TextView tv_stockNumber;
		private TextView tv_productDescription;// 产品描述
		private TextView tv_jian;// 减
		private TextView tv_jia;// 加
		private TextView tv_productNumber;// 数量
		private TextView tv_price;// 价格
		private LinearLayout llIsSelected;

	}

	@Override
	public void notifyDataSetChanged() {
		Message message = new Message();
		double total = 0;
		for (int i = 0; i < list.size(); i++) {
			ProductEntity productEntity = list.get(i);
			if (productEntity.isSelected()) {
				total += productEntity.getNumber() * productEntity.getPrice();
			}
		}

		message.what = 1001;
		message.obj = total;
		handler.sendMessage(message);
		super.notifyDataSetChanged();
	}

	/**
	 * 加入购物车的方法
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
		new HttpTask(context, HttpTask.POST, "cart_items/edit_product_num", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						list.get(position).setNumber(product_num);
						notifyDataSetChanged();
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
				int number = Integer.valueOf(et_number.getText().toString());
				if (number > 0 && number <= stock_num) {
					updateCartItemNumber(position, list.get(position).getId(), number);
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
