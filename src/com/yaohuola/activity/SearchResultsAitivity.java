package com.yaohuola.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.yaohuola.classification.activity.ProductDetailsActivity;
import com.yaohuola.classification.adapter.ProductGridViewAdapter;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.data.entity.SmallClassifyEntity;
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
 * @author admin 搜索结果页面
 */
public class SearchResultsAitivity extends BaseActivity
		implements OnItemClickListener, AutoLoadCallBack, AddShoppingCartListener {

	private GridView productGridView;// 产品
	private ProductGridViewAdapter productGridViewAdapter;
	private List<ProductEntity> productEntities;
	private SmallClassifyEntity smallClassifyEntity;
	private RelativeLayout footview;
	private String searchContent;
	private TextView tvSearchInput;
	private ImageView iv_shopping_cart;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_search_results);

	}

	@Override
	public void initView() {
		smallClassifyEntity = (SmallClassifyEntity) getIntent().getSerializableExtra("smallClassifyEntity");
		searchContent = getIntent().getStringExtra("searchContent");
		tvSearchInput = (TextView) findViewById(R.id.searchInput);
		productGridView = (GridView) findViewById(R.id.productGridView);
		productEntities = new ArrayList<ProductEntity>();
		productGridViewAdapter = new ProductGridViewAdapter(this, productEntities, this);
		productGridView.setAdapter(productGridViewAdapter);
		productGridView.setOnItemClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.searchInput).setOnClickListener(this);
		iv_shopping_cart = (ImageView) findViewById(R.id.shopping_cart);
		iv_shopping_cart.setOnClickListener(this);
		if (smallClassifyEntity != null) {
			productEntities.addAll(smallClassifyEntity.getProductEntities());
			productGridViewAdapter.notifyDataSetChanged();
			footview = (RelativeLayout) findViewById(R.id.footview);
			// 添加自动翻页的事件
			AutoLoadListener autoLoadListener = new AutoLoadListener(this);
			productGridView.setOnScrollListener(autoLoadListener);
			tvSearchInput.setText(searchContent);
		}
	}

	/**
	 * 
	 * @param requestParameter请求参数
	 * @param requestType请求类型
	 */
	private void getData() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("page_num", pageNum + "");
		map.put("key_word", searchContent);
		new HttpTask(this, HttpTask.POST, "v1/products/search_name", map) {
			protected void onPostExecute(String result) {
				hideFootView();
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						smallClassifyEntity.setTotal_pages(jsonObject.optInt("total_pages", 1));
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
		switch (v.getId()) {
		case R.id.back:
		case R.id.searchInput:
			finish();
			break;
		case R.id.shopping_cart:
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
			if (pageNum <= smallClassifyEntity.getTotal_pages()) {
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
