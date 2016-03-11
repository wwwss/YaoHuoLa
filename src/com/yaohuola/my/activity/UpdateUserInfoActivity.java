package com.yaohuola.my.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.library.uitls.AESUitls;
import com.library.uitls.AddImageUitls;
import com.library.uitls.Base64Uitls;
import com.library.uitls.PathUtils;
import com.library.uitls.PictureProcessingUtils;
import com.library.uitls.SmartLog;
import com.library.view.RoundImageView;
import com.yaohuola.YaoHuoLaApplication;
import com.yaohuola.constants.Constants;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.UserEntity;
import com.yaohuola.interfaces.UploadImageListenter;
import com.yaohuola.task.HttpTask;

/**
 * 
 * @author admin 修改个人信息
 */
public class UpdateUserInfoActivity extends BaseActivity implements UploadImageListenter {

	private RoundImageView iv_avatar;// 头像
	private EditText et_nikeName;// 用户昵称
	private TextView tv_phoneNum;// 手机号码
	private UserEntity user;
	private AddImageUitls addImageUitls;
	private String imagePath;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_update_user_info);

	}

	@Override
	public void initView() {
		user = (UserEntity) getIntent().getSerializableExtra("user");
		iv_avatar = (RoundImageView) findViewById(R.id.avatar);
		et_nikeName = (EditText) findViewById(R.id.nikeName);
		tv_phoneNum = (TextView) findViewById(R.id.phoneNum);
		findViewById(R.id.back).setOnClickListener(this);
		findViewById(R.id.completed).setOnClickListener(this);
		iv_avatar.setOnClickListener(this);
		iv_avatar.setOval(true);
		findViewById(R.id.shippingAddressManagement).setOnClickListener(this);
		findViewById(R.id.loginOut).setOnClickListener(this);
		setUserInfo(user);
	}

	private void setUserInfo(UserEntity user) {
		if (user == null) {
			return;
		}
		et_nikeName.setText(user.getName());
		if (!TextUtils.isEmpty(user.getImage())) {
			YaoHuoLaApplication.disPlayFromUrl(user.getImage(), iv_avatar, R.drawable.ic_launcher);
		}
		if (!TextUtils.isEmpty(user.getPhone_num())) {
			tv_phoneNum.setText(new String(AESUitls.decode(user.getPhone_num())));
		}
	}

	@Override
	public void onClick(View v) {
		if (user == null) {
			return;
		}
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.avatar:
			if (addImageUitls == null) {
				addImageUitls = new AddImageUitls(this, this);
			}
			addImageUitls.addImage(Constants.CAMERA, Constants.GALLERY);
			break;
		case R.id.completed:
			completed();
			break;
		case R.id.shippingAddressManagement:
			startActivity(new Intent(this, AddrManagementActivity.class));
			break;
		case R.id.loginOut:
			loginOut();
			break;
		default:
			break;
		}

	}

	/**
	 * 退出当前账号
	 */
	private void loginOut() {
		LocalCache.getInstance(this).clearToken();
		Intent intent = new Intent(this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);

	}

	private void completed() {
		Map<String, String> map = new HashMap<String, String>();
		String token = LocalCache.getInstance(this).getToken();
		if (TextUtils.isEmpty(token)) {
			return;
		}
		map.put("token", token);
		if (TextUtils.isEmpty(et_nikeName.getText().toString().trim())) {
			ToastShow("请输入昵称");
			return;
		}
		if (et_nikeName.getText().toString().trim().equals(user.getName()) && TextUtils.isEmpty(imagePath)) {
			ToastShow("您还没有修改任何信息");
			return;
		}
		map.put("user_name", et_nikeName.getText().toString().trim());
		if (!TextUtils.isEmpty(imagePath)) {
			try {
				map.put("head_portrait", Base64Uitls.encodeBase64File(PictureProcessingUtils.imageDispose(imagePath)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		new HttpTask(this, HttpTask.PUT, "users?", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						Toast.makeText(context, "提交成功", Toast.LENGTH_SHORT).show();
						setResult(RESULT_OK);
						finish();
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
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case Constants.CAMERA:
			this.imagePath = addImageUitls.getImagePath();
			YaoHuoLaApplication.disPlayFromSDCard(addImageUitls.getImagePath(), iv_avatar, R.drawable.ic_launcher);
			break;
		case Constants.GALLERY:
			this.imagePath = PathUtils.getUriPath(data, this);
			SmartLog.i(TAG, imagePath);
			SmartLog.i(TAG, PictureProcessingUtils.imageDispose(imagePath));
			YaoHuoLaApplication.disPlayFromSDCard(imagePath, iv_avatar, R.drawable.ic_launcher);
			break;
		}
	}

	@Override
	public void onUploadSuccess(String result) {
		try {
			JSONObject jsonObject = new JSONObject(result);
			int code = jsonObject.optInt("result", -1);
			if (code == 0) {
				setResult(RESULT_OK);
				finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub

	}

}
