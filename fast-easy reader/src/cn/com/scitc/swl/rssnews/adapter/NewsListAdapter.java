package cn.com.scitc.swl.rssnews.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.com.scitc.swl.rssnews.R;
import cn.com.scitc.swl.rssnews.constants.CommonUrl;
import cn.com.scitc.swl.rssnews.http.DownloadImg;
import cn.com.scitc.swl.rssnews.http.DownloadImg.ImageCalback;
import cn.com.scitc.swl.rssnews.model.News;
import cn.com.scitc.swl.rssnews.service.FileService;
import cn.com.scitc.swl.rssnews.tools.BitmapCompressTools;
import cn.com.scitc.swl.rssnews.tools.StringUtils;

/**
 * 推荐页面的适配器
 * 
 * @author ASHENG
 * 
 */
public class NewsListAdapter extends BaseAdapter {

	private ArrayList<News> mNewsList;

	private Activity activity;

	public NewsListAdapter(Activity activity) {
		this.activity = activity;
	}

	public void setData(ArrayList<News> mNewsList) {
		this.mNewsList = mNewsList;
	}

	@Override
	public int getCount() {
		return mNewsList.size();
	}

	@Override
	public Object getItem(int position) {
		return mNewsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView,
			ViewGroup viewGroup) {
		if (convertView == null) {
			convertView = LayoutInflater.from(activity).inflate(
					R.layout.news_list_item, null);
		}
		News news = mNewsList.get(position);
		TextView title = (TextView) convertView.findViewById(R.id.item_title);
		TextView time = (TextView) convertView.findViewById(R.id.item_time);
		final ImageView image = (ImageView) convertView
				.findViewById(R.id.item_image);
		title.setText(news.title);
		time.setText(StringUtils.formatDate(new Date(news.time)));
		if (mNewsList.get(position).img != null && !"".equals(news.img)) {
			int start = news.img.lastIndexOf("/");
			final String imageName = news.img.substring(start + 1);
			/** 判断本地缓存中是否存在图片 */
			if ((FileService.readImgFromSdcard(imageName, Context.MODE_PRIVATE,
					"rssCache")) != null) {
				byte[] data = FileService.readImgFromSdcard(imageName,
						Context.MODE_PRIVATE, "rssCache");
				image.setImageBitmap(BitmapCompressTools
						.decodeSampledBitmapFromByte(data, 100, 100));
			} else {
				/** 如果没有图片就开启线程下载 */
				DownloadImg downloadImg = new DownloadImg(
						CommonUrl.NES_BASE_PATH + news.img);
				downloadImg.DownloadImage(new ImageCalback() {

					@Override
					public void getImage(byte[] data) {
						boolean flag = FileService.savaImgToSdcard(imageName,
								Context.MODE_PRIVATE, data, "rssCache");
						if (flag) {
							image.setImageBitmap(BitmapCompressTools
									.decodeSampledBitmapFromByte(data, 100, 100));
						}
					}
				});
			}
		}
		return convertView;
	}
}
