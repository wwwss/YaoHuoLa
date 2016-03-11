package com.yaohuola.data.entity;

import com.library.entity.BaseEntity;

/**
 * 分类实体类
 */
public class ClassifyEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * 分类ID
	 */
	private String id;
	/**
	 * 分类图片
	 */
	private String pic;
	/**
	 * 分类名称
	 */
	private String name;
	/**
	 * 分类资源图片
	 */
	private int drawable;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDrawable() {
		return drawable;
	}

	public void setDrawable(int drawable) {
		this.drawable = drawable;
	}

}
