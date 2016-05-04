package com.yaohuola.my.fragment;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.yaohuola.R;
import com.library.view.RoundImageView;
import com.yaohuola.YaoHuoLaApplication;
import com.yaohuola.data.cache.LocalCache;
import com.yaohuola.data.entity.UserEntity;
import com.yaohuola.my.activity.AboutUsActivity;
import com.yaohuola.my.activity.CollectBabyActivity;
import com.yaohuola.my.activity.LoginActivity;
import com.yaohuola.my.activity.OrderListActivity;
import com.yaohuola.my.activity.UpdateUserInfoActivity;
import com.yaohuola.task.HttpTask;

public class MyFragMent extends Fragment implements OnClickListener {

	private RoundImageView iv_avatar;// 头像
	private TextView tv_userNikeName;// 用户昵称
	private Context context;
	private UserEntity user;
	private final int UPDATE_USER_INFO = 0;// 修改用户信息
	private String kefutell;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_my, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}

	private void initView() {
		View view = getView();
		context = getActivity();
		iv_avatar = (RoundImageView) view.findViewById(R.id.avatar);
		tv_userNikeName = (TextView) view.findViewById(R.id.userNikeName);
		iv_avatar.setOval(true);
		view.findViewById(R.id.updateUserInfo).setOnClickListener(this);
		view.findViewById(R.id.completedOrders).setOnClickListener(this);
		view.findViewById(R.id.forReceivingOrders).setOnClickListener(this);
		view.findViewById(R.id.collectBaby).setOnClickListener(this);
		view.findViewById(R.id.feedback).setOnClickListener(this);
		view.findViewById(R.id.aboutUs).setOnClickListener(this);
		kefutell = LocalCache.getInstance(context).getKeFuTell();

	}

	@Override
	public void onResume() {
		// 获取个人信息
		getData();
		super.onResume();
	}

	/**
	 * 获取个人信息
	 */
	public void getData() {
		Map<String, String> map = new HashMap<String, String>();
		String token = LocalCache.getInstance(context).getToken();
		if (TextUtils.isEmpty(token)) {
			tv_userNikeName.setText("点击登录");
			iv_avatar.setImageResource(R.drawable.default_avatar_icon);
			return;
		}
		map.put("token", token);
		new HttpTask(context, HttpTask.POST, "v1/users/user_info", map) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						JSONObject jsonObject2 = jsonObject.optJSONObject("user");
						if (jsonObject2 == null) {
							return;
						}
						user = new UserEntity();
						user.setUnique_id(jsonObject2.optString("unique_id", ""));
						user.setName(jsonObject2.optString("name", "点击修改昵称"));
						user.setImage(jsonObject2.optString("image", ""));
						user.setIdentification(jsonObject2.optString("identification", ""));
						user.setPhone_num(jsonObject2.optString("phone_num", ""));
						user.setRand(jsonObject2.optString("rand", ""));
						setUserInfo(user);
					} else {
						Toast.makeText(context, "获取用户信息失败", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			};
		}.run();
	}

	/**
	 * 
	 * @param user
	 */
	protected void setUserInfo(UserEntity user) {
		if (!TextUtils.isEmpty(user.getName())) {
			tv_userNikeName.setText(user.getName());
		}
		if (!TextUtils.isEmpty(user.getImage())) {
			YaoHuoLaApplication.disPlayFromUrl(user.getImage(), iv_avatar, R.drawable.default_avatar_icon);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.updateUserInfo:
			if (!YaoHuoLaApplication.isLogin(context)) {
				intent = new Intent(context, LoginActivity.class);
				context.startActivity(intent);
				return;
			}
			intent = new Intent(context, UpdateUserInfoActivity.class);
			intent.putExtra("user", user);
			startActivityForResult(intent, UPDATE_USER_INFO);
			break;
		case R.id.completedOrders:
			if (!YaoHuoLaApplication.isLogin(context)) {
				intent = new Intent(context, LoginActivity.class);
				context.startActivity(intent);
				return;
			}
			intent = new Intent(context, OrderListActivity.class);
			intent.putExtra("type", 1);
			startActivity(intent);
			break;
		case R.id.forReceivingOrders:
			if (!YaoHuoLaApplication.isLogin(context)) {
				intent = new Intent(context, LoginActivity.class);
				context.startActivity(intent);
				return;
			}
			intent = new Intent(context, OrderListActivity.class);
			intent.putExtra("type", 0);
			startActivity(intent);
			break;
		case R.id.collectBaby:
			if (!YaoHuoLaApplication.isLogin(context)) {
				intent = new Intent(context, LoginActivity.class);
				context.startActivity(intent);
				return;
			}
			intent = new Intent(context, CollectBabyActivity.class);
			intent.putExtra("type", 0);
			startActivity(intent);
			break;
		case R.id.feedback:
			if (!TextUtils.isEmpty(kefutell)) {
				intent = new Intent(Intent.ACTION_DIAL);
				intent.setData(Uri.parse("tel:" + kefutell));
				if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
					startActivity(intent);
				}
			}
			break;
		case R.id.aboutUs:
			intent = new Intent(context, AboutUsActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 如果返回失败，退出
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case UPDATE_USER_INFO:
			getData();
			break;
		}
	}
}
