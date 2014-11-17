package cn.com.scitc.swl.rssnews.tools;

import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import cn.com.scitc.swl.rssnews.model.RssNews;

/**
 * 该类主要完成了对XML文件的解析
 * 
 * @author ASHENG
 * 
 */
public class XMLTools {

	public XMLTools() {
		// TODO Auto-generated constructor stub
	}

	public static ArrayList<RssNews> parseXML(String xmlString) {
		ArrayList<RssNews> list = null;
		RssNews rssNews = null;
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(new StringReader(xmlString));
			int evenType = parser.getEventType();
			while (evenType != XmlPullParser.END_DOCUMENT) {
				switch (evenType) {
				case XmlPullParser.START_DOCUMENT:
					list = new ArrayList<RssNews>();
					break;
				case XmlPullParser.START_TAG:
					if ("item".equals(parser.getName())) {
						rssNews = new RssNews();
					}
					if (rssNews != null) {
						if ("title".equals(parser.getName())) {
							rssNews.title = parser.nextText();
						} else if ("link".equals(parser.getName())) {
							rssNews.link = parser.nextText();
						} else if ("pubDate".equals(parser.getName())) {
							rssNews.pubDate = parser.nextText();
						} else if ("source".equals(parser.getName())) {
							rssNews.source = parser.nextText();
						} else if ("description".equals(parser.getName())) {
							rssNews.description = parser.nextText() + "";
							if (rssNews.description.contains("<img")) {
								rssNews.imgUrl = StringUtils
										.getImg(rssNews.description);
								rssNews.imgName = StringUtils
										.getImgName(rssNews.imgUrl);
							}
						} else if ("comments".equals(parser.getName())) {
							rssNews.comments = parser.nextText();
						} else if ("content:encoded".equals(parser.getName())) {
							rssNews.content = parser.nextText() + "";
							if (rssNews.content.contains("<img")) {
								rssNews.imgUrl = StringUtils
										.getImg(rssNews.content);
								rssNews.imgName = StringUtils
										.getImgName(rssNews.imgUrl);
							}
						}
					}
					break;
				case XmlPullParser.END_TAG:
					if ("item".equals(parser.getName())) {
						// rssNews.imgUrl = StringUtils
						// .getImg(rssNews.content.toString());
						list.add(rssNews);
						rssNews = null;
					}
					break;
				}
				evenType = parser.next();
			}
			return list;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}
}
