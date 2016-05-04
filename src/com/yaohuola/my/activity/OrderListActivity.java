package com.yaohuola.my.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.AddrEntity;
import com.yaohuola.data.entity.OrderEntity;
import com.yaohuola.my.adapter.OrderListAdapter;
import com.yaohuola.task.HttpTask;

/**
 * 
 * @author admin 订单列表页面
 */
public class OrderListActivity extends BaseActivity implements OnItemClickListener  {

	private TextView tv_title;
	private ListView listView;
	private List<OrderEntity> orders;
	private OrderListAdapter adapter;
	//private int pageNumber;
	private int type;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_order_list);
	}

	@Override
	public void initView() {
		type=getIntent().getIntExtra("type", -1);
		tv_title=(TextView) findViewById(R.id.title);
		if (type==0) {
			tv_title.setText("未完成订单");
		}else if (type==1){
			tv_title.setText("已完成订单");
		}else{
			return;
		}
		listView = (ListView) findViewById(R.id.listView);
		orders = new ArrayList<OrderEntity>();
		adapter = new OrderListAdapter(this, orders);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		//listView.setOnRefreshListener(this);
		getData();
	}

	private void getData() {
		String token = LocalCache.getInstance(this).getToken();
		if (TextUtils.isEmpty(token)) {
			return;
		}
		Map<String, String> map=new HashMap<String, String>();
		map.put("token", token);
		JSONArray jsonArray=new JSONArray();
		if (type==0) {
			jsonArray.put(0);
			jsonArray.put(1);
		}else if(type==1) {
			jsonArray.put(2);
		}else{
			return;
		}
		map.put("state", jsonArray.toString());
		new HttpTask(this, HttpTask.GET, "v1/orders/", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						JSONArray jsonArray=jsonObject.optJSONArray("orders");
						if (jsonArray==null) {
							return;
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2=jsonArray.optJSONObject(i);
							if (jsonObject2==null) {
								return;
							}
							OrderEntity order = new OrderEntity();
							order.setId(jsonObject2.optString("unique_id", ""));
							order.setSn(jsonObject2.optString("order_no", ""));
							order.setCreate_at(jsonObject2.optString("created_at", ""));
							AddrEntity addrEntity=new AddrEntity();
							addrEntity.setName(jsonObject2.optString("receive_name", ""));
							order.setAddrEntity(addrEntity);
							order.setProductNumber(jsonObject2.optInt("pro_count", -1));
							order.setStatus(jsonObject2.optInt("state", -1));
							orders.add(order);
						}
					if (orders.size() > 0) {
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
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
		
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		OrderEntity orderEntity=(OrderEntity) parent.getItemAtPosition(position);
		Intent intent=new Intent(this,OrderDetailsActivity.class);
		intent.putExtra("id", orderEntity.getId());
		startActivity(intent);
		
	}

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub
		
	}

}
