package cn.com.scitc.swl.rssnews.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.scitc.swl.rssnews.model.RssNews;

import android.content.Context;
import android.os.Environment;

public class FileService {

	/**
	 * 保存文件到sdcard
	 * 
	 * @param fileName
	 * @param mode
	 * @param data
	 * @param dirName
	 * @return
	 */
	public static boolean savaImgToSdcard(String fileName, int mode,
			byte[] data, String dirName) {
		autoDel(dirName);
		boolean flag = false;
		/** 获得当前sdcard的状态 */
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			FileOutputStream fileOutputStream = null;
			try {
				File dir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/" + dirName);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				fileOutputStream = new FileOutputStream(new File(dir, fileName));
				fileOutputStream.write(data, 0, data.length);
				flag = true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return flag;
	}

	/**
	 * 从sdcard上读取文件
	 * 
	 * @param fileName
	 * @param mode
	 * @param dirName
	 * @return
	 */
	public static byte[] readImgFromSdcard(String fileName, int mode,
			String dirName) {
		byte[] response = null;
		/** 文件存放的目录 */
		File dir = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/" + dirName);
		File img = new File(dir, fileName);
		FileInputStream fileInputStream = null;
		if (img.exists()) {
			try {
				fileInputStream = new FileInputStream(img);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				int len = 0;
				byte[] data = new byte[1024];
				while ((len = fileInputStream.read(data)) != -1) {
					outputStream.write(data, 0, len);
				}
				response = outputStream.toByteArray();
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return response;
	}

	/**
	 * 读取一个文件夹中的所有文件
	 * 
	 * @param dirName文件名
	 */
	public static List<RssNews> readListFile(String dirName) {
		List<RssNews> list = new ArrayList<RssNews>();
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				File dir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/" + dirName);
				if (dir.exists()) {
					File[] childFiles = dir.listFiles();
					for (int i = 0; i < childFiles.length; i++) {
						String fileName = childFiles[i].getName();
						byte[] data = readImgFromSdcard(fileName,
								Context.MODE_PRIVATE, dirName);
						RssNews news = RssNews.parse(new String(data));
						list.add(news);
						System.out.println(news.toString());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 删除文件夹
	 * 
	 * @param dirName
	 * @return
	 */
	public static void delDir(String dirName) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				File dir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/" + dirName);
				if (dir.exists()) {
					File[] childFiles = dir.listFiles();
					if (childFiles == null || childFiles.length == 0) {
						dir.delete();
					}
					for (int i = 0; i < childFiles.length; i++) {
						childFiles[i].delete();
					}
					dir.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 删除文件
	 * 
	 * @param fileName
	 */
	public static void delFile(String fileName, String dirName) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				File file = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/" + dirName + "/" + fileName);
				if (file.isFile()) {
					file.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 自动删除
	 * 
	 * @param dirName
	 */
	public static void autoDel(String dirName) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				File dir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/" + dirName);
				if (dir.listFiles().length > 30) {
					delDir(dirName);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
