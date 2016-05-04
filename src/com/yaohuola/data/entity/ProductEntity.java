package com.yaohuola.data.entity;

import org.json.JSONArray;

import com.library.entity.BaseEntity;

/**
 * 产品实体类
 */
public class ProductEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private String id;

	private String pic;

	private String name;

	private String description;

	private double price;

	private int number = 1;

	/**
	 * 商品规格
	 */
	private String spec;

	/**
	 * 产地
	 */
	private String origin;

	/**
	 * 图片列表
	 */
	private JSONArray pics;

	/**
	 * 单位
	 */
	private String unit;

	/**
	 * 供货量
	 */
	private int stock_num;

	/**
	 * 销量
	 */
	private String sales;

	/**
	 * 产品详情
	 */
	private String info;
	
	/**
	 * 0是已收藏，1是未收藏
	 */
	private int favorites;
	
	/**
	 * 收藏ID
	 */
	private String favorite_unique_id;
	
	private String unit_price;


	public String getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(String unit_price) {
		this.unit_price = unit_price;
	}

	public int getFavorites() {
		return favorites;
	}

	public void setFavorites(int favorites) {
		this.favorites = favorites;
	}

	public String getFavorite_unique_id() {
		return favorite_unique_id;
	}

	public void setFavorite_unique_id(String favorite_unique_id) {
		this.favorite_unique_id = favorite_unique_id;
	}

	public String getSales() {
		return sales;
	}

	public void setSales(String sales) {
		this.sales = sales;
	}

	public int getStock_num() {
		return stock_num;
	}

	public void setStock_num(int stock_num) {
		this.stock_num = stock_num;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public JSONArray getPics() {
		return pics;
	}

	public void setPics(JSONArray pics) {
		this.pics = pics;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	

}
