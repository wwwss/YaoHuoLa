package com.yaohuola.my.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.yaohuola.my.adapter.SelectAreaAdapter;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 
 * @author admin 选择区域页面
 */
public class SelectAreaActivity extends BaseActivity implements OnItemClickListener {

	private ListView listView;
	private List<String> areas;
	private SelectAreaAdapter adapter;
	private String[] areaNames = { "闸北区", "黄浦区", "徐汇区", "长宁区", "静安区", "普陀区", "虹口区", "杨浦区", "闵行区", "宝山区", "嘉定区", "浦东区",
			"金山区", "松江区", "青浦区", "奉贤区", "崇明县区" };

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_area);

	}

	@Override
	public void initView() {
		listView = (ListView) findViewById(R.id.listView);
		findViewById(R.id.back).setOnClickListener(this);
		areas = new ArrayList<String>();
		for (int i = 0; i < areaNames.length; i++) {
			areas.add(areaNames[i]);
		}
		adapter = new SelectAreaAdapter(this, areas);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:

			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String area = (String) arg0.getItemAtPosition(arg2);
		Intent intent = new Intent();
		intent.putExtra("area", area);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub
		
	}

}
