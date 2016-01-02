package com.weather.app.db;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import com.weather.app.model.City;
import com.weather.app.model.County;
import com.weather.app.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeatherDB {
	
	/**
	 * 数据库名
	 */
	public static final String DB_NAME = "werther";
	
	/**
	 * 数据库版本
	 */
	public static final int VERSION = 1;
	
	private static WeatherDB weatherDB;
	
	private SQLiteDatabase db;
	
	/**
	 * 构造方法私有化
	 * @param context
	 */
	private WeatherDB(Context context){
		WeatherOpenHelper dbHelper = new WeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	/**
	 * 获取weatherDB的实例。
	 * @param context
	 * @return
	 */
	public synchronized static WeatherDB getInstance(Context context){
		if(weatherDB == null){
			weatherDB = new WeatherDB(context);
		}
		return weatherDB;
	}
	/**
	 * 将Province实例存储到数据库
	 * @param province
	 */
	public void saveProvince(Province province){
		if(province != null){
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	/**
	 * 从数据库读取全国所有省份信息
	 * @return
	 */
	public List<Province> loadProvinces(){
		List<Province> list = new ArrayList<Province>();
		//Cursor游标
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if (cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}while(cursor.moveToNext());
		}
		
		if(cursor != null){
			cursor.close();
		}
		return list;
	}
	
	/**
	 * 将City实例存储到数据库
	 * @param city
	 */
	public void saveCity(City city){
		if(city != null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	/**
	 * 从数据库读取某省下所有的城市信息。
	 */
	public List<City> loadCities(int provinceId){
		List<City> list = new ArrayList<City>();
		//Cursor游标
		Cursor cursor = db.query("City", null, "province_id = ?", new String[] {String.valueOf(provinceId)}, null, null, null);
		if (cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				list.add(city);
			}while(cursor.moveToNext());
		}
		
		if(cursor != null){
			cursor.close();
		}
		return list;
	}
	
	/**
	 * 将City实例存储到数据库
	 * @param city
	 */
	public void saveCounty(County county){
		if(county != null){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id", county.getCityid());
			db.insert("County", null, values);
		}
	}
	
	/**
	 * 从数据库读取某城市下所有的县的信息。
	 */
	public List<County> loadCounties(int cityID){
		List<County> list = new ArrayList<County>();
		//Cursor游标
		Cursor cursor = db.query("County", null, "city_id = ?", new String[] {String.valueOf(cityID)}, null, null, null);
		if (cursor.moveToFirst()){
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityid(cityID);
				list.add(county);
			}while(cursor.moveToNext());
		}
		
		if(cursor != null){
			cursor.close();
		}
		return list;
	}
}
