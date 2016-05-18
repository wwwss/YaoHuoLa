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
import com.yaohuola.classification.activity.ProductDetailsActivity;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.OrderEntity;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.data.entity.ShoppingCartEntity;
import com.yaohuola.interfaces.FragmentSwitchListenter;
import com.yaohuola.shoppingcart.activity.FillOrdersActivity;
import com.yaohuola.shoppingcart.adapter.ShoppingCartAdapter;
import com.yaohuola.task.HttpTask;
import com.yaohuola.view.DeleteDialog;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ShoppingcartFragment extends Fragment implements OnClickListener, OnItemClickListener {
	private Context context;
	private DeleteDialog alertDialog;
	private ListView listView;
	private List<ShoppingCartEntity> shoppingCartEntities;
	private ShoppingCartAdapter adapter;
	private TextView tv_allSelect;
	private TextView tv_total;
	private TextView tv_orderNow;
	private TextView tv_orderNowHint;
	private View view;
	private double sendPrice;
	private FragmentSwitchListenter listenter;

	public ShoppingcartFragment(FragmentSwitchListenter listenter) {
		this.listenter = listenter;
	}

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
		tv_total = (TextView) view.findViewById(R.id.total);
		tv_allSelect = (TextView) view.findViewById(R.id.allSelect);
		listView = (ListView) view.findViewById(R.id.listView);
		shoppingCartEntities = new ArrayList<ShoppingCartEntity>();
		adapter = new ShoppingCartAdapter(context, shoppingCartEntities, handler);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		listView.setEmptyView(view.findViewById(R.id.shoppingCartEmptyHint));
		view.findViewById(R.id.goHomepage).setOnClickListener(this);
		view.findViewById(R.id.delete).setOnClickListener(this);
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
		super.onResume();
		if (getUserVisibleHint()) {
			handler.sendEmptyMessage(1002);
		}
	}

	/**
	 * 获取数据
	 */
	public void getData() {
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			shoppingCartEntities.clear();
			adapter.notifyDataSetChanged();
			view.findViewById(R.id.footView).setVisibility(View.INVISIBLE);
			view.findViewById(R.id.delete).setVisibility(View.INVISIBLE);
			return;
		}
		new HttpTask(context, HttpTask.GET, "v2/cart_items/" + token, null) {
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
						shoppingCartEntities.clear();
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = jsonArray.optJSONObject(i);
							if (jsonObject2 == null) {
								continue;
							}
							String categoryName = jsonObject2.optString("category_name", "");
							JSONArray jsonArray2 = jsonObject2.optJSONArray("list");
							if (jsonArray2 == null) {
								continue;
							}
							for (int j = 0; j < jsonArray2.length(); j++) {
								JSONObject jsonObject3 = jsonArray2.optJSONObject(j);
								if (jsonObject3 == null) {
									continue;
								}
								ShoppingCartEntity shoppingCartEntity = new ShoppingCartEntity();
								shoppingCartEntity.setCategoryName(categoryName);
								shoppingCartEntity.setShoppingcartId(jsonObject3.optString("unique_id", ""));
								ProductEntity productEntity = new ProductEntity();
								productEntity.setId(jsonObject3.optString("product_unique_id", ""));
								int product_num = jsonObject3.optInt("product_num", 0);
								productEntity.setNumber(product_num);
								productEntity.setName(jsonObject3.optString("product_name", ""));
								productEntity.setPic(jsonObject3.optString("product_image", ""));
								productEntity.setPrice(jsonObject3.optDouble("product_price", 0));
								productEntity.setSpec(jsonObject3.optString("product_spec", ""));
								int stock_num = jsonObject3.optInt("product_stock_num", 0);
								productEntity.setStock_num(stock_num);
								if (product_num > stock_num) {
									shoppingCartEntity.setSelected(false);
								} else {
									shoppingCartEntity.setSelected(true);
								}
								shoppingCartEntity.setSelecteIsShow(true);
								shoppingCartEntity.setProductEntity(productEntity);
								shoppingCartEntities.add(shoppingCartEntity);
							}
						}
						if (shoppingCartEntities.size() > 0) {
							adapter.notifyDataSetChanged();
							adapter.recordIndex();
							tv_allSelect.setSelected(true);
						}
						view.findViewById(R.id.footView).setVisibility(View.VISIBLE);
						view.findViewById(R.id.delete).setVisibility(View.VISIBLE);
					} else {
						shoppingCartEntities.clear();
						adapter.recordIndex();
						adapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();

	}

	private double total;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 1001:
				if (shoppingCartEntities.size() == 0) {
					view.findViewById(R.id.footView).setVisibility(View.INVISIBLE);
					view.findViewById(R.id.delete).setVisibility(View.INVISIBLE);
					return;
				}
				int count = 0;
				for (int i = 0; i < shoppingCartEntities.size(); i++) {
					if (shoppingCartEntities.get(i).isSelected()) {
						count++;
					}
				}
				if (count == shoppingCartEntities.size()) {
					tv_allSelect.setSelected(true);
				} else {
					tv_allSelect.setSelected(false);
				}
				total = (Double) msg.obj;
				DecimalFormat df = new DecimalFormat("##0.00");
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
		case R.id.delete:
			delect();
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
			// 去结算
			if (total > 0) {
				Intent intent = new Intent(context, FillOrdersActivity.class);
				OrderEntity orderEntity = new OrderEntity();
				DecimalFormat df = new DecimalFormat("#,##0.00");
				String strValue = df.format(total);
				orderEntity.setTotal(strValue);
				final List<ShoppingCartEntity> newShoppingCartEntites = new ArrayList<ShoppingCartEntity>();
				for (int i = 0; i < shoppingCartEntities.size(); i++) {
					ShoppingCartEntity shoppingCartEntity = shoppingCartEntities.get(i);
					if (shoppingCartEntity.isSelected()) {
						newShoppingCartEntites.add(shoppingCartEntity);
					}
				}
				orderEntity.setShoppingCartEntities(newShoppingCartEntites);
				intent.putExtra("orderEntity", orderEntity);
				startActivity(intent);
			}
			break;
		case R.id.goHomepage:
			listenter.go(0, null);
			break;
		default:
			break;
		}

	}

	private void delect() {
		alertDialog = new DeleteDialog(context).builder();
		final JSONArray jsonArray = new JSONArray();
		final List<ShoppingCartEntity> newShoppingCartEntites = new ArrayList<ShoppingCartEntity>();
		for (int i = 0; i < shoppingCartEntities.size(); i++) {
			ShoppingCartEntity shoppingCartEntity = shoppingCartEntities.get(i);
			if (shoppingCartEntity.isSelected()) {
				jsonArray.put(shoppingCartEntity.getShoppingcartId());
			} else {
				newShoppingCartEntites.add(shoppingCartEntity);
			}
		}
		if (jsonArray.length() == 0)
			return;
		alertDialog.setTitle("您确定要删除这些商品吗？").setPositiveButton("是", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				delete(jsonArray, newShoppingCartEntites);
			}
		}).setNegativeButton("否", new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		alertDialog.show();
	}

	private void selected(boolean selected) {
		for (int i = 0; i < shoppingCartEntities.size(); i++) {
			shoppingCartEntities.get(i).setSelected(selected);
		}
		adapter.notifyDataSetChanged();
	}

	private void delete(JSONArray jsonArray, final List<ShoppingCartEntity> newShoppingCartEntites) {
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			return;
		}
		final Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
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
						shoppingCartEntities.clear();
						shoppingCartEntities.addAll(newShoppingCartEntites);
						adapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(context, ProductDetailsActivity.class);
		ShoppingCartEntity shoppingCartEntity = (ShoppingCartEntity) parent.getItemAtPosition(position);
		intent.putExtra("id", shoppingCartEntity.getProductEntity().getId());
		startActivity(intent);
	}
}
