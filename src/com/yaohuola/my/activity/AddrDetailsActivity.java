package com.yaohuola.my.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.AddrEntity;
import com.yaohuola.task.HttpTask;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class AddrDetailsActivity extends BaseActivity {

	private static final int SELECT_AREA = 0;
	private TextView tv_title;
	private TextView tv_delete;
	private EditText et_name;
	private EditText et_contact;
	private TextView tv_area;
	private EditText et_addrDetails;
	private ImageView iv_isDefaultAddr;
	private int type;// 0是新增，1是修改
	private AddrEntity addrEntity;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_addr_details);
	}

	@Override
	public void initView() {
		type = getIntent().getIntExtra("type", -1);
		tv_title = (TextView) findViewById(R.id.title);
		tv_delete = (TextView) findViewById(R.id.delete);
		et_name = (EditText) findViewById(R.id.name);
		et_contact = (EditText) findViewById(R.id.contact);
		tv_area = (TextView) findViewById(R.id.area);
		et_addrDetails = (EditText) findViewById(R.id.addrDetails);
		findViewById(R.id.back).setOnClickListener(this);
		iv_isDefaultAddr = (ImageView) findViewById(R.id.defaultAddr);
		iv_isDefaultAddr.setOnClickListener(this);
		findViewById(R.id.saveShippingAddress).setOnClickListener(this);
		findViewById(R.id.ll_area).setOnClickListener(this);
		if (type == 0) {
			tv_title.setText("新增地址");
		} else if (type == 1) {
			addrEntity = (AddrEntity) getIntent().getSerializableExtra("addrEntity");
			tv_title.setText("修改地址");
			tv_delete.setVisibility(View.VISIBLE);
			tv_delete.setOnClickListener(this);
			setData(addrEntity);
		}
	}

	private void setData(AddrEntity addrEntity) {
		et_name.setText(addrEntity.getName());
		et_contact.setText(addrEntity.getPhone());
		tv_area.setText(addrEntity.getArea());
		et_addrDetails.setText(addrEntity.getAddr());
		if (addrEntity.isDefault()) {
			iv_isDefaultAddr.setSelected(true);
		} else {
			iv_isDefaultAddr.setSelected(false);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.defaultAddr:
			if (iv_isDefaultAddr.isSelected()) {
				iv_isDefaultAddr.setSelected(false);
			} else {
				iv_isDefaultAddr.setSelected(true);
			}
			break;
		case R.id.ll_area:
			Intent intent = new Intent(this, SelectAreaActivity.class);
			startActivityForResult(intent, SELECT_AREA);
			break;
		case R.id.saveShippingAddress:
			if (type == 0) {
				saveShippingAddress(HttpTask.POST);
			} else if (type == 1) {
				saveShippingAddress(HttpTask.PUT);
			} else {
				return;
			}
			break;
		case R.id.delete:
			deleteShippingAddress();
		default:
			break;
		}
	}

	private void deleteShippingAddress() {
		if (addrEntity == null) {
			return;
		}
		String token = LocalCache.getInstance(this).getToken();
		if (TextUtils.isEmpty(token)) {
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		map.put("unique_id", addrEntity.getId() + "");
		new HttpTask(this, HttpTask.DELETE, "addresses", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						setResult(RESULT_OK);
						finish();
					} else {
						ToastShow("删除地址失败");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			};
		}.run();
	}

	/**
	 * 添加修改收货地址
	 * 
	 * @param RequestWay
	 *            0是添加1是修改
	 */
	private void saveShippingAddress(final int RequestWay) {
		String name = et_name.getText().toString().trim();
		String contact = et_contact.getText().toString().trim();
		String area = tv_area.getText().toString().trim();
		String addrDetails = et_addrDetails.getText().toString();
		if (TextUtils.isEmpty(name)) {
			ToastShow("请填写收货人姓名");
			return;
		}
		if (TextUtils.isEmpty(contact)) {
			ToastShow("请填写收货人联系方式");
			return;
		}
		if (TextUtils.isEmpty(area)) {
			ToastShow("请填写所在区域");
			return;
		}
		if (TextUtils.isEmpty(addrDetails)) {
			ToastShow("请填写详细地址");
			return;
		}
		String token = LocalCache.getInstance(this).getToken();
		if (TextUtils.isEmpty(token)) {
			return;
		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("token", token);
		if (RequestWay == 2) {
			map.put("unique_id", addrEntity.getId() + "");
		}
		map.put("detail", addrDetails);
		map.put("area", area);
		map.put("receive_name", name);
		map.put("receive_phone", contact);
		map.put("default", "1");
		new HttpTask(this, RequestWay, "addresses", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						setResult(RESULT_OK);
						finish();
					} else {
						if (RequestWay == 2) {
							ToastShow("修改地址失败");
						} else {
							ToastShow("添加地址失败");
						}
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
		case SELECT_AREA:
			String area = data.getStringExtra("area");
			if (TextUtils.isEmpty(area)) {
				return;
			}
			tv_area.setText(area);
			break;
		}
	}

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub
		
	}

}
