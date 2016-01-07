package com.weather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.weather.app.activity.ChooseAreaActivity;
import com.weather.app.activity.WeatherActivity;
import com.weather.app.db.WeatherDB;
import com.weather.app.model.City;
import com.weather.app.model.County;
import com.weather.app.model.Province;
import com.weather.app.util.HttpCallbackListener;
import com.weather.app.util.HttpUtil;
import com.weather.app.util.Utility;

//import android.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.DownloadManager.Query;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.weather.app.R;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTY = 2;

	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private WeatherDB weatherDB;
	private List<String> dataList = new ArrayList<String>();
	/**
	 * 省列表
	 */
	private List<Province> provinceList;
	
	/**
	 * 市列表
	 */
	private List<City> cityList;
	
	/**
	 * 县列表
	 */
	private List<County> countyList;
	
	/**
	 * 选中的省份
	 */
	private Province selectedPeovince;
	
	/**
	 * 选中的城市
	 */
	private City selectedCity;
	
	/**
	 * 当前选中的级别
	 */
	private int currenLevel;
	/**
	 * 是否从WeatherActivity中跳转过来
	 */
	private boolean isFromWeatherActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
			Intent intent = new Intent(this, WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		listView = (ListView) findViewById(R.id.list_view);
		titleText = (TextView) findViewById(R.id.title_text);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
		listView.setAdapter(adapter);
		weatherDB = WeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int index, long id) {
				if(currenLevel == LEVEL_PROVINCE){
					selectedPeovince = provinceList.get(index);
					queryCities();
				}else if(currenLevel == LEVEL_CITY){
					selectedCity = cityList.get(index);
					queryCounties();
				}else if (currenLevel == LEVEL_COUNTY) {
					String countyCode = countyList.get(index).getCountyCode();
					Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
					intent.putExtra("county_code", countyCode);
					startActivity(intent);
					finish();
				}
			}
			
		});
		queryProvince();//加载省级资源
	}
	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询到在去服务器上查询
	 */
	private void queryProvince() {
		provinceList = weatherDB.loadProvinces();
		if(provinceList.size() > 0){
			dataList.clear();
			for(Province province : provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();//适配器改变时刷新每一个Item
			listView.setSelection(0);
			titleText.setText("中国");
			currenLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province");
		}
	}
	/**
	 * 查询全国所有的市，优先从数据库查询，如果没有查询到在去服务器上查询
	 */
	private void queryCities() {
		cityList = weatherDB.loadCities(selectedPeovince.getId());
		if(cityList.size() > 0){
			dataList.clear();
			for(City city : cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();//适配器改变时刷新每一个Item
			listView.setSelection(0);
			titleText.setText(selectedPeovince.getProvinceName());
			currenLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedPeovince.getProvinceCode(),"city");
		}
	}
	/**
	 * 查询全国所有的县，优先从数据库查询，如果没有查询到在去服务器上查询
	 */
	private void queryCounties() {
		countyList = weatherDB.loadCounties(selectedCity.getId());
		if(countyList.size() > 0){
			dataList.clear();
			for(County county : countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();//适配器改变时刷新每一个Item
			listView.setSelection(0);
			titleText.setText(selectedPeovince.getProvinceName());
			currenLevel = LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(),"county");
		}
	}
	/**
	 * 根据传入的code和类型从服务器上查询省市县级书数据。
	 * @param code
	 * @param string
	 */
	private void queryFromServer(final String code,final String type) {
		String address;
		if(!TextUtils.isEmpty(code)){
			address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
		}else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpResquest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				boolean resule = false;
				if ("province".equals(type)){
					resule = Utility.handleProvincesResponse(weatherDB, response);
				}else if ("city".equals(type)){
					resule = Utility.handleCitiesResponse(weatherDB, response, selectedPeovince.getId());
				}else if ("county".equals(type)){
					resule = Utility.handleCountiesResponse(weatherDB, response, selectedCity.getId());
				}
				
				if(resule){
					//通过runOnUiThread方法回到主线程处理逻辑
					runOnUiThread(new Runnable() {
						public void run() {
							closeProgressDialog();
							if("province".equals(type)){
								queryProvince();
							}else if ("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				//通过runOnUiThraed()方法回到主线程处理逻辑
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	
	/**
	 * 打开对话框
	 */
	private void showProgressDialog() {
		if(progressDialog == null){
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	/**
	 * 关闭对话框
	 */
	private void closeProgressDialog() {
		if(progressDialog != null){
			progressDialog.dismiss();
		}
	}
	/**
	 * 捕获back按键，根据当前的级别来判断，此时应该返回市列表，省列表，还是直接退出。
	 */
	@Override
	public void onBackPressed() {
		if (currenLevel == LEVEL_COUNTY){
			queryCities();
		}else if(currenLevel == LEVEL_CITY){
			queryProvince();
		}else {
			if (isFromWeatherActivity) {
				Intent intent = new Intent(this, WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		} 
	}
	
}
