package com.yaohuola.data.entity;

import java.util.List;

import com.library.entity.BaseEntity;

/**
 * 订单实体类
 */
public class OrderEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 订单id
	 */
	private String id;
	/**
	 * /** 订单编号
	 */
	private String sn;
	/**
	 * 
	 * 订单创建成功时间
	 */
	private String create_at;
	/**
	 * 
	 * 发货时间
	 */
	private String delivery_time;
	/**
	 * 
	 * 完成时间
	 */
	private String complete_time;
	public String getDelivery_time() {
		return delivery_time;
	}
	public void setDelivery_time(String delivery_time) {
		this.delivery_time = delivery_time;
	}
	public String getComplete_time() {
		return complete_time;
	}
	public void setComplete_time(String complete_time) {
		this.complete_time = complete_time;
	}
	/**
	 * /** 收件人信息
	 */
	private AddrEntity addrEntity;
	/**
	 * /** 订单状态 0未完成 1已完成
	 */
	private int status;
	/**
	 * /** 产品种类数
	 */
	private int productNumber;
	/**
	 * 
	 * 总价
	 */
	private String total;
	private List<ProductEntity> productEntities;
	public AddrEntity getAddrEntity() {
		return addrEntity;
	}
	public void setAddrEntity(AddrEntity addrEntity) {
		this.addrEntity = addrEntity;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public List<ProductEntity> getProductEntities() {
		return productEntities;
	}
	public void setProductEntities(List<ProductEntity> productEntities) {
		this.productEntities = productEntities;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	public String getCreate_at() {
		return create_at;
	}
	public void setCreate_at(String create_at) {
		this.create_at = create_at;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getProductNumber() {
		return productNumber;
	}
	public void setProductNumber(int productNumber) {
		this.productNumber = productNumber;
	}

}
