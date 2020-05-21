package com.gnetop.ltgame.core.util;

import android.content.Context;

/**
 * dp和px单位转换工具类
 * 
 * @author heaven
 * @since 2016年11月28日
 */
public class DensityUtils {

	/**
	 * 根据手机的分辨率从dp的单位转成为px(像素)
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int Dp2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从px(像素)的单位 转成为dp
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int Px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param con
	 * @param pxValue
	 * @return
	 */
	public static int Px2sp(Context con, float pxValue) {
		final float scale = con.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param con
	 * @param spValue
	 * @return
	 */
	public static int Sp2px(Context con, float spValue) {
		final float scale = con.getResources().getDisplayMetrics().density;
		return (int) (spValue * scale + 0.5f);
	}

}
