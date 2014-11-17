package cn.com.scitc.swl.rssnews.tools;

import java.util.Date;

public class StringUtils {

	public StringUtils() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * parse rss xml's image_path string
	 * 
	 * @param XMLString
	 * @return
	 */
	public static String getImg(String XMLString) {
		String response = "";
		if (XMLString.contains("<img")) {
			int start = XMLString.indexOf("src=\"");
			int end = XMLString.indexOf("\" ", start);
			response = XMLString.substring(start + 5, end);
		}
		return response;
	}

	public static String getImgName(String imgStr) {
		String response = "";
		if (null != imgStr && !"".equals(imgStr)) {
			int start = imgStr.lastIndexOf("/");
			int end = imgStr.lastIndexOf(".");
			response = imgStr.substring(start + 1, end + 4);
		}

		return response;
	}

	public static String formatDate(Date date) {
		String str = "";
		if (date != null) {
			Date curDate = new Date(System.currentTimeMillis());
			int month = date.getMonth() + 1;
			int day = date.getDate();
			String hour = date.getHours()<10?"0"+date.getHours():date.getHours()+"";
			String minutes = date.getMinutes() < 10 ? "0" + date.getMinutes()
					: date.getMinutes() + "";
			int curMonth = curDate.getMonth() + 1;
			int curDay = curDate.getDate();
			// 今天
			if (month == curMonth && day == curDay) {
				str = "今天   " + hour + ":" + minutes;
			} else if (month == curMonth && curDay - day == 1) {
				str = "昨天   " + hour + ":" + minutes;
			} else if (month == curMonth && curDay - day == 2) {
				str = "前天   " + hour + ":" + minutes;
			} else {
				str = month + " 月  " + day + "日  " + hour + ":" + minutes;
			}
		}
		return str;
	}
}
