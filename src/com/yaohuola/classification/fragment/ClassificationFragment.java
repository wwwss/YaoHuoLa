package com.yaohuola.classification.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.yaohuola.classification.adapter.AllClassifyAdapter;
import com.yaohuola.classification.adapter.SmallClassifyAdapter;
import com.yaohuola.data.entity.ClassifyEntity;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.data.entity.SmallClassifyEntity;
import com.yaohuola.task.HttpTask;
import com.yaohuola.task.SearchTask;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

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
	private EditText etSearch;

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
		etSearch = (EditText) view.findViewById(R.id.edit);
		allClassificationListView = (ListView) view.findViewById(R.id.allClassifyListView);
		classifyEntities = new ArrayList<ClassifyEntity>();
		allClassifyAdapter = new AllClassifyAdapter(context, classifyEntities);
		allClassificationListView.setAdapter(allClassifyAdapter);
		allClassificationListView.setOnItemClickListener(this);
		smallclassifyListView = (ListView) view.findViewById(R.id.smallclassifyListView);
		smallClassifyEntities = new ArrayList<SmallClassifyEntity>();
		smallClassifyAdapter = new SmallClassifyAdapter(context, smallClassifyEntities);
		smallclassifyListView.setAdapter(smallClassifyAdapter);
		etSearch.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH) {

					InputMethodManager imm = (InputMethodManager) v.getContext()
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					if (imm.isActive()) {
						imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
					}
					String keyWord = etSearch.getText().toString();
					if (TextUtils.isEmpty(keyWord)) {
						Toast.makeText(context, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
						return false;
					}
					SearchTask.search(getActivity(),keyWord);
					return true;
				}
				return false;
			}

		});
		view.findViewById(R.id.seach).setOnClickListener(this);
		// 获取数据
		getData();
	}

	// @Override
	// public void onResume() {
	// if (getUserVisibleHint()) {
	// getData();
	// }
	// super.onResume();
	// }

	/**
	 * 获取数据
	 */
	public void getData() {
		new HttpTask(context, HttpTask.GET, "categories", null) {
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
		new HttpTask(context, HttpTask.GET, "categories/" + id, null) {
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
						if (jsonArray.length()==0) {
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
								productEntities.add(productEntity);
							}
							smallClassifyEntity.setProductEntities(productEntities);
							smallClassifyEntities.add(smallClassifyEntity);
						}
						if (smallClassifyEntities.size() > 0) {
							smallClassifyAdapter.notifyDataSetChanged();
							allClassifyAdapter.setSelectItem(postion);
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
		case R.id.seach:
			String keyWord = etSearch.getText().toString();
			if (TextUtils.isEmpty(keyWord)) {
				Toast.makeText(context, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			SearchTask.search(getActivity(),keyWord);
			break;

		default:
			break;
		}
	}

	

}
