package com.yaohuola.adapter;

import java.util.List;

import android.content.Context;

public abstract class BaseAdapter <E> extends android.widget.BaseAdapter {

	// 应用上下文
	protected Context context;
	// 数据集合
	protected List<E> list;

	public BaseAdapter(Context context, List<E> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

}
