package com.weather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Telephony.MmsSms.PendingMessages;
import android.text.TextUtils;
import android.util.Log;

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
	/**
	 * �������������ص�Json���ݣ����������������ݴ��浽���ء�
	 * @param context
	 * @param response
	 */
	public static void handleWeatherResponse(Context context,String response){
		try {
			Log.i("main", response);
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesp = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");
			
			Log.i("main", weatherCode);
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * �����������ص�����������Ϣ���浽SharPreference�ļ���
	 */
	private static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy��M��d��",Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("cityName", cityName);
		editor.putString("weatherCode", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
		
	}

}

