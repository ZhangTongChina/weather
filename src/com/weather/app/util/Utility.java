package com.weather.app.util;

import android.text.TextUtils;

import com.weather.app.db.WeatherDB;
import com.weather.app.model.City;
import com.weather.app.model.County;
import com.weather.app.model.Province;

public class Utility {
	/**
	 * 解析和处理服务器返回的省级数据
	 * @param weatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB ,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");//用逗号分开
			if(allProvinces != null && allProvinces.length > 0){
				for (String p : allProvinces){
					String[] array = p.split("\\|");// "|"是转义字符所以用"\\|"
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//将解析出来的数据存储到Province表
					weatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解析和处理服务器返回的市级数据
	 * @param weatherDB
	 * @param response
	 * @param provinceid
	 * @return
	 */
	public synchronized static boolean handleCitiesResponse(WeatherDB weatherDB ,String response,int provinceid){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");//用逗号分开
			if(allCities != null && allCities.length > 0){
				for (String c : allCities){
					String[] array = c.split("\\|");// "|"是转义字符所以用"\\|"
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceid);
					//将解析出来的数据存储到City表
					weatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean handleCountiesResponse(WeatherDB weatherDB ,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");//用逗号分开
			if(allCounties != null && allCounties.length > 0){
				for (String c : allCounties){
					String[] array = c.split("\\|");// "|"是转义字符所以用"\\|"
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityid(cityId);
					//将解析出来的数据存储到City表
					weatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	

}

