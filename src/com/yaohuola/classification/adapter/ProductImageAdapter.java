package com.yaohuola.classification.adapter;

import java.util.List;

import com.android.yaohuola.R;
import com.yaohuola.YaoHuoLaApplication;
import com.yaohuola.adapter.BaseAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 
 * @author admin 产品图片适配器
 */
public class ProductImageAdapter extends BaseAdapter<String> {
	public ProductImageAdapter(Context context, List<String> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_product_image, null);
			ItemCache itemCache = new ItemCache();
			itemCache.ivImage = (ImageView) convertView.findViewById(R.id.image);
			convertView.setTag(itemCache);
		}
		ItemCache itemCache = (ItemCache) convertView.getTag();
		YaoHuoLaApplication.disPlayFromUrl(list.get(position), itemCache.ivImage, R.drawable.default_banner_icon);
		return convertView;
	}

	private class ItemCache {
		public ImageView ivImage;// 名称
	}

}
