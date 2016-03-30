package com.yaohuola.constants;

public interface UrlConstants {
	// /**
	// * 公网服务器地址
	// */
	// String SERVER = "http://103.21.117.21/";

	 /**
	 * 公网服务器地址
	 */
	 String SERVER = "http://yaohuo.la/";

//	/**
//	 * 测试服务器地址
//	 */
//	String SERVER = "http://192.168.0.104:3000/";

	/**
	 * 公网服务器接口地址
	 */
	String SERVERAPI = SERVER + "api/v1/";

	/**
	 * 上传图片
	 */
	String UPLOADPICTURES = SERVERAPI + "users?";

	/**
	 * 服务协议地址
	 */
	String AGREEMENT_ADDRESS = "http://103.21.117.21/service_agreement";

}
