package com.yaohuola.shoppingcart.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.OrderEntity;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.shoppingcart.activity.FillOrdersActivity;
import com.yaohuola.shoppingcart.adapter.ShoppingCartAdapter;
import com.yaohuola.task.HttpTask;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class ShoppingcartFragment extends Fragment implements OnClickListener {
	private Context context;
	private TextView tv_edit;
	private ListView listView;
	private List<ProductEntity> productEntities;
	private ShoppingCartAdapter adapter;
	private TextView tv_allSelect;
	private TextView tv_total;
	private TextView tv_orderNow;
	private TextView tv_orderNowHint;
	private View view;
	private double sendPrice;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_shoppingcart, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView() {
		view = getView();
		context = getActivity();
		tv_edit = (TextView) view.findViewById(R.id.edit);
		tv_total = (TextView) view.findViewById(R.id.total);
		tv_allSelect = (TextView) view.findViewById(R.id.allSelect);
		listView = (ListView) view.findViewById(R.id.listView);
		productEntities = new ArrayList<ProductEntity>();
		adapter = new ShoppingCartAdapter(context, productEntities, handler);
		listView.setAdapter(adapter);
		tv_edit.setOnClickListener(this);
		tv_allSelect.setOnClickListener(this);
		tv_orderNow = (TextView) view.findViewById(R.id.orderNow);
		tv_orderNowHint = (TextView) view.findViewById(R.id.orderNow_hint);
		tv_orderNow.setOnClickListener(this);
		sendPrice = LocalCache.getInstance(context).getSendPrice();

	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		// 判断显示才加载数据
		if (getUserVisibleHint()) {
			handler.sendEmptyMessage(1002);
		}
	}

	@Override
	public void onResume() {
		if (getUserVisibleHint()) {
			handler.sendEmptyMessage(1002);
		}
		super.onResume();
	}

	/**
	 * 获取数据
	 */
	public void getData() {
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			return;
		}
		new HttpTask(context, HttpTask.GET, "cart_items/" + token, null) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						JSONArray jsonArray = jsonObject.optJSONArray("cart_items");
						if (jsonArray == null) {
							return;
						}
						if (jsonArray.length() == 0) {
							return;
						}
						productEntities.clear();
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = jsonArray.optJSONObject(i);
							if (jsonObject2 == null) {
								continue;
							}
							JSONObject jsonObject3 = jsonObject2.optJSONObject("product");
							if (jsonObject3 == null) {
								continue;
							}
							ProductEntity productEntity = new ProductEntity();
							productEntity.setId(jsonObject2.optString("unique_id", ""));
							productEntity.setId2(jsonObject3.optString("unique_id", ""));
							int product_num = jsonObject2.optInt("product_num", 0);
							productEntity.setNumber(product_num);
							productEntity.setName(jsonObject3.optString("name", ""));
							productEntity.setPic(jsonObject3.optString("image", ""));
							productEntity.setDescription(jsonObject3.optString("desc", ""));
							productEntity.setPrice(jsonObject3.optDouble("price", 0));
							productEntity.setSpec(jsonObject3.optString("spec", ""));
							int stock_num = jsonObject3.optInt("stock_num", 0);
							productEntity.setStock_num(stock_num);
							if (product_num >stock_num) {
								productEntity.setSelected(false);
							} else {
								productEntity.setSelected(true);
							}
							productEntity.setSelecteIsShow(true);
							productEntities.add(productEntity);
						}
						if (productEntities.size() > 0) {
							adapter.notifyDataSetChanged();
							tv_allSelect.setSelected(true);
						}
						view.findViewById(R.id.footView).setVisibility(View.VISIBLE);
						tv_edit.setVisibility(View.VISIBLE);
					} else {
						productEntities.clear();
						adapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();

	}

	private double total;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1001:
				if (productEntities.size() == 0) {
					view.findViewById(R.id.footView).setVisibility(View.INVISIBLE);
					tv_edit.setVisibility(View.INVISIBLE);
					return;
				} else {

				}
				int count = 0;
				for (int i = 0; i < productEntities.size(); i++) {
					if (productEntities.get(i).isSelected()) {
						count++;
					}

				}
				if (count == productEntities.size()) {
					tv_allSelect.setSelected(true);

				}
				total = (Double) msg.obj;
				DecimalFormat df=new DecimalFormat("#,##0.00"); 
				String strValue = df.format(total);
				tv_total.setText("合计：" + strValue);
				if (total >= sendPrice) {
					tv_orderNow.setVisibility(View.VISIBLE);
					tv_orderNowHint.setVisibility(View.GONE);
				} else {
					tv_orderNow.setVisibility(View.GONE);
					tv_orderNowHint.setVisibility(View.VISIBLE);
					tv_orderNowHint.setText("还差" + (1000 - total) + "元起送");
				}
				break;
			case 1002:
				getData();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit:
			if ("编辑".equals(tv_edit.getText().toString())) {
				edit();
			} else {
				complete();
			}

			break;
		case R.id.allSelect:
			if (tv_allSelect.isSelected()) {
				selected(false);
				tv_allSelect.setSelected(false);
			} else {
				selected(true);
				tv_allSelect.setSelected(true);
			}
			break;
		case R.id.orderNow:
			if ("删除".equals(tv_orderNow.getText().toString())) {
				delect();
			} else {
				// 去结算
				if (total > 0) {
					Intent intent = new Intent(context, FillOrdersActivity.class);
					OrderEntity orderEntity = new OrderEntity();
					DecimalFormat df=new DecimalFormat("#,##0.00"); 
					String strValue = df.format(total);
					orderEntity.setTotal(strValue);
					final List<ProductEntity> newProductEntities = new ArrayList<ProductEntity>();
					for (int i = 0; i < productEntities.size(); i++) {
						ProductEntity productEntity = productEntities.get(i);
						if (productEntity.isSelected()) {
							newProductEntities.add(productEntity);
						}
					}
					orderEntity.setProductEntities(newProductEntities);
					intent.putExtra("orderEntity", orderEntity);
					startActivity(intent);
				} else {

				}

			}
			break;
		default:
			break;
		}

	}

	private void delect() {
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		JSONArray jsonArray = new JSONArray();
		final List<ProductEntity> newProductEntities = new ArrayList<ProductEntity>();
		for (int i = 0; i < productEntities.size(); i++) {
			ProductEntity productEntity = productEntities.get(i);
			if (productEntity.isSelected()) {
				jsonArray.put(productEntity.getId());
			} else {
				newProductEntities.add(productEntity);
			}
		}
		map.put("unique_ids", jsonArray.toString());
		new HttpTask(context, HttpTask.DELETE, "cart_items", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						productEntities.clear();
						productEntities.addAll(newProductEntities);
						adapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			};
		}.run();

	}

	private void selected(boolean selected) {
		for (int i = 0; i < productEntities.size(); i++) {
			productEntities.get(i).setSelected(selected);
		}
		adapter.notifyDataSetChanged();
	}

	private void complete() {
		tv_edit.setText("编辑");
		adapter.setDelect(false);
		tv_total.setVisibility(View.VISIBLE);
		tv_orderNow.setText("立即下单");
		adapter.notifyDataSetChanged();

	}

	/**
	 * 编辑事件
	 */
	private void edit() {
		for (int i = 0; i < productEntities.size(); i++) {
			productEntities.get(i).setSelecteIsShow(true);
		}
		tv_edit.setText("完成");
		tv_total.setVisibility(View.INVISIBLE);
		tv_orderNow.setText("删除");
		adapter.setDelect(true);
		adapter.notifyDataSetChanged();

	}
}