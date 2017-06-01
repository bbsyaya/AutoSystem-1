package com.kidney_hospital.base.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;

/**
 * @author Lianxw
 * @time 2015-12-15 上午7:55:05
 * @summary
 **/
public class BitmapUtils {

	/**
	 * 缩放bitmap
	 * @param bitmap
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFile(File bitmap, int reqWidth, int reqHeight) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(bitmap.getAbsolutePath(), options);
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(bitmap.getAbsolutePath(), options);
	}

	/**
	 * 缩放bitmap from uri
	 * @param context
	 * @param uri
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapUri(Context context, Uri uri, int reqWidth, int reqHeight) {
		File file = new File(resolveImageUri(context, uri));
		return decodeSampledBitmapFile(file, reqWidth, reqHeight);
	}

	public static String resolveImageUri(Context context, Uri uri) {
		return UriUtils.getFileAbsolutePath(context, uri);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
}
