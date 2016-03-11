package com.yaohuola.classification.adapter;

import java.util.List;

import com.android.yaohuola.R;
import com.yaohuola.YaoHuoLaApplication;
import com.yaohuola.adapter.BaseAdapter;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.task.AddToCartTask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductGridViewAdapter extends BaseAdapter<ProductEntity> {

	public ProductGridViewAdapter(Context context, List<ProductEntity> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_hot_sale, null);
			ItemCache itemCache = new ItemCache();
			itemCache.tvName = (TextView) convertView.findViewById(R.id.name);
			itemCache.ivPic = (ImageView) convertView.findViewById(R.id.pic);
			itemCache.tvDescription = (TextView) convertView.findViewById(R.id.description);
			itemCache.tvPrice = (TextView) convertView.findViewById(R.id.price);
			itemCache.ivAddToCart = (ImageView) convertView.findViewById(R.id.addToCart);
			convertView.setTag(itemCache);
		}
		ItemCache itemCache = (ItemCache) convertView.getTag();
		final ProductEntity productEntity = list.get(position);
		YaoHuoLaApplication.disPlayFromUrl(productEntity.getPic(), itemCache.ivPic, R.drawable.default_product_icon);
		itemCache.tvName.setText(productEntity.getName());
		itemCache.tvDescription.setText(productEntity.getSpec());
		itemCache.tvPrice.setText("¥" + productEntity.getPrice());
		itemCache.ivAddToCart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (productEntity.getStock_num() > 0) {
					AddToCartTask.addToCart(context, productEntity.getId());
				}

			}

		});
		if (productEntity.getStock_num() > 0) {
			itemCache.ivAddToCart.setImageResource(R.drawable.shopping_cart);
		} else {
			itemCache.ivAddToCart.setImageResource(R.drawable.shopping_cart_grey);
		}
		return convertView;
	}

	private class ItemCache {
		public ImageView ivPic;// 图片
		public TextView tvName;// 名称
		public TextView tvDescription;// 描述
		public TextView tvPrice;// 价格
		private ImageView ivAddToCart;// 加入购物车

	}

}
