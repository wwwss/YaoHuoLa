package com.yaohuola.classification.adapter;

import java.util.List;

import com.android.yaohuola.R;
import com.yaohuola.YaoHuoLaApplication;
import com.yaohuola.adapter.BaseAdapter;
import com.yaohuola.data.entity.ProductEntity;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author admin 具体分类适配器
 */
public class ConcretenessClassifyAdapter extends BaseAdapter<ProductEntity> {
	public ConcretenessClassifyAdapter(Context context, List<ProductEntity> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_concreteness_classify, null);
			ItemCache itemCache = new ItemCache();
			itemCache.tvName = (TextView) convertView.findViewById(R.id.name);
			itemCache.ivPic = (ImageView) convertView.findViewById(R.id.pic);
			convertView.setTag(itemCache);
		}
		ItemCache itemCache = (ItemCache) convertView.getTag();
		ProductEntity productEntity = list.get(position);
		itemCache.tvName.setText(productEntity.getName());
		if (TextUtils.isEmpty(productEntity.getPic())) {
			itemCache.ivPic.setImageResource(R.drawable.default_product_icon);
		} else {
			YaoHuoLaApplication.disPlayFromUrl(productEntity.getPic(), itemCache.ivPic,
					R.drawable.default_product_icon);
		}
		return convertView;
	}

	private class ItemCache {
		public TextView tvName;// 名称
		public ImageView ivPic;// 图片
	}

}
