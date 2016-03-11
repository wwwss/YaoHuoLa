package com.yaohuola.classification.adapter;

import java.util.List;

import com.android.yaohuola.R;
import com.yaohuola.adapter.BaseAdapter;
import com.yaohuola.data.entity.ClassifyEntity;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author admin 全部分类适配器
 */
public class AllClassifyAdapter extends BaseAdapter<ClassifyEntity> {

	public AllClassifyAdapter(Context context, List<ClassifyEntity> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_all_classify, null);
			ItemCache itemCache = new ItemCache();
			itemCache.tvName = (TextView) convertView.findViewById(R.id.name);
			itemCache.linearLayout=(LinearLayout) convertView.findViewById(R.id.linearLayout);
			convertView.setTag(itemCache);
		}
		ItemCache itemCache = (ItemCache) convertView.getTag();
		ClassifyEntity classifyEntity = list.get(position);
		itemCache.tvName.setText(classifyEntity.getName());

		if (position == selectItem) {
			itemCache.tvName.setTextColor(Color.parseColor("#ff3434"));
			itemCache.linearLayout.setBackgroundColor(Color.parseColor("#ffffff"));
			
		} else {
			itemCache.tvName.setTextColor(Color.parseColor("#ff000000"));
			itemCache.linearLayout.setBackgroundColor(Color.parseColor("#eeeeee"));
		}
		return convertView;
	}

	private class ItemCache {
		public TextView tvName;// 名称
		public LinearLayout linearLayout;
	}

	public void setSelectItem(int selectItem) {
		this.selectItem = selectItem;
	}

	private int selectItem = -1;

}
