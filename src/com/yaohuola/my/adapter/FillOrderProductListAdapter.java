package com.yaohuola.my.adapter;

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

public class FillOrderProductListAdapter extends BaseAdapter<ProductEntity> {
	
	public FillOrderProductListAdapter(Context context, List<ProductEntity> list) {
		super(context, list);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_fill_order_product_list, null);
			ItemCache itemCache = new ItemCache();
			itemCache.iv_pic=(ImageView) convertView.findViewById(R.id.pic);
			itemCache.tv_productName = (TextView) convertView.findViewById(R.id.name);
			itemCache.tv_productDescription = (TextView) convertView.findViewById(R.id.description);
			itemCache.tv_productPrcieAndNumber = (TextView) convertView.findViewById(R.id.prcieAndNumber);
			convertView.setTag(itemCache);

		}
		final ItemCache itemCache = (ItemCache) convertView.getTag();
		final ProductEntity productEntity = list.get(position);
		if (TextUtils.isEmpty(productEntity.getPic())) {
			itemCache.iv_pic.setImageResource(R.drawable.default_product_icon);
		} else {
			YaoHuoLaApplication.disPlayFromUrl(productEntity.getPic(), itemCache.iv_pic,
					R.drawable.default_product_icon);
		}
		itemCache.tv_productName.setText(productEntity.getName());
		itemCache.tv_productDescription.setText(productEntity.getDescription());
		itemCache.tv_productPrcieAndNumber.setText("¥"+productEntity.getPrice()+"  x"+productEntity.getNumber());
		return convertView;
	}

	private class ItemCache {
		private ImageView iv_pic;
		private TextView tv_productName;
		private TextView tv_productDescription;//产品描述
		private TextView tv_productPrcieAndNumber;//价格和数量

	}

}
