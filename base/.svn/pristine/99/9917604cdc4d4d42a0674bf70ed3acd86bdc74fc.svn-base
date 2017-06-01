package com.kidney_hospital.base.util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author : lianxw
 * @time : 2015年12月11日 上午9:31:05
 * @desicription :
 */
public class IOUtils {

	public static String readToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static void saveStringToSDCard(Context context, String name, String content) {
		boolean exist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		if (!exist) {
			return;
		}
		String fileName = Environment.getExternalStorageDirectory() + File.separator + name;
		File file = new File(fileName);
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
