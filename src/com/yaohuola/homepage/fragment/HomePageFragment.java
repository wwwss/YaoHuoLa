package com.yaohuola.homepage.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.library.view.SAGridView;
import com.yaohuola.classification.activity.ProductDetailsActivity;
import com.yaohuola.data.entity.BannerEntity;
import com.yaohuola.data.entity.ClassifyEntity;
import com.yaohuola.data.entity.HotSaleEntity;
import com.yaohuola.homepage.adapter.ClassifyAdapter;
import com.yaohuola.homepage.adapter.HotSaleAdapter;
import com.yaohuola.task.HttpTask;
import com.yaohuola.task.SearchTask;
import com.yaohuola.view.SlideShowView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * 
 * @author admin 首页视图
 */
public class HomePageFragment extends Fragment implements OnClickListener, OnItemClickListener {
	private SlideShowView slideShowView;
	private SAGridView classifyGridView;// 分类
	private ClassifyAdapter classifyAdapter;// 分类适配器
	private List<ClassifyEntity> classifyEntities;
	private SAGridView hotSaleGridView;// 热卖
	private HotSaleAdapter hotSaleAdapter;
	private List<HotSaleEntity> hotSaleEntities;
	private Context context;
	private EditText etSearch;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_homepage, container, false);
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
		slideShowView = (SlideShowView) view.findViewById(R.id.slideshowView);
		classifyGridView = (SAGridView) view.findViewById(R.id.classifyGridView);
		classifyEntities = new ArrayList<ClassifyEntity>();
		classifyAdapter = new ClassifyAdapter(getActivity(), classifyEntities);
		classifyGridView.setAdapter(classifyAdapter);
		classifyGridView.setOnItemClickListener(this);
		hotSaleGridView = (SAGridView) view.findViewById(R.id.hotSaleGridView);
		hotSaleEntities = new ArrayList<HotSaleEntity>();
		hotSaleAdapter = new HotSaleAdapter(getActivity(), hotSaleEntities);
		hotSaleGridView.setAdapter(hotSaleAdapter);
		hotSaleGridView.setOnItemClickListener(this);
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
					SearchTask.search(getActivity(), keyWord);
					return true;
				}
				return false;
			}

		});
		view.findViewById(R.id.seach).setOnClickListener(this);
		// 获取数据
		getData();
	}

	/**
	 * 获取数据的方法
	 */
	public void getData() {
		if (classifyEntities.size() > 0) {
			classifyEntities.clear();
		}
		int[] picArray = { R.drawable.nuts_icon, R.drawable.dry_cargo_icon, R.drawable.cereal_icon,
				R.drawable.flowering_tea_icon, R.drawable.snacks_icon, R.drawable.milk_icon, R.drawable.candy_icon,
				R.drawable.biscuit_icon };
		String[] nameArray = { "坚果", "干货", "杂粮", "花茶", "零食", "牛奶", "糖果", "饼干" };
		for (int i = 0; i < picArray.length; i++) {
			ClassifyEntity classifyEntity = new ClassifyEntity();
			classifyEntity.setDrawable(picArray[i]);
			classifyEntity.setName(nameArray[i]);
			classifyEntities.add(classifyEntity);
		}
		if (classifyEntities.size() > 0) {
			classifyAdapter.notifyDataSetChanged();
		}
		new HttpTask(context, HttpTask.GET, "adverts", null) {
			@SuppressWarnings("null")
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						List<BannerEntity> imageList = new ArrayList<BannerEntity>();
						JSONArray bannerArray = jsonObject.optJSONArray("adverts");
						if (bannerArray == null&&bannerArray.length()==0) {
							return;
						}
						for (int i = 0; i < bannerArray.length(); i++) {
							BannerEntity banner = new BannerEntity();
							JSONObject bannerObj = bannerArray.optJSONObject(i);
							if (bannerObj == null) {
								continue;
							}
							banner.setImage_url(bannerObj.optString("image_url", ""));
							banner.setId(bannerObj.optInt("unique_id", -1));
							banner.setProduct_unique_id(bannerObj.optString("product_unique_id", ""));
							banner.setTitle(bannerObj.optString("title", ""));
							imageList.add(banner);
						}
						slideShowView.setData(imageList);
						JSONArray jsonArray = jsonObject.optJSONArray("populars");
						if (jsonArray == null) {
							return;
						}
						if (hotSaleEntities.size() > 0) {
							hotSaleEntities.clear();
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = jsonArray.optJSONObject(i);
							if (jsonObject2 == null) {
								continue;
							}
							HotSaleEntity hotSaleEntity = new HotSaleEntity();
							hotSaleEntity.setId(jsonObject2.optString("unique_id", ""));
							hotSaleEntity.setName(jsonObject2.optString("name", ""));
							hotSaleEntity.setPic(jsonObject2.optString("image", ""));
							hotSaleEntity.setDescription(jsonObject2.optString("desc", ""));
							hotSaleEntity.setPrice(jsonObject2.optDouble("price", 0));
							hotSaleEntity.setSpec(jsonObject2.optString("spec", ""));
							hotSaleEntity.setStock_num(jsonObject2.optInt("stock_num", 0));
							hotSaleEntities.add(hotSaleEntity);
						}
						if (hotSaleEntities.size() > 0) {
							hotSaleAdapter.notifyDataSetChanged();
							getView().findViewById(R.id.tips).setVisibility(View.VISIBLE);
						}else{
							getView().findViewById(R.id.tips).setVisibility(View.GONE);	
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();
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

	/**
	 * Fragment的懒加载
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		// 判断显示才加载滚动banner
		if (getUserVisibleHint()) {
			handler.sendEmptyMessage(1002);
		} else {
			handler.sendEmptyMessage(1003);
		}

	}


	@Override
	public void onStop() {
		if (getUserVisibleHint()) {
			handler.sendEmptyMessage(1003);
		}
		super.onStop();
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
			SearchTask.search(getActivity(), keyWord);
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		switch (parent.getId()) {
		case R.id.classifyGridView:
			ClassifyEntity classifyEntity = (ClassifyEntity) parent.getItemAtPosition(position);
			if (!TextUtils.isEmpty(classifyEntity.getName())) {
				SearchTask.search(getActivity(), classifyEntity.getName());
			}
			break;
		case R.id.hotSaleGridView:
			Intent intent = new Intent(context, ProductDetailsActivity.class);
			HotSaleEntity hotSaleEntity = (HotSaleEntity) parent.getItemAtPosition(position);
			intent.putExtra("id", hotSaleEntity.getId());
			startActivity(intent);
			break;

		default:
			break;
		}

	}
}
