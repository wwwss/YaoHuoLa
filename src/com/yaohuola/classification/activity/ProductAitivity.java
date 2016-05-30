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
import com.yaohuola.classification.adapter.ProductGridViewAdapter;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.interfaces.AddShoppingCartListener;
import com.yaohuola.interfaces.AutoLoadListener;
import com.yaohuola.interfaces.AutoLoadListener.AutoLoadCallBack;
import com.yaohuola.task.HttpTask;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author admin 产品页面
 */
public class ProductAitivity extends BaseActivity
		implements OnItemClickListener, AutoLoadCallBack, AddShoppingCartListener {

	private GridView productGridView;// 产品
	private ProductGridViewAdapter productGridViewAdapter;
	private List<ProductEntity> productEntities;
	private TextView tv_title;
	private TextView tv_Classify;
	private RelativeLayout footview;
	private int type;
	private String title;
	private String id;
	private ImageView iv_shopping_cart;
	private int total_pages;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_product);

	}

	@Override
	public void initView() {
		type = getIntent().getIntExtra("type", -1);
		id = getIntent().getStringExtra("id");
		title = getIntent().getStringExtra("title");
		tv_title = (TextView) findViewById(R.id.title);
		findViewById(R.id.back).setOnClickListener(this);
		tv_Classify = (TextView) findViewById(R.id.classify);
		tv_Classify.setOnClickListener(this);
		productGridView = (GridView) findViewById(R.id.productGridView);
		productEntities = new ArrayList<ProductEntity>();
		productGridViewAdapter = new ProductGridViewAdapter(this, productEntities, this);
		productGridView.setAdapter(productGridViewAdapter);
		productGridView.setOnItemClickListener(this);
		iv_shopping_cart = (ImageView) findViewById(R.id.shopping_cart);
		iv_shopping_cart.setOnClickListener(this);
		footview = (RelativeLayout) findViewById(R.id.footview);
		// 添加自动翻页的事件
		AutoLoadListener autoLoadListener = new AutoLoadListener(this);
		productGridView.setOnScrollListener(autoLoadListener);
		if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(title)) {
			tv_title.setText(title);
			if (type == 2) {
				tv_Classify.setVisibility(View.VISIBLE);
			} else {
				tv_Classify.setVisibility(View.GONE);
			}
			getData();
		}

	}

	/**
	 * 
	 * @param requestParameter请求参数
	 * @param requestType请求类型
	 */
	private void getData() {
		String funcName = null;
		int HttpType = -1;
		Map<String, String> map = new HashMap<String, String>();
		String token = LocalCache.getInstance(this).getToken();
		if (!TextUtils.isEmpty(token)) {
			map.put("token", token);
		}
		switch (type) {
		case 0:
			HttpType = HttpTask.GET;
			map.put("page_num", pageNum + "");
			funcName = "v1/detail_categories/" + id;
			break;
		case 1:
			HttpType = HttpTask.GET;
			map.put("page_num", pageNum + "");
			funcName = "v1/products/sub_category/" + id;
			break;
		case 2:
			HttpType = HttpTask.POST;
			map.put("key_word", title);
			map.put("page_num", pageNum + "");
			funcName = "v1/products/search";
			break;
		}
		new HttpTask(this, HttpType, funcName, map) {
			protected void onPostExecute(String result) {
				hideFootView();
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						total_pages = (jsonObject.optInt("total_pages", 1));
						JSONArray jsonArray = jsonObject.optJSONArray("products");
						if (jsonArray == null) {
							return;
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = jsonArray.optJSONObject(i);
							if (jsonObject2 == null) {
								continue;
							}
							ProductEntity productEntity = new ProductEntity();
							productEntity.setId(jsonObject2.optString("unique_id", ""));
							productEntity.setName(jsonObject2.optString("name", ""));
							productEntity.setPic(jsonObject2.optString("image", ""));
							productEntity.setDescription(jsonObject2.optString("desc", ""));
							productEntity.setPrice(jsonObject2.optDouble("price", 0));
							productEntity.setSpec(jsonObject2.optString("spec", ""));
							productEntity.setStock_num(jsonObject2.optInt("stock_num", 0));
							productEntity.setNumber(jsonObject2.optInt("number", 0));
							productEntity.setCart_item_unique_id(jsonObject2.optString("cart_item_unique_id", ""));
							productEntities.add(productEntity);
						}
						if (productEntities.size() > 0) {
							productGridViewAdapter.notifyDataSetChanged();
						}

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.classify:
			intent = new Intent();
			intent.putExtra("classifyId", id);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.shopping_cart:
			intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra("type", 2);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, ProductDetailsActivity.class);
		ProductEntity productEntity = (ProductEntity) parent.getItemAtPosition(position);
		intent.putExtra("id", productEntity.getId());
		startActivity(intent);

	}

	@Override
	public void refreshData() {
	}

	private boolean IsLoading;
	private int pageNum = 1;

	private void hideFootView() {
		footview.setVisibility(View.GONE);
		IsLoading = false;
	}

	@Override
	public void execute() {
		if (!IsLoading) {
			pageNum++;
			if (pageNum <= total_pages) {
				IsLoading = true;
				footview.setVisibility(View.VISIBLE);
				getData();
			}
		}

	}

	@Override
	public void addSucceed(int number) {
		Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
		shake.reset();
		shake.setFillAfter(true);
		iv_shopping_cart.setAnimation(shake);
	}

}
