package com.yaohuola.my.activity;

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
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.CollectBabyEntity;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.my.adapter.CollectBabyAdapter;
import com.yaohuola.task.HttpTask;
import com.yaohuola.view.SilderListView;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CollectBabyActivity extends BaseActivity implements OnItemClickListener {

	private int pageNum = 1;
	private SilderListView silderListView;
	private List<CollectBabyEntity> collectBabyEntities;
	private CollectBabyAdapter adapter;
	private int total_pages;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_collect_baby);

	}

	@Override
	public void initView() {
		findViewById(R.id.back).setOnClickListener(this);
		silderListView = (SilderListView) findViewById(R.id.listView);
		collectBabyEntities = new ArrayList<CollectBabyEntity>();
		adapter = new CollectBabyAdapter(this, collectBabyEntities);
		silderListView.setAdapter(adapter);
		silderListView.setOnItemClickListener(this);
		findViewById(R.id.empty).setOnClickListener(this);
		silderListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// 当不滚动时
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// 判断是否滚动到底部
					if (view.getLastVisiblePosition() == view.getCount() - 1) {
						// 加载更多功能的代码
						if (pageNum <= total_pages) {
							pageNum++;
							getData();
						}
					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});
		getData();
	}

	private void getData() {
		String token = LocalCache.getInstance(this).getToken();
		if (TextUtils.isEmpty(token)) {
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("page_num", pageNum + "");
		new HttpTask(this, HttpTask.GET, "v1/favorites", map) {

			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					total_pages = jsonObject.optInt("total_pages", -1);
					JSONArray jsonArray = jsonObject.optJSONArray("favorites");
					if (jsonArray != null && jsonArray.length() > 0) {
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = jsonArray.optJSONObject(i);
							CollectBabyEntity collectBabyEntity = new CollectBabyEntity();
							ProductEntity productEntity = new ProductEntity();
							if (jsonObject2 == null) {
								continue;
							}
							collectBabyEntity.setCollectBabyId(jsonObject2.optString("unique_id", ""));
							productEntity.setId(jsonObject2.optString("product_unique_id", ""));
							productEntity.setName(jsonObject2.optString("product_name", ""));
							productEntity.setPic(jsonObject2.optString("product_image", ""));
							productEntity.setPrice(jsonObject2.optDouble("product_price"));
							productEntity.setSpec(jsonObject2.optString("product_spec", ""));
							collectBabyEntity.setProductEntity(productEntity);
							collectBabyEntities.add(collectBabyEntity);
						}
						if (collectBabyEntities.size() > 0) {
							adapter.notifyDataSetChanged();
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}

			};
		}.run();
	}

	@Override
	public void refreshData() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.empty:
			if (collectBabyEntities.size() > 0) {
				empty();
			}
			break;
		default:
			break;
		}

	}

	private void empty() {
		String token = LocalCache.getInstance(this).getToken();
		if (TextUtils.isEmpty(token)) {
			return;
		}
		new HttpTask(this, HttpTask.GET, "v1/favorites/delete_all?token=" + token, null) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						Toast.makeText(context, "清空收藏成功", Toast.LENGTH_SHORT).show();
						collectBabyEntities.clear();
						adapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CollectBabyEntity collectBabyEntity = (CollectBabyEntity) parent.getItemAtPosition(position);
		Intent intent = new Intent(this, ProductDetailsActivity.class);
		intent.putExtra("id", collectBabyEntity.getProductEntity().getId());
		startActivity(intent);
	}
}
