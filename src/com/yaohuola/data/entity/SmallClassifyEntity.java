package com.yaohuola.data.entity;

import java.util.List;

/**
 * 小分类实体类
 */
public class SmallClassifyEntity extends ClassifyEntity {
	private static final long serialVersionUID = 1L;
	
	
	private String title;

	private List<ProductEntity> productEntities;
	
	private int total_pages;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public List<ProductEntity> getProductEntities() {
		return productEntities;
	}

	public void setProductEntities(List<ProductEntity> productEntities) {
		this.productEntities = productEntities;
	}

	public int getTotal_pages() {
		return total_pages;
	}

	public void setTotal_pages(int total_pages) {
		this.total_pages = total_pages;
	}
}
