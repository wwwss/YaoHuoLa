package com.yaohuola.data.entity;

import com.library.entity.BaseEntity;

/**
 * 地址信息实体类
 */
public class AddrEntity extends BaseEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;
	/**
	 * 收货人姓名
	 */
	private String name;
	/**
	 * 收货人手机
	 */
	private String phone;
	/**
	 * 收货人地址
	 */
	private String addr;
	/**
	 * 收货人区域
	 */
	private String area;
	/**
	 * 是否为默认地址
	 */
	private boolean isDefault;
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}


}
