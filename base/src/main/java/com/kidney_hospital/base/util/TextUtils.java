package com.kidney_hospital.base.util;
/** 
* @author : lianxw
* 创建时间：2015年12月8日 下午5:36:01 
* 
*/
public class TextUtils {
	public static boolean isNull(String text) {
		return text==null || text.trim().length()==0 || text.equalsIgnoreCase("null");
	}

	public static String addZero(int a) {
		if (a<10){
			String result = "0"+a;
			return result;
		}else{
			return a+"";
		}
	}
}
