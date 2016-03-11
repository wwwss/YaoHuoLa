package com.yaohuola.classification.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.yaohuola.R;
import com.library.activity.BaseActivity;
import com.library.uitls.ListViewUitls;
import com.yaohuola.YaoHuoLaApplication;
import com.yaohuola.classification.adapter.ProductImageAdapter;
import com.yaohuola.data.entity.ProductEntity;
import com.yaohuola.task.AddToCartTask;
import com.yaohuola.task.HttpTask;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author admin 产品详情页面
 */
public class ProductDetailsActivity extends BaseActivity {

	private ImageView iv_pic;
	private TextView tv_producteDscription;// 产品描述
	private TextView tv_priceAndunit;// 单价和单位
	private TextView tv_output;// 供货量
	private TextView tv_packingWay;// 包装方式
	private TextView tv_suttle;// 净重
	private TextView tv_shelfLife;// 保质期
	private TextView tv_info;// 产品介绍
	private ListView listView;// 图片列表
	private ProductImageAdapter adapter;
	private String unique_id;// 产品ID
	private TextView tv_nowAddShoppingCart;// 立即加入购物车

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_product_details);

	}

	@Override
	public void initView() {
		unique_id = getIntent().getStringExtra("id");
		if (TextUtils.isEmpty(unique_id)) {
			return;
		}
		iv_pic = (ImageView) findViewById(R.id.pic);
		tv_producteDscription = (TextView) findViewById(R.id.producteDscription);
		tv_priceAndunit = (TextView) findViewById(R.id.priceAndunit);
		tv_output = (TextView) findViewById(R.id.output);
		tv_info = (TextView) findViewById(R.id.productInfo);
		tv_packingWay = (TextView) findViewById(R.id.packingWay);
		tv_suttle = (TextView) findViewById(R.id.suttle);
		tv_shelfLife = (TextView) findViewById(R.id.shelfLife);
		listView = (ListView) findViewById(R.id.listView);
		findViewById(R.id.back).setOnClickListener(this);
		tv_nowAddShoppingCart = (TextView) findViewById(R.id.nowAddShoppingCart);
		tv_nowAddShoppingCart.setOnClickListener(this);
		getData();
	}

	private void getData() {
		new HttpTask(this, HttpTask.GET, "products/" + unique_id, null) {
			protected void onPostExecute(String result) {
				if (TextUtils.isEmpty(result)) {
					return;
				}
				try {
					JSONObject jsonObject = new JSONObject(result);
					int code = jsonObject.optInt("result", -1);
					if (code == 0) {
						JSONObject jsonObject2 = jsonObject.optJSONObject("product");
						if (jsonObject2 == null) {
							return;
						}
						ProductEntity productEntity = new ProductEntity();
						productEntity.setId(jsonObject2.optString("unique_id", ""));
						productEntity.setName(jsonObject2.optString("name", ""));
						productEntity.setDescription(jsonObject2.optString("desc", ""));
						productEntity.setPrice(jsonObject2.optDouble("price", 0));
						productEntity.setSpec(jsonObject2.optString("spec", ""));
						productEntity.setOrigin(jsonObject2.optString("origin", ""));
						productEntity.setUnit(jsonObject2.optString("unit", ""));
						productEntity.setStock_num(jsonObject2.optInt("stock_num", 0));
						productEntity.setSales(jsonObject2.optString("sale_count", ""));
						productEntity.setInfo(jsonObject2.optString("info", ""));
						String images = jsonObject2.optString("images", "");
						if (!TextUtils.isEmpty(images)) {
							JSONArray jsonArray = new JSONArray(images);
							productEntity.setPics(jsonArray);
						}
						setData(productEntity);
					} else {

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}.run();

	}

	private void setData(ProductEntity productEntity) throws JSONException {
		findViewById(R.id.scrollView).setVisibility(View.VISIBLE);
		tv_nowAddShoppingCart.setVisibility(View.VISIBLE);
		tv_producteDscription.setText(productEntity.getName() + " " + productEntity.getDescription());
		tv_priceAndunit.setText("¥" + productEntity.getPrice() + "（" + productEntity.getUnit() + "）");
		tv_output.setText("供货量：" + productEntity.getStock_num());
		tv_packingWay.setText("销量：" + productEntity.getSales());
		tv_suttle.setText("规格：" + productEntity.getSpec());
		tv_shelfLife.setText("产地：" + productEntity.getOrigin());
		if (productEntity.getStock_num() > 0) {
			tv_nowAddShoppingCart.setBackgroundColor(Color.parseColor("#ff3434"));
			tv_nowAddShoppingCart.setClickable(true);
		} else {
			tv_nowAddShoppingCart.setBackgroundColor(Color.parseColor("#eeeeee"));
			tv_nowAddShoppingCart.setClickable(false);
		}
		if (!TextUtils.isEmpty(productEntity.getInfo())) {
			findViewById(R.id.graphicIntroduction).setVisibility(View.VISIBLE);
			tv_info.setVisibility(View.VISIBLE);
			tv_info.setText(productEntity.getInfo());
		}
		if (productEntity.getPics() != null && productEntity.getPics().length() > 0) {
			YaoHuoLaApplication.disPlayFromUrl(productEntity.getPics().getString(0), iv_pic,
					R.drawable.default_banner_icon);
			List<String> list = new ArrayList<String>();
			for (int i = 1; i < productEntity.getPics().length(); i++) {
				list.add(productEntity.getPics().getString(i));
			}
			if (list.size() > 0) {
				adapter = new ProductImageAdapter(this, list);
				listView.setAdapter(adapter);
				listView.setVisibility(View.VISIBLE);
				ListViewUitls.setListViewHeightBasedOnChildren(listView);
			}

		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			finish();
			break;
		case R.id.nowAddShoppingCart:
			AddToCartTask.addToCart(this, unique_id);
			break;
		default:
			break;
		}

	}

	@Override
	public void refreshData() {
		// TODO Auto-generated method stub

	}
}
