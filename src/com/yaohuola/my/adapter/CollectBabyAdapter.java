package com.yaohuola.my.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.yaohuola.YaoHuoLaApplication;
import com.yaohuola.adapter.BaseAdapter;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.CollectBabyEntity;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.task.HttpTask;
import com.yaohuola.view.SliderView;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CollectBabyAdapter extends BaseAdapter<CollectBabyEntity> {

	public CollectBabyAdapter(Context context, List<CollectBabyEntity> list) {
		super(context, list);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		SliderView slideView = (SliderView) convertView;
		if (slideView == null) {
			View itemView = LayoutInflater.from(context).inflate(R.layout.adapter_collect_baby, null);
			slideView = new SliderView(context);
			slideView.setContentView(itemView);
			ItemCache itemCache = new ItemCache();
			itemCache.tvName = (TextView) slideView.findViewById(R.id.productName);
			itemCache.ivImage = (ImageView) slideView.findViewById(R.id.productImage);
			itemCache.tvSpec = (TextView) slideView.findViewById(R.id.productSpecification);
			itemCache.tvCurrentPrice = (TextView) slideView.findViewById(R.id.productCurrentPrice);
			itemCache.deleteHolder = (ViewGroup) slideView.findViewById(R.id.holder);
			slideView.setTag(itemCache);
		}
		ItemCache itemCache = (ItemCache) slideView.getTag();
		final CollectBabyEntity collectBabyEntity = list.get(position);
		ProductEntity productEntity = collectBabyEntity.getProductEntity();
		slideView.shrink();
		YaoHuoLaApplication.disPlayFromUrl(productEntity.getPic(), itemCache.ivImage, R.drawable.default_product_icon);
		itemCache.tvName.setText(productEntity.getName());
		itemCache.tvSpec.setText(productEntity.getSpec());
		itemCache.tvCurrentPrice.setText("¥" + productEntity.getPrice());
		itemCache.deleteHolder.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				delete(collectBabyEntity.getCollectBabyId(), position);
			}
		});
		return slideView;
	}

	private class ItemCache {
		private TextView tvName;
		private ImageView ivImage;
		private TextView tvCurrentPrice;
		private TextView tvSpec;
		private ViewGroup deleteHolder;
	}

	private void delete(String id, final int position) {
		Map<String, String> map = new HashMap<String, String>();
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			return;
		}
		map.put("token", token);
		map.put("unique_id", id);
		new HttpTask(context, HttpTask.DELETE, "v1/favorites", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						Toast.makeText(context, "删除收藏成功", Toast.LENGTH_SHORT).show();
						list.remove(position);
						notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();
	}
}
