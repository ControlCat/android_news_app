package cn.com.scitc.swl.rssnews.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * 新闻列表结构体
 * 
 * @author ASHENG
 * 
 */
public class NewsList {
	/** 新闻列表 */
	public ArrayList<News> newsList;
	/** true表示成功 */
	public boolean success;

	public static NewsList parse(String jsonString) {
		if (TextUtils.isEmpty(jsonString)) {
			return null;
		}
		NewsList newsList = new NewsList();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			newsList.success = jsonObject.optBoolean("success");
			if (newsList.success) {
				JSONArray jsonArray = jsonObject.getJSONArray("yi18");
				if (jsonArray != null && jsonArray.length() > 0) {
					newsList.newsList = new ArrayList<News>(jsonArray.length());
					for (int i = 0; i < jsonArray.length(); i++) {
						newsList.newsList.add(News.parse(jsonArray.getJSONObject(i)));
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return newsList;
	}

}
