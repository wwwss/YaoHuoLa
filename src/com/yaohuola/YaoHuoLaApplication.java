package com.yaohuola;

import com.library.LibraryApplaction;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.yaohuola.data.cache.LocalCache;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

public class YaoHuoLaApplication extends LibraryApplaction {
	public LocalCache localCache;
	public static YaoHuoLaApplication youHuoLaApplication;

	@Override
	public void onCreate() {
		super.onCreate();
		youHuoLaApplication = this;
		// 初始化本地缓存
	}

	public static void disPlayFromUrl(String imageUri, ImageView imageView, int drawable) {
		if (TextUtils.isEmpty(imageUri)) {
			imageView.setImageResource(drawable);
			return;
		}
		DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).showImageOnLoading(drawable).cacheOnDisk(true) // 设置图片不缓存于内存中
				.showImageForEmptyUri(drawable).resetViewBeforeLoading(true).showImageOnFail(drawable)
				.bitmapConfig(Bitmap.Config.RGB_565) // 设置图片的质量
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT) // 设置图片的缩放类型，该方法可以有效减少内存的占用
				.build();
		ImageLoader.getInstance().displayImage(imageUri, imageView, options);
	}

	/**
	 * 从内存卡中异步加载本地图片
	 * 
	 * @param uri
	 * @param imageView
	 */
	public static void disPlayFromSDCard(String uri, ImageView imageView, int drawable) {
		DisplayImageOptions options = new DisplayImageOptions.Builder().showImageOnLoading(drawable)
				.showImageForEmptyUri(drawable).showImageOnFail(drawable).cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoader.getInstance().displayImage("file://" + uri, imageView, options);
	}

	public static boolean isLogin(Context context) {
		String token = LocalCache.getInstance(context).getToken();
		if (token != null) {
			return true;
		} else {
			return false;
		}
	}
}
