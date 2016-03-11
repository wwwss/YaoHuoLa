package com.yaohuola.shoppingcart.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.library.uitls.ListViewUitls;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.AddrEntity;
import com.yaohuola.data.entity.OrderEntity;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.my.activity.OrderListActivity;
import com.yaohuola.my.adapter.FillOrderProductListAdapter;
import com.yaohuola.task.HttpTask;

/**
 * 
 * @author admin 填写订单页面
 */
public class FillOrdersActivity extends BaseActivity {
	private TextView tv_consignee;// 收货人
	private TextView tv_phoneNumber;
	private TextView tv_shippingAddress;
	private TextView tv_total;
	private ListView listView;
	private List<ProductEntity> productEntities;
	private FillOrderProductListAdapter adapter;
	private OrderEntity orderEntity;
	private final int SHIPPING_ADDRESS_SELECT = 0;
	private AddrEntity addrEntity;// 收货地址

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_fill_orders);

	}

	@Override
	public void initView() {
		orderEntity = (OrderEntity) getIntent().getSerializableExtra("orderEntity");
		if (orderEntity == null) {
			return;
		}
		tv_consignee = (TextView) findViewById(R.id.consignee);
		tv_phoneNumber = (TextView) findViewById(R.id.phoneNumber);
		tv_shippingAddress = (TextView) findViewById(R.id.shippingAddress);
		tv_total = (TextView) findViewById(R.id.total);
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.addr).setOnClickListener(this);
		findViewById(R.id.submitOrder).setOnClickListener(this);
		listView = (ListView) findViewById(R.id.listView);
		productEntities = new ArrayList<ProductEntity>();
		if (orderEntity.getProductEntities() != null && orderEntity.getProductEntities().size() > 0) {
			productEntities = orderEntity.getProductEntities();
			tv_phoneNumber.setText("选择收货地址");
			tv_total.setText("实付金额：¥" + orderEntity.getTotal());
		}
		adapter = new FillOrderProductListAdapter(this, productEntities);
		listView.setAdapter(adapter);
		ListViewUitls.setListViewHeightBasedOnChildren(listView);
		// 让listView失去焦点
		listView.setFocusable(false);
		ScrollView scrollview = (ScrollView) findViewById(R.id.scrollView);
		scrollview.scrollTo(0, 20);
		listView.setFocusable(true);
		// 获取默认地址
		getDefaultAddr();
	}

	/**
	 * 获取默认地址
	 */
	private void getDefaultAddr() {
		String token = LocalCache.getInstance(this).getToken();
		if (TextUtils.isEmpty(token)) {
			return;
		}
		new HttpTask(this, HttpTask.GET, "addresses/default/" + token, null) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						JSONObject jsonObject2 = jsonObject.optJSONObject("address");
						if (jsonObject2 == null) {
							return;
						}
						addrEntity = new AddrEntity();
						addrEntity.setId(jsonObject2.optString("unique_id", ""));
						addrEntity.setName(jsonObject2.optString("receive_name", ""));
						addrEntity.setPhone(jsonObject2.optString("receive_phone", ""));
						addrEntity.setArea(jsonObject2.optString("area", ""));
						addrEntity.setAddr(jsonObject2.optString("detail", ""));
						int isDefault = jsonObject2.optInt("default", -1);
						if (isDefault == 1) {
							addrEntity.setDefault(true);
						} else {
							addrEntity.setDefault(false);
						}
						setAddrInfo(addrEntity);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			};
		}.run();

	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.addr:
			intent = new Intent(this, AddrSelectActivity.class);
			startActivityForResult(intent, SHIPPING_ADDRESS_SELECT);
			break;
		case R.id.submitOrder:
			submitOrder();
			break;

		default:
			break;
		}
	}

	/**
	 * 创建订单
	 */
	private void submitOrder() {
		Map<String, String> map = new HashMap<String, String>();
		String token = LocalCache.getInstance(this).getToken();
		map.put("token", token);
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < productEntities.size(); i++) {
			ProductEntity productEntity = productEntities.get(i);
			Map<String, String> map2 = new HashMap<String, String>();
			map2.put("unique_id", productEntity.getId2());
			map2.put("number", productEntity.getNumber() + "");
			JSONObject jsonObject = new JSONObject(map2);
			jsonArray.put(jsonObject);
		}
		map.put("products", jsonArray.toString());
		map.put("money", orderEntity.getTotal());
		if (addrEntity == null) {
			ToastShow("请选择收货地址");
			return;
		}
		map.put("receive_name", addrEntity.getName());
		map.put("address_id", addrEntity.getId());
		map.put("phone_num", addrEntity.getPhone());
		new HttpTask(this, HttpTask.POST, "orders", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						ToastShow("提交订单成功");
						Intent intent = new Intent(FillOrdersActivity.this, OrderListActivity.class);
						intent.putExtra("type", 0);
						startActivity(intent);
						finish();
					} else if (code == 3) {
						Toast.makeText(context, "库存不足", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, "提交订单失败", Toast.LENGTH_SHORT).show();

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			};
		}.run();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 如果返回失败，退出
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case SHIPPING_ADDRESS_SELECT:
			addrEntity = (AddrEntity) data.getSerializableExtra("addrEntity");
			if (addrEntity == null) {
				return;
			}
			setAddrInfo(addrEntity);
			break;
		}
	}

	private void setAddrInfo(AddrEntity addrEntity) {
		tv_consignee.setText("收货人：" + addrEntity.getName());
		tv_phoneNumber.setText("联系电话：" + addrEntity.getPhone());
		tv_shippingAddress.setText("收货地址：" + addrEntity.getAddr());
	}

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub

	}
}
