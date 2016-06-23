package cn.homecaught.ibus_android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import cn.homecaught.ibus_android.MyApplication;

public class SharedPreferenceManager {
	public static final String PREFERENCE_FILE = "FILE";

	public static final String SP_NAME = "negotiation";
	public final static String LOGIN_USER_MOBILE = "user_mobile";
	public final static String LOGIN_USER_PASS = "user_pass";


	private SharedPreferences sp;
	private Editor editor;

	public SharedPreferenceManager(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}


	public void setUserMobile(String mobile) {
		editor.putString(LOGIN_USER_MOBILE, mobile);
		editor.commit();
	}

	public String getUserMobile() {
		return getString(LOGIN_USER_MOBILE, "");
	}
	public void setUserPass(String pass) {
		editor.putString(LOGIN_USER_PASS, pass);
		editor.commit();
	}

	public String getUserPass() {
		return getString(LOGIN_USER_PASS, "");
	}



	private Boolean getBoolean(String tag, Boolean defaultValue) {
		if (sp == null) {
			sp = getSharedPreferences(MyApplication.instance);
		}

		return sp.getBoolean(tag, defaultValue);
	}
	public void putString(String tag,String value){
		editor.putString(tag, value);
		editor.commit();
	}
	public String getString(String tag, String defaultValue) {

//	private String getString(String tag, String defaultValue) {
		if (sp == null) {
			sp = getSharedPreferences(MyApplication.instance);
		}

		return sp.getString(tag, defaultValue);
	}
	public Editor getEdit() {
		return editor;
	}

	public static SharedPreferences getSharedPreferences(Context context) {
		return context.getSharedPreferences(SP_NAME, 0);
	}

}
