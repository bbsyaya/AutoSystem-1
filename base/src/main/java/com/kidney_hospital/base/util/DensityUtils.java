package com.kidney_hospital.base.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * px to dp 及 sp to px 工具类
 * 
 * @author liyakun 创建时间：2015年7月6日
 */
public class DensityUtils {
	private static float DENSITY;
	private static float SCALED_DENSITY;
	//屏幕宽度（像素）
	public static int SCREEN_WIDTH;
	//屏幕高度（像素）
	public static int SCREEN_HEIGHT;
	static {
		DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
		SCREEN_WIDTH = metrics.widthPixels;
		SCREEN_HEIGHT = metrics.heightPixels;
		DENSITY = metrics.density;
		SCALED_DENSITY = metrics.scaledDensity;
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(float pxValue) {
		return (int) (pxValue / SCALED_DENSITY + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(float spValue) {

		return (int) (spValue * SCALED_DENSITY + 0.5f);
	}

	/**
	 * 
	 * @param context
	 * @param mmValue
	 *            1 in 是25.4mm
	 * @return
	 */
	public static int mm2px(Context context, float mmValue) {
		final float xdpi = context.getResources().getDisplayMetrics().xdpi;
		return (int) (mmValue * xdpi * (1.0f / 25.4f));
	}

	/**
	 * 将dp值转换为px值，保证尺寸大小不变
	 * 
	 * @param dp
	 * @return
	 */
	public static int dp2px(float dp) {
		return (int) (DENSITY * dp + 0.5f);
	}

	/**
	 * 将px值转换为dp值，保证尺寸大小不变
	 * 
	 * @param px
	 * @return
	 */
	public static float px2dp(int px) {
		return (int) (px / DENSITY + 0.5f);
	}

}
