package com.yaohuola.my.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.yaohuola.adapter.BaseAdapter;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.AddrEntity;
import com.yaohuola.data.entity.OrderEntity;
import com.yaohuola.my.activity.LoginActivity;
import com.yaohuola.task.HttpTask;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class OrderListAdapter extends BaseAdapter<OrderEntity> {
	public OrderListAdapter(Context context, List<OrderEntity> list) {
		super(context, list);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_order_list, null);
			ItemCache itemCache = new ItemCache();
			itemCache.tv_orderNumber = (TextView) convertView.findViewById(R.id.oderNumber);
			itemCache.tv_orderStatus = (TextView) convertView.findViewById(R.id.orderStatus);
			itemCache.tv_createTime = (TextView) convertView.findViewById(R.id.createTime);
			itemCache.tv_consignee = (TextView) convertView.findViewById(R.id.consignee);
			itemCache.tv_productNumber = (TextView) convertView.findViewById(R.id.productNumber);
			itemCache.tv_againToBuy = (TextView) convertView.findViewById(R.id.againToBuy);
			convertView.setTag(itemCache);

		}
		final ItemCache itemCache = (ItemCache) convertView.getTag();
		final OrderEntity order = list.get(position);
		itemCache.tv_orderNumber.setText("订单编号：" + order.getSn());
		itemCache.tv_createTime.setText("创建时间：" + order.getCreate_at());
		AddrEntity addrEntity = order.getAddrEntity();
		if (addrEntity != null) {
			itemCache.tv_consignee.setText("收货人：" + addrEntity.getName());
		}
		itemCache.tv_productNumber.setText("共" + order.getProductNumber() + " 种商品");
		switch (order.getStatus()) {
		case 0:
			itemCache.tv_orderStatus.setText("待收货");
			break;
		case 2:
			itemCache.tv_orderStatus.setText("已完成");
			break;
		default:
			break;
		}
		itemCache.tv_againToBuy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addToCarts(order.getId());

			}
		});
		return convertView;
	}

	public class ItemCache {
		private TextView tv_orderNumber;
		private TextView tv_orderStatus;
		private TextView tv_createTime;
		private TextView tv_consignee;// 收货人
		private TextView tv_productNumber;
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
	private void addToCarts(String unique_id) {
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			context.startActivity(new Intent(context, LoginActivity.class));
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("unique_id", unique_id);
		new HttpTask(context, HttpTask.POST, "v1/cart_items/order_batch_entry", map) {
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
