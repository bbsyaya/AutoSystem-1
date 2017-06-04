package com.kidney_hospital.base.util.exceptioncatch;/**
 * Created by tarena on 2016/12/5.
 */

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * created by Vampire
 * on: 2016/12/5 下午2:10
 */
public class WriteFileUtil {
    private static final String TAG = "WriteFileUtil-vampire";

    //判断SD卡是否存在
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    // 获取SD卡根目录路径
    public static String getSdCardPath() {
        boolean exist = isSdCardExist();
        String sdpath = "";
        if (exist) {
            sdpath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath();
        } else {
            sdpath = "不适用";
        }
        return sdpath;

    }

    // 获取默认路径
    public static String getDefaultFilePath() {
        String filepath = "";
        File file = new File(Environment.getExternalStorageDirectory(),
                "files");
        if (file.exists()) {
            filepath = file.getAbsolutePath();
        } else {
            filepath = "不适用";
        }
        return filepath;
    }

    //使用FileInputStream读取文件
    public void readFileByFileInputStream() {
        try {
            File file = new File(Environment.getExternalStorageDirectory(),
                    "files.txt");
            FileInputStream is = new FileInputStream(file);
            byte[] b = new byte[is.available()];
            is.read(b);
            String result = new String(b);
            System.out.println("读取成功：" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //使用BufferReader读取文件
    public static String readFileByBufferReader(String path) {
        try {
            File file = new File(path);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String readline = "";
            StringBuffer sb = new StringBuffer();
            while ((readline = br.readLine()) != null) {
                System.out.println("readline:" + readline);
                return readline;
            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static File wrieFileByFileOutputStream(String info, String fileName) {
        try {
            File file = new File("storage/emulated/0/Download/" + fileName + ".txt", "UTF-8");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(info.getBytes());
            fos.close();
            Log.d(TAG, "写入成功：");
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "e:" + e);
        }
        return null;
    }

    public static void wrieFileByBufferedWriter(String info, String fileName) {
        try {
//            File file = new File("storage/emulated/0/Download/auto/" + fileName + ".txt");
            File file = new File("storage/emulated/0/Download/auto_log/");
            if (!file.exists()){
                file.mkdirs();
            }
            File fullFile = new File("storage/emulated/0/Download/auto_log/" + fileName + ".txt");
            //第二个参数意义是说是否以append方式添加内容
            BufferedWriter bw = new BufferedWriter(new FileWriter(fullFile, true));
            bw.write(info);
            bw.flush();
            Log.d(TAG, "写入成功");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "e:" + e);
        }
    }


    public static void wrieFileUserIdByBufferedWriter(String info, String fileName) {
        try {
            File fullFile = new File(fileName);
            //第二个参数意义是说是否以append方式添加内容
            BufferedWriter bw = new BufferedWriter(new FileWriter(fullFile, true));
            bw.write(info);
            bw.flush();
            Log.d(TAG, "写入成功");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "e:" + e);
        }
    }



}
