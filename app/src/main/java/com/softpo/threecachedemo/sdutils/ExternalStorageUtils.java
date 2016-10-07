package com.softpo.threecachedemo.sdutils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Environment;

public class ExternalStorageUtils {
	// 1.检查外部存储是否挂载
	public static boolean isSDCardMounted() {
		// 1.获取状态
		String state = Environment.getExternalStorageState();
		// 2.判断是否是挂载的值
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * 将data中的数据存储到sd卡的根目录下
	 * 
	 * @param fileName
	 * @param data
	 * @return
	 */
	public static boolean writeDataToRoot(Context context, String fileName, byte[] data) {
		// 1.判断是否挂载sd卡
		if (isSDCardMounted()) {
			// 2.获取路径
			String parentFile = context.getExternalCacheDir() + File.separator + "MyCache";

			File dir = new File(parentFile);

			if(!dir.exists()){
				dir.mkdirs();
			}

			File file = new File(dir,fileName);

			// 3.写
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file);
				fos.write(data);
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return false;
	}

	// 3.取
	/**
	 * 从sd卡的根部目录获取文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static byte[] readDataFromRoot(Context context,String fileName) {
		if (isSDCardMounted()) {
			String parentFile = context.getExternalCacheDir().getAbsolutePath() + File.separator + "MyCache";
			File file = new File(parentFile, fileName);
			FileInputStream fis = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				fis = new FileInputStream(file);
				byte[] buf = new byte[1024];
				int len = 0;
				while ((len = fis.read(buf)) != -1) {
					baos.write(buf, 0, len);
				}
				return baos.toByteArray();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				try {
					if (fis != null) {
						fis.close();
					}
					if (baos != null) {
						baos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
