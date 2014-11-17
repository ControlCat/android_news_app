package cn.com.scitc.swl.rssnews.model;

import org.json.JSONException;
import org.json.JSONObject;

public class RssNews {

	/** 标题 */
	public String title;
	/** 链接地址 */
	public String link;
	/** 评论 */
	public String comments;
	/** 描述 */
	public String description;
	/** 来源 */
	public String source;
	/** 日期 */
	public String pubDate;
	/** 图片地址 */
	public String imgUrl;
	/** 图片名称 */
	public String imgName;
	/** 内容 */
	public String content;

	public RssNews() {
	}

	@Override
	public String toString() {
		return "RssNews [title=" + title + ", link=" + link + ", comments="
				+ comments + ", description=" + description + ", source="
				+ source + ", pubDate=" + pubDate + ", imgUrl=" + imgUrl
				+ ", imgName=" + imgName + ", content=" + content + "]";
	}

	public String toJSONString() {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("title", title);
			jsonObject.put("link", link);
			jsonObject.put("comments", comments);
			jsonObject.put("description", description);
			jsonObject.put("source", source);
			jsonObject.put("pubDate", pubDate);
			jsonObject.put("imgUrl", imgUrl);
			jsonObject.put("imgName", imgName);
			jsonObject.put("content", content);
			return jsonObject.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static RssNews parse(String jsonString) {
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			return RssNews.parse(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static RssNews parse(JSONObject jsonObject) {
		if (null == jsonObject) {
			return null;
		}

		RssNews news = new RssNews();
		news.title = jsonObject.optString("title");
		news.link = jsonObject.optString("link");
		news.comments = jsonObject.optString("comments");
		news.description = jsonObject.optString("description");
		news.source = jsonObject.optString("source");
		news.pubDate = jsonObject.optString("pubDate");
		news.imgUrl = jsonObject.optString("imgUrl");
		news.imgName = jsonObject.optString("imgName");
		return news;
	}
}
