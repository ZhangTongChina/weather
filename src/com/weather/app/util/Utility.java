package com.weather.app.util;

import android.text.TextUtils;

import com.weather.app.db.WeatherDB;
import com.weather.app.model.City;
import com.weather.app.model.County;
import com.weather.app.model.Province;

public class Utility {
	/**
	 * �����ʹ�����������ص�ʡ������
	 * @param weatherDB
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvincesResponse(WeatherDB weatherDB ,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces = response.split(",");//�ö��ŷֿ�
			if(allProvinces != null && allProvinces.length > 0){
				for (String p : allProvinces){
					String[] array = p.split("\\|");// "|"��ת���ַ�������"\\|"
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//���������������ݴ洢��Province��
					weatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * �����ʹ�����������ص��м�����
	 * @param weatherDB
	 * @param response
	 * @param provinceid
	 * @return
	 */
	public synchronized static boolean handleCitiesResponse(WeatherDB weatherDB ,String response,int provinceid){
		if(!TextUtils.isEmpty(response)){
			String[] allCities = response.split(",");//�ö��ŷֿ�
			if(allCities != null && allCities.length > 0){
				for (String c : allCities){
					String[] array = c.split("\\|");// "|"��ת���ַ�������"\\|"
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceid);
					//���������������ݴ洢��City��
					weatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}
	
	public synchronized static boolean handleCountiesResponse(WeatherDB weatherDB ,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounties = response.split(",");//�ö��ŷֿ�
			if(allCounties != null && allCounties.length > 0){
				for (String c : allCounties){
					String[] array = c.split("\\|");// "|"��ת���ַ�������"\\|"
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityid(cityId);
					//���������������ݴ洢��City��
					weatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}
	

}

