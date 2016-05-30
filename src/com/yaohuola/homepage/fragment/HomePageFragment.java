package com.yaohuola.homepage.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.library.uitls.AppUtils;
import com.library.view.SAGridView;
import com.yaohuola.activity.SearchActivity;
import com.yaohuola.classification.activity.ProductAitivity;
import com.yaohuola.classification.activity.ProductDetailsActivity;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.BannerEntity;
import com.yaohuola.data.entity.ClassifyEntity;
import com.yaohuola.data.entity.HotSaleEntity;
import com.yaohuola.homepage.adapter.ClassifyAdapter;
import com.yaohuola.homepage.adapter.HotSaleAdapter;
import com.yaohuola.interfaces.FragmentSwitchListenter;
import com.yaohuola.task.HttpTask;
import com.yaohuola.view.SlideShowView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * 
 * @author admin 首页视图
 */
public class HomePageFragment extends Fragment implements OnClickListener, OnItemClickListener, OnTouchListener {
	private SlideShowView slideShowView;
	private SAGridView classifyGridView;// 分类
	private ClassifyAdapter classifyAdapter;// 分类适配器
	private List<ClassifyEntity> classifyEntities;
	private SAGridView hotSaleGridView;// 热卖
	private HotSaleAdapter hotSaleAdapter;
	private List<HotSaleEntity> hotSaleEntities;
	private Context context;
	private ScrollView scrollView;
	private RelativeLayout footview;
	private View view;
	private FragmentSwitchListenter listenter;

	public HomePageFragment(FragmentSwitchListenter listenter) {
		this.listenter = listenter;
	}

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
		view = getView();
		context = getActivity();
		// etSearch = (EditText) view.findViewById(R.id.edit);
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
		scrollView = (ScrollView) view.findViewById(R.id.scrollView);
		scrollView.setOnTouchListener(this);
		footview = (RelativeLayout) view.findViewById(R.id.footview);
		view.findViewById(R.id.rlSearch).setOnClickListener(this);
		// 获取Banner数据
		getData();
		// 获取热门产品数据
		getPopularsData();
	}

	/**
	 * 获取数据的方法
	 */
	public void getData() {
		new HttpTask(context, HttpTask.GET, "v1/adverts/advert", null) {
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
						if (bannerArray == null || bannerArray.length() == 0) {
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
						if (imageList.size() > 0) {
							slideShowView.setData(imageList);
						}
						JSONArray jsonArray = jsonObject.optJSONArray("categories");
						if (jsonArray == null || jsonArray.length() == 0) {
							return;
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject2 = jsonArray.optJSONObject(i);
							if (jsonObject2 == null) {
								continue;
							}
							ClassifyEntity classifyEntity = new ClassifyEntity();
							classifyEntity.setPic(jsonObject2.optString("image", ""));
							classifyEntity.setName(jsonObject2.optString("name", ""));
							classifyEntity.setId(jsonObject2.optString("unique_id", ""));
							classifyEntities.add(classifyEntity);
						}
						if (classifyEntities.size() > 0) {
							classifyAdapter.notifyDataSetChanged();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();

	}

	private int pageNum = 1;
	private int total_pages;

	/**
	 * 获取热门产品数据
	 */
	private void getPopularsData() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("page_num", pageNum + "");
		String token = LocalCache.getInstance(context).getToken();
		if (!TextUtils.isEmpty(token)) {
			map.put("token", token);
		}
		new HttpTask(context, HttpTask.GET, "v1/adverts/hot_products", map) {
			@SuppressWarnings("null")
			protected void onPostExecute(String result) {
				handler.sendEmptyMessage(1004);
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						total_pages = jsonObject.optInt("total_pages", 1);
						JSONArray jsonArray = jsonObject.optJSONArray("populars");
						if (jsonArray == null && jsonArray.length() == 0) {
							return;
						}
						if (pageNum == 1) {
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
							hotSaleEntity.setCart_item_unique_id(jsonObject2.optString("cart_item_unique_id", ""));
							hotSaleEntity.setNumber(jsonObject2.optInt("number", 0));
							hotSaleEntities.add(hotSaleEntity);
						}
						if (hotSaleEntities.size() > 0) {
							hotSaleAdapter.notifyDataSetChanged();
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
			case 1004:
				footview.setVisibility(View.GONE);
				IsLoading = false;
				break;
			case 1005:
				getView().findViewById(R.id.tips).setVisibility(View.VISIBLE);
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
			pageNum = 1;
			getPopularsData();
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
		case R.id.rlSearch:
			startActivity(new Intent(context, SearchActivity.class));
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
				search(classifyEntity.getName(), classifyEntity.getId());
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

	private int lastY = 0;
	private boolean IsLoading;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			lastY = scrollView.getScrollY();
			if (lastY >= scrollView.getHeight() - 100 && !IsLoading) {
				pageNum++;
				if (pageNum <= total_pages) {
					IsLoading = true;
					footview.setVisibility(View.VISIBLE);
					getPopularsData();
				} else {
					view.findViewById(R.id.tips).setVisibility(View.VISIBLE);
				}

			}
		}
		return false;
	}

	/**
	 * 搜索
	 * 
	 * @param context
	 * @param keyWord
	 * @param searchType
	 * 
	 */
	public void search(final String searchContent, final String classifyId) {
		if (AppUtils.isFastClick()) {
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("page_num", 1 + "");
		map.put("key_word", searchContent);
		new HttpTask(context, HttpTask.POST, "v1/products/search", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						Intent intent = new Intent(context, ProductAitivity.class);
						intent.putExtra("type", 2);
						intent.putExtra("title", searchContent);
						intent.putExtra("id", classifyId);
						startActivityForResult(intent, 1001);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			};
		}.run();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 如果返回失败，退出
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case 1001:
			String classifyId = data.getStringExtra("classifyId");
			listenter.go(1, classifyId);
			break;

		}
	}
}
