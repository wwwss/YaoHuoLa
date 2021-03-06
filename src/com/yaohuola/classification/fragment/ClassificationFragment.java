package com.yaohuola.classification.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.yaohuola.activity.MainActivity;
import com.yaohuola.activity.SearchActivity;
import com.yaohuola.classification.adapter.AllClassifyAdapter;
import com.yaohuola.classification.adapter.SmallClassifyAdapter;
import com.yaohuola.data.entity.ClassifyEntity;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.data.entity.SmallClassifyEntity;
import com.yaohuola.task.HttpTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 
 * @author admin 分类视图
 */
public class ClassificationFragment extends Fragment implements OnItemClickListener, OnClickListener {

	private ListView allClassificationListView;
	private List<ClassifyEntity> classifyEntities;
	private AllClassifyAdapter allClassifyAdapter;
	private ListView smallclassifyListView;
	private List<SmallClassifyEntity> smallClassifyEntities;
	private SmallClassifyAdapter smallClassifyAdapter;
	private Context context;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_classify, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView() {
		View view = getView();
		context = getActivity();
		allClassificationListView = (ListView) view.findViewById(R.id.allClassifyListView);
		classifyEntities = new ArrayList<ClassifyEntity>();
		allClassifyAdapter = new AllClassifyAdapter(context, classifyEntities);
		allClassificationListView.setAdapter(allClassifyAdapter);
		allClassificationListView.setOnItemClickListener(this);
		smallclassifyListView = (ListView) view.findViewById(R.id.smallclassifyListView);
		smallClassifyEntities = new ArrayList<SmallClassifyEntity>();
		smallClassifyAdapter = new SmallClassifyAdapter(context, smallClassifyEntities);
		smallclassifyListView.setAdapter(smallClassifyAdapter);
		view.findViewById(R.id.rlSearch).setOnClickListener(this);
		// 获取数据
		getData();
	}

	/**
	 * 获取数据
	 */
	public void getData() {
		new HttpTask(context, HttpTask.GET, "v1/categories", null) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						JSONArray jsonArray = jsonObject.optJSONArray("categories");
						if (jsonArray == null) {
							return;
						}
						classifyEntities.clear();
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = jsonArray.optJSONObject(i);
							ClassifyEntity classifyEntity = new ClassifyEntity();
							if (jsonObject2 == null) {
								continue;
							}
							classifyEntity.setId(jsonObject2.optString("unique_id", ""));
							classifyEntity.setName(jsonObject2.optString("name", ""));
							classifyEntities.add(classifyEntity);
						}
						if (classifyEntities.size() > 0) {
							allClassifyAdapter.notifyDataSetChanged();
						}
						JSONArray jsonArray2 = jsonObject.optJSONArray("sub_categories");
						if (jsonArray2 == null) {
							return;
						}
						smallClassifyEntities.clear();
						for (int i = 0; i < jsonArray2.length(); i++) {
							JSONObject jsonObject3 = jsonArray2.optJSONObject(i);
							if (jsonObject3 == null) {
								continue;
							}
							SmallClassifyEntity smallClassifyEntity = new SmallClassifyEntity();
							smallClassifyEntity.setId(jsonObject3.optString("unique_id", ""));
							smallClassifyEntity.setTitle(jsonObject3.optString("name", ""));
							List<ProductEntity> productEntities = new ArrayList<ProductEntity>();
							JSONArray jsonArray3 = jsonObject3.optJSONArray("details_categories");
							if (jsonArray3 == null) {
								continue;
							}
							for (int j = 0; j < jsonArray3.length(); j++) {
								JSONObject jsonObject4 = jsonArray3.optJSONObject(j);
								if (jsonObject4 == null) {
									continue;
								}
								ProductEntity productEntity = new ProductEntity();
								productEntity.setId(jsonObject4.optString("unique_id", ""));
								productEntity.setName(jsonObject4.optString("name", ""));
								productEntity.setPic(jsonObject4.optString("image", ""));
								productEntities.add(productEntity);
							}
							smallClassifyEntity.setProductEntities(productEntities);
							smallClassifyEntities.add(smallClassifyEntity);
						}
						if (smallClassifyEntities.size() > 0) {
							smallClassifyAdapter.notifyDataSetChanged();

						}
						allClassifyAdapter.setSelectItem(0);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			};
		}.run();
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int postion, long arg3) {
		switch (adapterView.getId()) {

		case R.id.allClassifyListView:
			ClassifyEntity classifyEntity = (ClassifyEntity) adapterView.getItemAtPosition(postion);
			getSmallClassData(classifyEntity.getId(), postion);
			break;

		default:
			break;
		}

	}

	/**
	 * 获取小分类列表
	 */
	private void getSmallClassData(String id, final int postion) {
		new HttpTask(context, HttpTask.GET, "v1/categories/" + id, null) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						JSONArray jsonArray = jsonObject.optJSONArray("sub_categories");
						if (jsonArray == null) {
							return;
						}
						if (jsonArray.length() == 0) {
							return;
						}
						smallClassifyEntities.clear();
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = jsonArray.optJSONObject(i);
							if (jsonObject2 == null) {
								continue;
							}
							SmallClassifyEntity smallClassifyEntity = new SmallClassifyEntity();
							smallClassifyEntity.setId(jsonObject2.optString("unique_id", ""));
							smallClassifyEntity.setTitle(jsonObject2.optString("name", ""));
							List<ProductEntity> productEntities = new ArrayList<ProductEntity>();
							JSONArray jsonArray2 = jsonObject2.optJSONArray("details_categories");
							if (jsonArray2 == null) {
								continue;
							}
							for (int j = 0; j < jsonArray2.length(); j++) {
								JSONObject jsonObject3 = jsonArray2.optJSONObject(j);
								if (jsonObject3 == null) {
									continue;
								}
								ProductEntity productEntity = new ProductEntity();
								productEntity.setId(jsonObject3.optString("unique_id", ""));
								productEntity.setName(jsonObject3.optString("name", ""));
								productEntity.setPic(jsonObject3.optString("image", ""));
								productEntities.add(productEntity);
							}
							smallClassifyEntity.setProductEntities(productEntities);
							smallClassifyEntities.add(smallClassifyEntity);
						}
						if (smallClassifyEntities.size() > 0) {
							smallClassifyAdapter.notifyDataSetChanged();
							allClassifyAdapter.setSelectItem(postion);
							allClassificationListView.setSelection(postion);
							allClassifyAdapter.notifyDataSetInvalidated();
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
		case R.id.rlSearch:
			startActivity(new Intent(context,SearchActivity.class));
			break;

		default:
			break;
		}
	}

	private MainActivity mainActivity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mainActivity = (MainActivity) activity;
		mainActivity.setHandler(handler);
	}

	public Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1001:
				String id = msg.obj.toString();
				int postion = -1;
				for (int i = 0; i < classifyEntities.size(); i++) {
					if (classifyEntities.get(i).getId().equals(id)) {
						postion = i;
					}
				}
				if (postion == -1) {
					return;
				}
				getSmallClassData(id, postion);
				break;
			}
		}

	};

}
