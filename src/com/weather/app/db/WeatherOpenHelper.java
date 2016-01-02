package com.weather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherOpenHelper extends SQLiteOpenHelper {
	
	/**
	 * Province�������
	 */
	public static final String CREAT_PROVINCE ="create table Province ("
					+"id integer primary key autoincrement,"
					+"province_name text,"
					+" province_code text)"	;	
	/**
	 * City�������
	 */
	public static final String CREAT_CITY ="create table City ("
					+"id integer primary key autoincrement,"
					+"city_name text,"
					+"city_code text,"
					+"province_id integer)";
	/**
	 * County�������
	 */
	public static final String CREAT_COUNTY ="create table County ("
					+"id integer primary key autoincrement,"
					+"county_name text,,"
					+"county_code text,"
					+"city_id integer)";
															
	public WeatherOpenHelper(Context context, String name,CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREAT_PROVINCE);//����Province��
		db.execSQL(CREAT_CITY);//����City��
		db.execSQL(CREAT_COUNTY);//����County
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
