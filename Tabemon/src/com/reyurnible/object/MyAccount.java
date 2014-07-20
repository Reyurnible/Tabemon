package com.reyurnible.object;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class MyAccount {
	private final static String sharedpref_key="account_pref";
	//key text is object name
	private final static String sp_sex = "sex";
	private final static String sp_age= "age";
	/*
	 * 0:Male
	 * 1:Female
	 */
	public int sex;
	/*
	 * 0:10~20
	 * 1:20~30
	 * 2:30~40
	 * 3:40~50
	 * 4:50~60
	 */
	public int age;
	
	private MyAccount(int sex,int age){
		this.sex = sex;
		this.age = age;
	}
	//save sharedpreference
	public static void saveAccount(Context context,int sex,int age){
		SharedPreferences pref = context.getSharedPreferences(sharedpref_key,Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(sp_sex, sex);
		editor.putInt(sp_age, age);
		editor.commit();
		editor.clear();
	}
	//get instance
	public static MyAccount getAccount(Context context){
		SharedPreferences pref = context.getSharedPreferences(sharedpref_key,Activity.MODE_PRIVATE);
		return new MyAccount(pref.getInt(sp_sex,-1),pref.getInt(sp_age, -1));
	}
}
