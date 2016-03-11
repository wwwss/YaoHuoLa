package com.yaohuola.classification.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.yaohuola.classification.adapter.ProductGridViewAdapter;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.data.entity.SmallClassifyEntity;
import com.yaohuola.task.HttpTask;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

/**
 * 
 * @author admin 产品页面
 */
public class ProductAitivity extends BaseActivity implements OnItemClickListener {

	private GridView productGridView;// 产品
	private ProductGridViewAdapter productGridViewAdapter;
	private List<ProductEntity> productEntities;
	private SmallClassifyEntity smallClassifyEntity;
	private int index;
	private TextView tv_title;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_product);

	}

	@Override
	public void initView() {
		productGridView = (GridView) findViewById(R.id.productGridView);
		productEntities = new ArrayList<ProductEntity>();
		productGridViewAdapter = new ProductGridViewAdapter(this, productEntities);
		productGridView.setAdapter(productGridViewAdapter);
		productGridView.setOnItemClickListener(this);
		findViewById(R.id.back).setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.title);
		smallClassifyEntity = (SmallClassifyEntity) getIntent().getSerializableExtra("smallClassifyEntity");
		index = getIntent().getIntExtra("index", 0);
		if (smallClassifyEntity == null) {
			return;
		}
		if (index == -1) {
			String title = getIntent().getStringExtra("title");
			tv_title.setText(title);
			productEntities.addAll(smallClassifyEntity.getProductEntities());
			productGridViewAdapter.notifyDataSetChanged();
		} else {
			getData(smallClassifyEntity.getProductEntities().get(index).getId());
			tv_title.setText(smallClassifyEntity.getProductEntities().get(index).getName());
		}

	}

	/**
	 * 获取数据
	 */
	private void getData(String id) {
		new HttpTask(this, HttpTask.GET, "detail_categories/" + id, null) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
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

}
