package cn.homecaught.ibus_jhr_wx.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import cn.homecaught.ibus_jhr_wx.MyApplication;

public class SharedPreferenceManager {
	public static final String PREFERENCE_FILE = "FILE";

	public static final String SP_NAME = "negotiation";
	public final static String LOGIN_USER_MOBILE = "user_mobile";
	public final static String LOGIN_USER_PASS = "user_pass";
	public final static String LOGIN_USER_HEAD = "user_head";
	public final static String LOGIN_USER_NAME = "user_name";

	public final static String LOGIN_SCHOOL_NAME = "school_name";
	public final static String LOGIN_SCHOOL_DOMAIN = "school_domain";
	public final static String LOGIN_SCHOOL_ID = "school_id";
	public final static String LOGIN_SCHOOL_LOGO = "school_logo";
	public final static String LOGIN_SCHOOL_Images = "school_images";
	public final static String LOGIN_SCHOOL_REMARK = "school_remark";

	private SharedPreferences sp;
	private Editor editor;

	public SharedPreferenceManager(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	public void setSchoolName(String schoolName) {
		editor.putString(LOGIN_SCHOOL_NAME, schoolName);
		editor.commit();
	}

	public String getSchoolName() {
		return getString(LOGIN_SCHOOL_NAME, "");
	}

	public void setSchoolId(String schoolId) {
		editor.putString(LOGIN_SCHOOL_ID, schoolId);
		editor.commit();
	}
	public String getSchoolId() {
		return getString(LOGIN_SCHOOL_ID, "");
	}


	public void setSchoolRemark(String schoolRemark) {
		editor.putString(LOGIN_SCHOOL_REMARK, schoolRemark);
		editor.commit();
	}
	public String getSchoolRemark() {
		return getString(LOGIN_SCHOOL_REMARK, "");
	}

	public void setSchoolLogo(String schoolLogo) {
		editor.putString(LOGIN_SCHOOL_LOGO, schoolLogo);
		editor.commit();
	}
	public String getSchoolLogo() {
		return getString(LOGIN_SCHOOL_LOGO, "");
	}

	public void setSchoolImages(String schoolImages) {
		editor.putString(LOGIN_SCHOOL_Images, schoolImages);
		editor.commit();
	}
	public String getSchoolImages() {
		return getString(LOGIN_SCHOOL_Images, "");
	}


	public void setSchoolDomain(String schoolDomain) {
		if (schoolDomain == null){
			editor.remove(LOGIN_SCHOOL_DOMAIN);
		}else {
			editor.putString(LOGIN_SCHOOL_DOMAIN, schoolDomain);
			editor.commit();
		}

	}
	public String getSchoolDomain() {
		return getString(LOGIN_SCHOOL_DOMAIN, "www.ibuschina.com");
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

	public void setUserHead(String head) {
		editor.putString(LOGIN_USER_HEAD, head);
		editor.commit();
	}

	public String getUserHead() {
		return getString(LOGIN_USER_HEAD, "");
	}

	public void setUserName(String name) {
		editor.putString(LOGIN_USER_NAME, name);
		editor.commit();
	}

	public String getUserName() {
		return getString(LOGIN_USER_NAME, "");
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
	public void clear(){
		editor.remove(LOGIN_USER_MOBILE);
		editor.remove(LOGIN_USER_PASS);
		editor.remove(LOGIN_USER_HEAD);
		editor.remove(LOGIN_USER_NAME);
		editor.remove(LOGIN_SCHOOL_NAME);
		editor.remove(LOGIN_SCHOOL_DOMAIN);
		editor.remove(LOGIN_SCHOOL_ID);
		editor.remove(LOGIN_SCHOOL_LOGO);
		editor.remove(LOGIN_SCHOOL_Images);
		editor.remove(LOGIN_SCHOOL_REMARK);

		editor.clear();
		editor.commit();
	}

}
