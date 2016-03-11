package com.yaohuola.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.yaohuola.R;
import com.yaohuola.adapter.ViewPagerAdapter;
import com.yaohuola.view.GuidePageView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * 
 * @author admin App启动引导页
 */
public class GuidePageActivity extends Activity {

	private ViewPager viewPager;
	private List<View> guidePageViews;
	private ViewPagerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView();
	}

	public void setContentView() {
		setContentView(R.layout.activity_guide_page);

		initView();
	}

	public void initView() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		int[] arrayDrawable = { R.drawable.guide_page0, R.drawable.guide_page1, R.drawable.guide_page2, };
		guidePageViews = new ArrayList<View>();
		for (int i = 0; i < arrayDrawable.length; i++) {
			GuidePageView guidePageView = new GuidePageView(this, this, arrayDrawable[i], i);
			guidePageViews.add(guidePageView.getView());
		}
		adapter = new ViewPagerAdapter(guidePageViews);
		viewPager.setAdapter(adapter);
	}

}
