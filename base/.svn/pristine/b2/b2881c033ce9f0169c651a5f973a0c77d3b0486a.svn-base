package com.kidney_hospital.base.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * @author Lianxw
 * @time 2015-12-15 上午7:22:22
 * @summary
 **/
public class FileUtils {
	public static File saveBitmapToCache(Context context, Bitmap bitmap) throws IOException {
		boolean exist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (!exist) {
			return null;
		}
		File cache = context.getExternalCacheDir();
		File temp = null;
		if (cache != null) {
			String fileName = cache.getAbsolutePath() + File.separator + UUID.randomUUID() + ".jpg";
			temp = new File(fileName);
			OutputStream out = new FileOutputStream(temp);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.close();
		}
		return temp;
	}

	public static String generateFileName(Context context) {
		boolean _sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (_sdCardExist) {
			File mediaStorageDir = new File(
					Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "w1wl");
			if (!mediaStorageDir.exists()) {
				if (!mediaStorageDir.mkdirs()) {
					return null;
				}
			}
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
			return mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
		}
		return null;
	}
}
