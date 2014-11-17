package cn.com.scitc.swl.rssnews.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

public class DownloadImg {
	private String image_path;

	public DownloadImg(String image_path) {
		this.image_path = image_path;
	}

	@SuppressLint("HandlerLeak")
	public void DownloadImage(final ImageCalback imageCalback) {
		final Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				byte[] data = (byte[]) msg.obj;
				imageCalback.getImage(data);
			}
		};
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpPost = new HttpGet(image_path);
					HttpResponse response = httpClient.execute(httpPost);
					HttpEntity entity = response.getEntity();
					byte[] data = EntityUtils.toByteArray(entity);
					// BitmapFactory.Options opts = new BitmapFactory.Options();
					// opts.inSampleSize=2;
					// opts.inJustDecodeBounds = true;
					// Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
					// data.length);
					Message msg = handler.obtainMessage();
					msg.obj = data;
					handler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
	}

	@SuppressLint("HandlerLeak")
	public void DownloadImagePOST(final ImageCalback imageCalback) {
		final Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				byte[] data = (byte[]) msg.obj;
				imageCalback.getImage(data);
			}
		};
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					HttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(image_path);
					HttpResponse response = httpClient.execute(httpPost);
					HttpEntity entity = response.getEntity();
					byte[] data = EntityUtils.toByteArray(entity);
					// BitmapFactory.Options opts = new BitmapFactory.Options();
					// opts.inSampleSize=2;
					// opts.inJustDecodeBounds = true;
					// Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
					// data.length);
					Message msg = handler.obtainMessage();
					msg.obj = data;
					handler.sendMessage(msg);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
	}

	public interface ImageCalback {
		public void getImage(byte[] data);
	}
}
