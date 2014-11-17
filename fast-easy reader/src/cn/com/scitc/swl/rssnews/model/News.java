package cn.com.scitc.swl.rssnews.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 新闻结构体
 * 
 * @author ASHENG
 * 
 */
public class News {
	/** 资讯id */
	public long id;
	/** 资讯标题 */
	public String title;
	/** 资讯详细内容 */
	public String message;
	/** 标签 */
	public String tag;
	/** 图片 */
	public String img;
	/** 浏览次数 */
	public int count;
	/** 作者 */
	public String author;
	/** 是否焦点新闻 */
	public int focal;
	/** 发布时间 */
	public String time;
	/** 链接地址 */
	public String link;

	public static News parse(String jsonString) {
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			return News.parse(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static News parse(JSONObject jsonObject) {
		if (null == jsonObject) {
			return null;
		}

		News news = new News();
		news.id = jsonObject.optLong("id");
		news.title = jsonObject.optString("title");
		news.message = jsonObject.optString("message");
		news.tag = jsonObject.optString("tag");
		news.img = jsonObject.optString("img");
		news.count = jsonObject.optInt("count");
		news.author = jsonObject.optString("author");
		news.time = jsonObject.optString("time");
		news.focal = jsonObject.optInt("focal");
		return news;
	}

	@Override
	public String toString() {
		return "News [id=" + id + ", title=" + title + ", message=" + message
				+ ", tag=" + tag + ", img=" + img + ", count=" + count
				+ ", author=" + author + ", focal=" + focal + ", time=" + time
				+ "]";
	}

}
