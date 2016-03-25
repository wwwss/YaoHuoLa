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
import com.library.view.SAGridView;
import com.yaohuola.classification.adapter.ProductGridViewAdapter;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.data.entity.SmallClassifyEntity;
import com.yaohuola.task.HttpTask;

import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 
 * @author admin 产品页面
 */
public class ProductAitivity extends BaseActivity implements OnItemClickListener, OnTouchListener {

	private SAGridView productGridView;// 产品
	private ProductGridViewAdapter productGridViewAdapter;
	private List<ProductEntity> productEntities;
	private SmallClassifyEntity smallClassifyEntity;
	private int index;
	private TextView tv_title;
	private ScrollView scrollView;
	private RelativeLayout footview;
	private int type;
	private String title;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_product);

	}

	@Override
	public void initView() {
		productGridView = (SAGridView) findViewById(R.id.productGridView);
		productEntities = new ArrayList<ProductEntity>();
		productGridViewAdapter = new ProductGridViewAdapter(this, productEntities);
		productGridView.setAdapter(productGridViewAdapter);
		productGridView.setOnItemClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.title);
		smallClassifyEntity = (SmallClassifyEntity) getIntent().getSerializableExtra("smallClassifyEntity");
		index = getIntent().getIntExtra("index", 0);
		type = getIntent().getIntExtra("type", -1);
		if (smallClassifyEntity == null) {
			return;
		}
		if (index == -1) {
			title = getIntent().getStringExtra("title");
			tv_title.setText(title);
			productEntities.addAll(smallClassifyEntity.getProductEntities());
			productGridViewAdapter.notifyDataSetChanged();
		} else {
			getData();
			tv_title.setText(smallClassifyEntity.getProductEntities().get(index).getName());
		}
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		scrollView.setOnTouchListener(this);
		footview = (RelativeLayout) findViewById(R.id.footview);

	}

	/**
	 * 
	 * @param requestParameter请求参数
	 * @param requestType请求类型
	 */
	private void getData() {
		if (TextUtils.isEmpty(title) && smallClassifyEntity == null) {
			hideFootView();
			return;
		}
		String funcName = null;
		int requestType = -1;
		Map<String, String> map = null;
		switch (type) {
		case 0:
			map = new HashMap<String, String>();
			map.put("page_num", pageNum +"");
			funcName = "detail_categories/" + smallClassifyEntity.getProductEntities().get(index).getId();
			requestType = HttpTask.GET;
			break;
		case 1:
			map = new HashMap<String, String>();
			map.put("page_num", pageNum + "");
			funcName = "products/sub_category/" + smallClassifyEntity.getId();
			requestType = HttpTask.GET;
			break;
		case 2:
			map = new HashMap<String, String>();
			map.put("page_num", pageNum + "");
			map.put("key_word", title);
			funcName = "products/search";
			requestType = HttpTask.POST;
			break;
		}
		if (TextUtils.isEmpty(funcName) || requestType == -1 || map == null) {
			hideFootView();
			return;
		}
		new HttpTask(this, requestType, funcName, map) {
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

	private int lastY = 0;
	private boolean IsLoading;
	private int pageNum = 1;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			lastY = scrollView.getScrollY();
			if (lastY >= scrollView.getHeight() - 100 && !IsLoading) {
				pageNum++;
				if (pageNum<=smallClassifyEntity.getTotal_pages()) {
					IsLoading = true;
					footview.setVisibility(View.VISIBLE);
					getData();
				}
			}

		}
		return false;
	}

	private void hideFootView() {
		if (footview.getVisibility() == View.VISIBLE) {
			footview.setVisibility(View.GONE);
			IsLoading = false;
		}
	}

}
