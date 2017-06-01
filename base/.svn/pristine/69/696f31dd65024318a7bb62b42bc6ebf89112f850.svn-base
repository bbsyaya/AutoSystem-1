package com.kidney_hospital.base.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;

/**
 * 媒体相关工具
 * 
 * @author Lianxw
 * @time 2016-3-3 下午2:03:57
 **/
public class MediaUtils {

	/**
	 * SD卡状态
	 * 
	 * @return true 可用 false 不可用
	 */
	public static boolean isSdCardMounted() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	/**
	 * 更新系统相册
	 * @param context 应用上下文
	 * @param image 图片文件
	 */
	public static void updateGallery(Context context, File image) {
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(image));
		context.sendBroadcast(intent);
	}
}
