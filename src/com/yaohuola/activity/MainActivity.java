package com.yaohuola.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.library.entity.Tab;
import com.library.interfaces.OnNavigationBarClickListenter;
import com.library.view.NavigationBar;
import com.umeng.update.UmengUpdateAgent;
import com.yaohuola.YaoHuoLaApplication;
import com.yaohuola.classification.fragment.ClassificationFragment;
import com.yaohuola.homepage.fragment.HomePageFragment;
import com.yaohuola.interfaces.FragmentSwitchListenter;
import com.yaohuola.my.fragment.MyFragMent;
import com.yaohuola.shoppingcart.fragment.ShoppingcartFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements OnNavigationBarClickListenter, FragmentSwitchListenter {
	private NavigationBar navigationBar;
	// private List<Tab> tabs;
	private HomePageFragment homePageFragment;
	private ClassificationFragment classificationFragment;
	private ShoppingcartFragment shoppingcartFragment;
	private MyFragMent myFragMent;
	// 当前fragment的index
	private int currentTabIndex;
	private Fragment[] fragments;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_main);

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// 不再保存Fragment的状态，达到其随着MainActivity一起被回收的效果！
		// super.onSaveInstanceState(outState);
	}

	@Override
	public void initView() {
		navigationBar = (NavigationBar) findViewById(R.id.navigation_bar);
		homePageFragment = new HomePageFragment();
		classificationFragment = new ClassificationFragment();
		shoppingcartFragment = new ShoppingcartFragment();
		myFragMent = new MyFragMent();
		fragments = new Fragment[] { homePageFragment, classificationFragment, shoppingcartFragment, myFragMent };
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		// 添加fragments显示第一个fragment
		fragmentTransaction.add(R.id.content, homePageFragment).show(homePageFragment).commit();
		initData();
		// 检查提示更新
		UmengUpdateAgent.update(this);
		// // 静默更新
		// UmengUpdateAgent.silentUpdate(this);

	}

	private void initData() {
		int[] drawableArray = { R.drawable.tab_home_bg, R.drawable.tab_classfication_bg,
				R.drawable.tab_shopping_cart_bg, R.drawable.tab_my_bg };
		int[] tableNameArray = { R.string.home_page, R.string.classification, R.string.shopping_cart, R.string.my };
		List<Tab> tabs = new ArrayList<Tab>();
		for (int i = 0; i < drawableArray.length; i++) {
			Tab tab = new Tab();
			tab.setDrawableId(drawableArray[i]);
			tab.setIndex(i);
			tab.setTextId(tableNameArray[i]);
			tabs.add(tab);
		}
		int widthPixels = getResources().getDisplayMetrics().widthPixels;
		navigationBar.initData(tabs, widthPixels, this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.content:
			break;

		default:
			break;
		}

	}

	@Override
	public void OnNavBarClick(int index) {
		if (currentTabIndex != index) {
			FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
			fragmentTransaction.hide(fragments[currentTabIndex]);
			fragments[currentTabIndex].setUserVisibleHint(false);
			if (!fragments[index].isAdded()) {
				fragmentTransaction.add(R.id.content, fragments[index]);
			}
//			if (index == 3) {
//				if (!YaoHuoLaApplication.isLogin(this)) {
//					startActivity(new Intent(this, LoginActivity.class));
//					navigationBar.onTabSelected(currentTabIndex);
//					return;
//				}
//			}
			fragmentTransaction.show(fragments[index]).commit();
			fragments[index].setUserVisibleHint(true);
		}
		currentTabIndex = index;

	}

	@Override
	public void go(int index) {
		navigationBar.onTabSelected(index);

	}

	private long exitTime = 0;

	/**
	 * 监听返回键，点击两次退出程序
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次要货啦", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void refreshData() {
		if (fragments == null && fragments.length == 0) {
			return;
		}
		switch (currentTabIndex) {
		case 0:
			homePageFragment.getData();
			break;
		case 1:
			classificationFragment.getData();
			break;
		case 2:
			if (!YaoHuoLaApplication.isLogin(this)) {
				return;
			}
			shoppingcartFragment.getData();
			break;
		case 3:
			if (!YaoHuoLaApplication.isLogin(this)) {
				return;
			}
			myFragMent.getData();
			break;
		default:
			break;
		}

	}
}
