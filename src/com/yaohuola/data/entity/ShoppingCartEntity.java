package com.yaohuola.data.entity;

import com.library.entity.BaseEntity;

/**
 * 购物车实体类
 */
public class ShoppingCartEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private String categoryName;

	private String shoppingcartId;

	private ProductEntity productEntity;

	private boolean isSelected;
	
	private boolean selecteIsShow;

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getShoppingcartId() {
		return shoppingcartId;
	}

	public void setShoppingcartId(String shoppingcartId) {
		this.shoppingcartId = shoppingcartId;
	}

	public ProductEntity getProductEntity() {
		return productEntity;
	}

	public void setProductEntity(ProductEntity productEntity) {
		this.productEntity = productEntity;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public boolean isSelecteIsShow() {
		return selecteIsShow;
	}

	public void setSelecteIsShow(boolean selecteIsShow) {
		this.selecteIsShow = selecteIsShow;
	}

}
