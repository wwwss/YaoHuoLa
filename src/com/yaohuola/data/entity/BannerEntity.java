package com.yaohuola.data.entity;

import com.library.entity.BaseEntity;

/**
 *  轮播图实体类
 */
public class BannerEntity extends BaseEntity{

	
	private static final long serialVersionUID = 1L;
	/**
	 * 链接地址
	 */
	private String page_url;
	
	/**
	 * 图片地址
	 */
	private String image_url;
	
	/**
	 * 专题title
	 */
	private String title;
	
	/**
	 * 
	 */
	private int drawable;
	
	private int id;
	private String product_unique_id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public String getProduct_unique_id() {
		return product_unique_id;
	}

	public void setProduct_unique_id(String product_unique_id) {
		this.product_unique_id = product_unique_id;
	}

	public int getDrawable() {
		return drawable;
	}

	public void setDrawable(int drawable) {
		this.drawable = drawable;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPage_url() {
		return page_url;
	}

	public void setPage_url(String page_url) {
		this.page_url = page_url;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}
}
