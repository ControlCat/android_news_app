package cn.com.scitc.swl.rssnews.service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public class ImgService {
	private Context context;

	public ImgService(Context context) {
		this.context = context;
	}

	public boolean saveImg(String fileName, int mode, byte[] data) {
		boolean flag = false;
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = context.openFileOutput(fileName, mode);
			fileOutputStream.write(data, 0, data.length);
			flag = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	public byte[] readImg(String fileName, int mode) {
		byte[] response = null;
		try {
			FileInputStream fileInputStream = context.openFileInput(fileName);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int len = 0;
			byte[] data = new byte[1024];
			while ((len = fileInputStream.read(data)) != -1) {
				outputStream.write(data, 0, len);
			}
			response = outputStream.toByteArray();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}
}
