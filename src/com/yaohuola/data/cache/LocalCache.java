package com.yaohuola.data.cache;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

/**
 * 功能：本地缓存，保存用户账号，电话等信息<br>
 * 作者：wss<br>
 * 时间：2014年4月2日<br>
 * 版本：1.0<br>
 */
public class LocalCache {

	// 单例模式
	private static LocalCache localCache;
	// 存储对象
	private SharedPreferences sp;
	// log标签
	public final String TAG = "LocalCache";
	private final String ISFIRST = "isFirst";
	private final String TOKEN = "token";
	private final String KEFUTELL = "KeFuTell";
	private final String SENDPRICE = "SendPrice";

	private LocalCache(Context context) {
		sp = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
	}

	/**
	 * 获取本地缓存实例F
	 * 
	 * @param context
	 *            应用上下文
	 * @return 本地缓存
	 */
	public static LocalCache getInstance(Context context) {
		if (localCache == null) {
			localCache = new LocalCache(context);
		}
		return localCache;
	}

	/**
	 * 清空token信息
	 */
	public void clearAll() {
		Editor editor = sp.edit();
		editor.remove(ISFIRST);
		editor.remove(TOKEN);
		editor.remove(KEFUTELL);
		editor.remove(SENDPRICE);
		editor.commit();
	}

	/**
	 * 获取是否第一次登录
	 * 
	 */
	public boolean getIsFirst() {
		return sp.getBoolean(ISFIRST, false);
	}

	/**
	 * 设置第一次登录
	 */
	public void setIsFirst(boolean isFirst) {
		Editor editor = sp.edit();
		editor.putBoolean(ISFIRST, isFirst);
		editor.commit();
	}

	/**
	 * 获取token信息
	 * 
	 */
	public String getToken() {
		return sp.getString(TOKEN, null);
	}

	/**
	 * 保存token信息
	 */
	public void setToken(String token) {
		Editor editor = sp.edit();
		editor.putString(TOKEN, token);
		editor.commit();
	}

	/**
	 * 清空token信息
	 */
	public void clearToken() {
		Editor editor = sp.edit();
		editor.remove(TOKEN);
		editor.commit();
	}

	/**
	 * 获取电话信息
	 * 
	 */
	public String getKeFuTell() {
		return sp.getString(KEFUTELL, null);
	}

	/**
	 * 保存电话信息
	 */
	public void setKeFuTell(String phoneNumber) {
		Editor editor = sp.edit();
		editor.putString(KEFUTELL, phoneNumber);
		editor.commit();
	}

	/**
	 * 获取起送价格
	 * 
	 */
	public Float getSendPrice() {
		return sp.getFloat(SENDPRICE, 0);
	}

	/**
	 * 保存电话信息
	 */
	public void setSendPrice(float sendPrice) {
		Editor editor = sp.edit();
		editor.putFloat(SENDPRICE, sendPrice);
		editor.commit();
	}

	public void setJSONArray(String key, JSONArray jsonArray) {
		Editor editor = sp.edit();
		editor.remove(key);
		editor.putString(key, jsonArray.toString());
		editor.commit();
	}

	public JSONArray getJSONArray(String key) {
		String string = sp.getString(key, null);
		if (TextUtils.isEmpty(string)) {
			return null;
		}
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(string);
			return jsonArray;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonArray;
	}

	public void clear(String key) {
		Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}
	// /**
	// * 获取channelId信息
	// *
	// */
	// public String getChannelId() {
	// return sp.getString("channelId", "0");
	// }
	//
	// /**
	// * 保存channelId信息
	// */
	// public void setChannelId(String channelId) {
	// Editor editor = sp.edit();
	// editor.putString("channelId", channelId);
	// editor.commit();
	// }

	// /**
	// * 获取userId信息
	// *
	// */
	// public String getUserId() {
	// return sp.getString("userId", "0");
	// }
	//
	// /**
	// * 保存userId信息
	// */
	// public void setUserId(String userId) {
	// Editor editor = sp.edit();
	// editor.putString("userId", userId);
	// editor.commit();
	// }
}
