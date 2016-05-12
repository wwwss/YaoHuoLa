package com.yaohuola.data.entity;

import com.library.entity.BaseEntity;

/**
 * 收藏宝贝实体类
 */
public class CollectBabyEntity extends BaseEntity {
	private static final long serialVersionUID = 1L;
	
	private String collectBabyId;
	
	private ProductEntity productEntity;
	
	public String getCollectBabyId() {
		return collectBabyId;
	}
	public void setCollectBabyId(String collectBabyId) {
		this.collectBabyId = collectBabyId;
	}
	public ProductEntity getProductEntity() {
		return productEntity;
	}
	public void setProductEntity(ProductEntity productEntity) {
		this.productEntity = productEntity;
	}
	
}
