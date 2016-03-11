package com.yaohuola.homepage.adapter;

import java.util.List;

import com.android.yaohuola.R;
import com.yaohuola.adapter.BaseAdapter;
import com.yaohuola.data.entity.ClassifyEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ClassifyAdapter extends BaseAdapter<ClassifyEntity> {

	

	public ClassifyAdapter(Context context, List<ClassifyEntity> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_classify, null);
			ItemCache itemCache = new ItemCache();
			itemCache.tvName=(TextView) convertView.findViewById(R.id.name);
			itemCache.ivPic=(ImageView) convertView.findViewById(R.id.pic);
			convertView.setTag(itemCache);
		}
		ItemCache itemCache = (ItemCache) convertView.getTag();
		ClassifyEntity classifyEntity = list.get(position);
		itemCache.tvName.setText(classifyEntity.getName());
		itemCache.ivPic.setImageResource(classifyEntity.getDrawable());
		return convertView;
	}



	private class ItemCache {
		public TextView tvName;// 名称
		public ImageView ivPic;// 图片
	}

}
