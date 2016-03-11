package com.yaohuola.my.adapter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.yaohuola.adapter.BaseAdapter;
import com.yaohuola.data.entity.AddrEntity;
import com.yaohuola.data.entity.OrderEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AddrListAdapter extends BaseAdapter<AddrEntity> {

	public AddrListAdapter(Context context, List<AddrEntity> list) {
		super(context, list);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_addr_list, null);
			ItemCache itemCache = new ItemCache();
			itemCache.tv_consignee = (TextView) convertView.findViewById(R.id.consignee);
			itemCache.tv_phoneNumber = (TextView) convertView.findViewById(R.id.phoneNumber);
			itemCache.tv_shippingAddress = (TextView) convertView.findViewById(R.id.shippingAddress);
			itemCache.iv_isDefault=(ImageView) convertView.findViewById(R.id.isDefault);
			convertView.setTag(itemCache);

		}
		final ItemCache itemCache = (ItemCache) convertView.getTag();
		final AddrEntity addrEntity = list.get(position);
		itemCache.tv_consignee.setText("收货人：" + addrEntity.getName());
		itemCache.tv_phoneNumber.setText("联系电话：" + addrEntity.getPhone());
		itemCache.tv_shippingAddress.setText("收货地址：" + addrEntity.getAddr());
		if (addrEntity.isDefault()) {
			itemCache.iv_isDefault.setVisibility(View.VISIBLE);
		}else{
			itemCache.iv_isDefault.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	public class ItemCache {
		private TextView tv_consignee;// 收货人
		private TextView tv_phoneNumber;
		private TextView tv_shippingAddress;
		private ImageView iv_isDefault;

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

}
