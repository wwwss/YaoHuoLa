package com.yaohuola.classification.adapter;

import java.util.List;

import com.android.yaohuola.R;
import com.yaohuola.adapter.BaseAdapter;
import com.yaohuola.data.entity.ProductEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 
 * @author admin 全部分类适配器
 */
public class ProductAdapter extends BaseAdapter<ProductEntity> {
	public ProductAdapter(Context context, List<ProductEntity> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_product, null);
			ItemCache itemCache = new ItemCache();
			itemCache.tvName = (TextView) convertView.findViewById(R.id.name);
			convertView.setTag(itemCache);
		}
		ItemCache itemCache = (ItemCache) convertView.getTag();
		ProductEntity productEntity = list.get(position);
		itemCache.tvName.setText(productEntity.getName());
		return convertView;
	}

	private class ItemCache {
		public TextView tvName;// 名称
	}

}
