package com.yaohuola.classification.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.yaohuola.activity.MainActivity;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.BannerEntity;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.my.activity.LoginActivity;
import com.yaohuola.task.AddToCartTask;
import com.yaohuola.task.HttpTask;
import com.yaohuola.view.SlideShowView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @author admin 产品详情页面
 */
public class ProductDetailsActivity extends BaseActivity {

	// private ImageView iv_pic;
	private TextView tv_producteDscription;// 产品描述
	private TextView tv_priceAndunit;// 单价和单位
	private TextView tv_output;// 供货量
	private TextView tv_packingWay;// 包装方式
	private TextView tv_suttle;// 净重
	private TextView tv_shelfLife;// 保质期
	private TextView tv_info;// 产品介绍
	private TextView tv_price;// 保质期
	// private ListView listView;// 图片列表
	// private ProductImageAdapter adapter;
	private String unique_id;// 产品ID
	private TextView tv_nowAddShoppingCart;// 立即加入购物车
	private SlideShowView slideShowView;
	private ImageView iv_collection;// 是否收藏
	private ProductEntity productEntity;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_product_details);

	}

	@Override
	public void initView() {
		unique_id = getIntent().getStringExtra("id");
		if (TextUtils.isEmpty(unique_id)) {
			return;
		}
		iv_collection = (ImageView) findViewById(R.id.collection);
		iv_collection.setOnClickListener(this);
		slideShowView = (SlideShowView) findViewById(R.id.slideshowView);
		// iv_pic = (ImageView) findViewById(R.id.pic);
		tv_producteDscription = (TextView) findViewById(R.id.producteDscription);
		tv_priceAndunit = (TextView) findViewById(R.id.priceAndunit);
		tv_output = (TextView) findViewById(R.id.output);
		tv_info = (TextView) findViewById(R.id.productInfo);
		tv_packingWay = (TextView) findViewById(R.id.packingWay);
		tv_suttle = (TextView) findViewById(R.id.suttle);
		tv_shelfLife = (TextView) findViewById(R.id.shelfLife);
		tv_price = (TextView) findViewById(R.id.price);
		// listView = (ListView) findViewById(R.id.listView);
		findViewById(R.id.back).setOnClickListener(this);
		tv_nowAddShoppingCart = (TextView) findViewById(R.id.nowAddShoppingCart);
		tv_nowAddShoppingCart.setOnClickListener(this);
		findViewById(R.id.goShoppingCart).setOnClickListener(this);
		getData();
	}

	private void getData() {
		Map<String, String> map = new HashMap<String, String>();
		String token = LocalCache.getInstance(this).getToken();
		String func = "v1/products/" + unique_id;
		if (!TextUtils.isEmpty(token)) {
			map.put("token", token);
		}
		new HttpTask(this, HttpTask.GET, func, map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						JSONObject jsonObject2 = jsonObject.optJSONObject("product");
						if (jsonObject2 == null) {
							return;
						}
						productEntity = new ProductEntity();
						productEntity.setId(jsonObject2.optString("unique_id", ""));
						productEntity.setName(jsonObject2.optString("name", ""));
						productEntity.setDescription(jsonObject2.optString("desc", ""));
						productEntity.setPrice(jsonObject2.optDouble("price", 0));
						productEntity.setSpec(jsonObject2.optString("spec", ""));
						productEntity.setOrigin(jsonObject2.optString("origin", ""));
						productEntity.setUnit(jsonObject2.optString("unit", ""));
						productEntity.setStock_num(jsonObject2.optInt("stock_num", 0));
						productEntity.setSales(jsonObject2.optString("sale_count", ""));
						productEntity.setInfo(jsonObject2.optString("info", ""));
						productEntity.setFavorites(jsonObject2.optInt("favorites", -1));
						productEntity.setUnit_price(jsonObject2.optString("unit_price",""));
						productEntity.setFavorite_unique_id(jsonObject2.optString("favorite_unique_id", ""));
						String images = jsonObject2.optString("images", "");
						if (!TextUtils.isEmpty(images)) {
							JSONArray jsonArray = new JSONArray(images);
							productEntity.setPics(jsonArray);
						}
						setData();
					} else {

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}.run();

	}

	private void setData() {
		findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
		if (productEntity.getFavorites() == 0) {
			iv_collection.setSelected(true);
		} else {
			iv_collection.setSelected(false);
		}
		tv_nowAddShoppingCart.setVisibility(View.VISIBLE);
		tv_producteDscription.setText(productEntity.getName() + "\n" + productEntity.getDescription());
		tv_priceAndunit.setText("¥" + productEntity.getPrice() + "（" + productEntity.getUnit() + "）");
		tv_output.setText("供货量：" + productEntity.getStock_num());
		tv_packingWay.setText("销量：" + productEntity.getSales());
		tv_suttle.setText("规格：" + productEntity.getSpec());
		tv_price.setText("单价：" + productEntity.getUnit_price());
		tv_shelfLife.setText("产地：" + productEntity.getOrigin());
		if (productEntity.getStock_num() > 0) {
			tv_nowAddShoppingCart.setBackgroundColor(Color.parseColor("#ff3434"));
			tv_nowAddShoppingCart.setClickable(true);
		} else {
			tv_nowAddShoppingCart.setBackgroundColor(Color.parseColor("#eeeeee"));
			tv_nowAddShoppingCart.setClickable(false);
		}
		if (!TextUtils.isEmpty(productEntity.getInfo())) {
			findViewById(R.id.graphicIntroduction).setVisibility(View.VISIBLE);
			tv_info.setVisibility(View.VISIBLE);
			tv_info.setText(productEntity.getInfo());
		}
		List<BannerEntity> imageList = new ArrayList<BannerEntity>();
		if (productEntity.getPics() == null && productEntity.getPics().length() == 0) {
			return;
		}
		for (int i = 0; i < productEntity.getPics().length(); i++) {
			try {
				BannerEntity banner = new BannerEntity();
				String imageUrl = productEntity.getPics().getString(i);
				if (TextUtils.isEmpty(imageUrl)) {
					continue;
				}
				banner.setImage_url(imageUrl);
				imageList.add(banner);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		slideShowView.setData(imageList);

	};

	@Override
	protected void onResume() {
		handler.sendEmptyMessage(1002);
		super.onResume();
	}

	@Override
	protected void onPause() {
		handler.sendEmptyMessage(1003);
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.nowAddShoppingCart:
			AddToCartTask.addToCart(this, unique_id);
			break;
		case R.id.collection:
			if (productEntity == null) {
				return;
			}
			if (iv_collection.isSelected()) {
				collection(HttpTask.DELETE, productEntity.getFavorite_unique_id());
			} else {
				collection(HttpTask.POST, productEntity.getId());
			}
			break;
		case R.id.goShoppingCart:
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("type", 2);
			startActivity(intent);
			finish();
			break;

		default:
			break;
		}

	}

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1002:
				if (!slideShowView.isPlay) {
					slideShowView.startPlay();
				}
				break;
			case 1003:
				slideShowView.stopPlay();
				break;
			}
		};
	};

	@Override
	public void refreshData() {

	}

	private void collection(final int type, String id) {
		Map<String, String> map = new HashMap<String, String>();
		String token = LocalCache.getInstance(this).getToken();
		if (TextUtils.isEmpty(token)) {
			startActivity(new Intent(this, LoginActivity.class));
			return;
		}
		map.put("token", token);
		map.put("unique_id", id);
		new HttpTask(this, type, "v1/favorites", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						if (type == HttpTask.DELETE) {
							ToastShow("取消收藏成功");
							iv_collection.setSelected(false);
						} else {
							ToastShow("收藏成功");
							productEntity.setFavorite_unique_id(jsonObject.optString("unique_id", ""));
							iv_collection.setSelected(true);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();
	}
}
