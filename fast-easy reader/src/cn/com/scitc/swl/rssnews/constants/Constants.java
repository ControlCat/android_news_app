package cn.com.scitc.swl.rssnews.constants;

import java.util.ArrayList;
import java.util.List;

import cn.com.scitc.swl.rssnews.R;
import cn.com.scitc.swl.rssnews.model.NewsClassify;
import cn.com.scitc.swl.rssnews.model.RssSource;

public class Constants {
	public static ArrayList<NewsClassify> getData() {
		ArrayList<NewsClassify> newsClassify = new ArrayList<NewsClassify>();
		NewsClassify classify = new NewsClassify();
		classify.setId(3);
		classify.setTitle("订阅");
		newsClassify.add(classify);
		classify = new NewsClassify();
		classify.setId(2);
		classify.setTitle("推荐");
		newsClassify.add(classify);
		classify = new NewsClassify();
		classify.setId(1);
		classify.setTitle("收藏");
		newsClassify.add(classify);
		return newsClassify;
	}

	public static List<RssSource> getRssSource() {
		List<RssSource> list = new ArrayList<RssSource>();
		RssSource rssSource = new RssSource();
		rssSource.id = 0;
		rssSource.name = "钛媒体";
		rssSource.imgId = R.drawable.taimeiti_logo;
		list.add(rssSource);
		RssSource rssSource2 = new RssSource();
		rssSource.id = 1;
		rssSource.name = "虎嗅网";
		rssSource.imgId = R.drawable.huxiu_logo;
		list.add(rssSource2);
		return list;
	}
}
