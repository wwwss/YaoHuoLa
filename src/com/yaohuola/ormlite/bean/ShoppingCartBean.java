package com.yaohuola.ormlite.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 用户表
 */
@DatabaseTable(tableName = "tb_ shopping_cart")  
public class ShoppingCartBean {
	
	/**
	 * 数据库对应ID
	 */
	@DatabaseField(generatedId=true)
	private int id;
	/**
	 * 产品ID
	 */
	@DatabaseField(columnName="productId")
	private int productId;
	/**
	 * 产品名称
	 */
	@DatabaseField(columnName="productName")
	private String productName;
	/**
	 * 产品数量
	 */
	@DatabaseField(columnName="productNumber")
	private int productNumber;
	/**
	 * 是否显示选择按钮
	 */
	@DatabaseField(columnName="isShowSelected")
	private boolean isShowSelected;
	/**
	 * 是否选中
	 */
	@DatabaseField(columnName="isSelected")
	private boolean isSelected;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public int getProductNumber() {
		return productNumber;
	}

	public void setProductNumber(int productNumber) {
		this.productNumber = productNumber;
	}

	public boolean isShowSelected() {
		return isShowSelected;
	}

	public void setShowSelected(boolean isShowSelected) {
		this.isShowSelected = isShowSelected;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

}
