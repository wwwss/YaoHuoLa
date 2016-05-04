package com.yaohuola.shoppingcart.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.AddrEntity;
import com.yaohuola.my.activity.AddrManagementActivity;
import com.yaohuola.my.adapter.AddrListAdapter;
import com.yaohuola.task.HttpTask;

/**
 * 
 * @author admin 收货地址管理页面
 */
public class AddrSelectActivity extends BaseActivity implements
		OnItemClickListener {

	private ListView listView;
	private AddrListAdapter adapter;
	private List<AddrEntity> addrEntities;
	private final int SHIPPING_ADDRESS_MANAGEMENT = 0;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_addr_select);

	}

	@Override
	public void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.shippingAddressManagement).setOnClickListener(this);
		listView = (ListView) findViewById(R.id.listView);
		addrEntities = new ArrayList<AddrEntity>();
		adapter = new AddrListAdapter(getApplicationContext(), addrEntities);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		getData();
	}

	private void getData() {
		String token = LocalCache.getInstance(getApplicationContext())
				.getToken();
		new HttpTask(this, HttpTask.GET, "v1/addresses/"
				+ token, null) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						addrEntities.clear();
						JSONArray jsonArray = jsonObject
								.optJSONArray("addresses");
						if (jsonArray == null) {
							return;
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = jsonArray.optJSONObject(i);
							if (jsonObject2 == null) {
								continue;
							}
							AddrEntity addrEntity = new AddrEntity();
							addrEntity.setId(jsonObject2.optString("unique_id",
									""));
							addrEntity.setName(jsonObject2.optString(
									"receive_name", ""));
							addrEntity.setPhone(jsonObject2.optString(
									"receive_phone", ""));
							addrEntity.setArea(jsonObject2
									.optString("area", ""));
							addrEntity.setAddr(jsonObject2.optString("detail",
									""));
							int isDefault = jsonObject2.optInt("default", -1);
							if (isDefault == 1) {
								addrEntity.setDefault(true);
							} else {
								addrEntity.setDefault(false);
							}
							addrEntities.add(addrEntity);
						}
						if (addrEntities.size() > 0) {
							adapter.notifyDataSetChanged();
						}
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
		case R.id.shippingAddressManagement:
			intent = new Intent(this, AddrManagementActivity.class);
			startActivityForResult(intent, SHIPPING_ADDRESS_MANAGEMENT);
			break;
		default:
			break;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 如果返回失败，退出
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case SHIPPING_ADDRESS_MANAGEMENT:
			getData();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		AddrEntity addrEntity = (AddrEntity) parent.getItemAtPosition(position);
		Intent intent = new Intent();
		intent.putExtra("addrEntity", addrEntity);
		setResult(RESULT_OK, intent);
		finish();

	}

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub
		
	}

}
