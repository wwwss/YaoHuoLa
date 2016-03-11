package com.yaohuola.my.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.library.uitls.ListViewUitls;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.AddrEntity;
import com.yaohuola.data.entity.OrderEntity;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.my.adapter.OrderProductListAdapter;
import com.yaohuola.task.HttpTask;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderDetailsActivity extends BaseActivity {

	private TextView tv_orderStatus;
	private TextView tv_consignee;// 收货人
	private TextView tv_contactPhoneNumber;// 联系电话
	private TextView tv_shippingAddress;// 收货地址
	private TextView tv_totalPrice;// 总价
	private TextView tv_orderNumber;
	private TextView tv_createTime;
	private TextView tv_deliveryTime;// 发货时间
	private TextView tv_transactionTime;// 成交时间.
	private ListView listView;
	private List<ProductEntity> productEntities;
	private OrderProductListAdapter adapter;
	private String id;
	private String totalPrice;
	private int totalNumber;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_order_details);

	}

	@Override
	public void initView() {
		id = getIntent().getStringExtra("id");
		tv_orderStatus = (TextView) findViewById(R.id.orderStatus);
		tv_consignee = (TextView) findViewById(R.id.consignee);
		tv_contactPhoneNumber = (TextView) findViewById(R.id.contactPhoneNumber);
		tv_shippingAddress = (TextView) findViewById(R.id.shippingAddress);
		tv_orderNumber = (TextView) findViewById(R.id.oderNumber);
		tv_createTime = (TextView) findViewById(R.id.createTime);
		tv_deliveryTime = (TextView) findViewById(R.id.deliveryTime);
		tv_transactionTime = (TextView) findViewById(R.id.transactionTime);
		listView = (ListView) findViewById(R.id.listView);
		tv_totalPrice = (TextView) findViewById(R.id.totalPrice);
		productEntities = new ArrayList<ProductEntity>();
		adapter = new OrderProductListAdapter(this, productEntities);
		listView.setAdapter(adapter);
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.aSingleAgain).setOnClickListener(this);
		if (TextUtils.isEmpty(id)) {
			return;
		}
		getData();
	}

	private void getData() {
		String token = LocalCache.getInstance(this).getToken();
		if (TextUtils.isEmpty(token)) {
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		new HttpTask(this, HttpTask.GET, "orders/" + id, map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						JSONObject jsonObject2 = jsonObject.optJSONObject("order");
						if (jsonObject2 == null) {
							return;
						}
						OrderEntity order = new OrderEntity();
						order.setId(jsonObject2.optString("unique_id", ""));
						order.setSn(jsonObject2.optString("order_no", ""));
						AddrEntity addrEntity = new AddrEntity();
						addrEntity.setName(jsonObject2.optString("receive_name", ""));
						addrEntity.setPhone(jsonObject2.optString("phone_num", ""));
						addrEntity.setAddr(jsonObject2.optString("address", ""));
						order.setAddrEntity(addrEntity);
						order.setStatus(jsonObject2.optInt("state", -1));
						order.setCreate_at(jsonObject2.optString("created_at", ""));
						order.setDelivery_time(jsonObject2.optString("delivery_time", ""));
						order.setComplete_time(jsonObject2.optString("complete_time", ""));
						order.setProductNumber(jsonObject2.optInt("pro_count", -1));
						totalPrice = jsonObject2.optString("order_money", "");
						JSONArray jsonArray = jsonObject2.optJSONArray("products");
						if (jsonArray == null) {
							return;
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject3 = jsonArray.optJSONObject(i);
							if (jsonObject3 == null) {
								continue;
							}
							ProductEntity productEntity = new ProductEntity();
							productEntity.setId(jsonObject3.optString("unique_id", ""));
							productEntity.setName(jsonObject3.optString("name", ""));
							productEntity.setPic(jsonObject3.optString("image", ""));
							productEntity.setDescription(jsonObject3.optString("desc", ""));
							double price = jsonObject3.optDouble("price", 0);
							productEntity.setPrice(price);
							productEntity.setSpec(jsonObject3.optString("spec", ""));
							int number = jsonObject3.optInt("number", 0);
							productEntity.setNumber(number);
							productEntity.setStock_num(jsonObject3.optInt("stock_num", 0));
							totalNumber += number;
							productEntities.add(productEntity);
						}

						order.setProductEntities(productEntities);
						setData(order);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		}.run();
	}

	private void setData(OrderEntity order) {
		if ("2".equals(order.getStatus())) {
			tv_orderStatus.setText("已完成");
			findViewById(R.id.orderStatusHint).setVisibility(View.INVISIBLE);
		} else {
			tv_orderStatus.setText("未完成");
			findViewById(R.id.orderStatusHint).setVisibility(View.VISIBLE);
		}
		AddrEntity addrEntity = order.getAddrEntity();
		if (addrEntity != null) {
			tv_consignee.setText("收货人：" + addrEntity.getName());
			tv_contactPhoneNumber.setText("联系电话：" + addrEntity.getPhone());
			tv_shippingAddress.setText("收货地址：" + addrEntity.getAddr());
		}
		tv_totalPrice.setText("共" + totalNumber + "件产品" + " " + "合计：" + totalPrice);
		tv_orderNumber.setText("订单编号：" + order.getSn());
		tv_createTime.setText("创建时间：" + order.getCreate_at());
		tv_deliveryTime.setText("发货时间：" + order.getCreate_at());
		tv_transactionTime.setText("成交时间：" + order.getCreate_at());
		if (productEntities.size() > 0) {
			adapter.notifyDataSetChanged();
			ListViewUitls.setListViewHeightBasedOnChildren(listView);
			// 让listView失去焦点
			listView.setFocusable(false);
			ScrollView scrollview = (ScrollView) findViewById(R.id.scrollView);
			scrollview.scrollTo(0, 20);
			scrollview.setVisibility(View.VISIBLE);
			findViewById(R.id.aSingleAgain).setVisibility(View.VISIBLE);
			listView.setFocusable(true);
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.aSingleAgain:
			addToCarts(id);
			break;
		default:
			break;
		}
	}

	/**
	 * 加入购物车的方法
	 */
	private void addToCarts(String unique_id) {
		String token = LocalCache.getInstance(this).getToken();
		if (TextUtils.isEmpty(token)) {
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("unique_id", unique_id);
		new HttpTask(this, HttpTask.POST, "cart_items/order_batch_entry", map) {
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

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub

	}

}
