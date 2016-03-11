package com.yaohuola.my.adapter;

import java.util.List;

import com.android.yaohuola.R;
import com.yaohuola.adapter.BaseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SelectAreaAdapter extends BaseAdapter<String> {

	public SelectAreaAdapter(Context context, List<String> list) {
		super(context, list);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_area, null);
			ItemCache itemCache = new ItemCache();
			itemCache.tv_areaName = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(itemCache);

		}
		final ItemCache itemCache = (ItemCache) convertView.getTag();
		final String name = list.get(position);
		itemCache.tv_areaName.setText(name);
		return convertView;
	}

	public class ItemCache {
		private TextView tv_areaName;// 行政区域名称

	}
}
