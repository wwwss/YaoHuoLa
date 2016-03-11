package com.yaohuola.data.entity;

import com.library.entity.BaseEntity;

/**
 * 
 */
public class UserEntity extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 登录令牌
	 */
	private String token;

	/**
	 * 用户ID
	 */
	private String unique_id;
	
	/**
	 * 姓名
	 */
	private String name;
	
	/**
	 * 头像
	 */
	private String image;
	
	/**
	 * 认证状态
	 */
	private String identification;
	
	/**
	 * 手机号码
	 */
	private String phone_num;
	
	/**
	 * 等级
	 */
	private String rand;
	

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUnique_id() {
		return unique_id;
	}

	public void setUnique_id(String unique_id) {
		this.unique_id = unique_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getIdentification() {
		return identification;
	}

	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public String getPhone_num() {
		return phone_num;
	}

	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}

	public String getRand() {
		return rand;
	}

	public void setRand(String rand) {
		this.rand = rand;
	}
}

