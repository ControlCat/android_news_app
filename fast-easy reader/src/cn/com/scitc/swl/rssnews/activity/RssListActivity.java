package cn.com.scitc.swl.rssnews.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.scitc.swl.rssnews.R;
import cn.com.scitc.swl.rssnews.adapter.RssListAdapter;
import cn.com.scitc.swl.rssnews.constants.CommonUrl;
import cn.com.scitc.swl.rssnews.model.RssNews;
import cn.com.scitc.swl.rssnews.model.RssSource;
import cn.com.scitc.swl.rssnews.service.HttpUtils;
import cn.com.scitc.swl.rssnews.tools.XMLTools;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class RssListActivity extends Activity {

	private PullToRefreshListView mPullToRefreshListView;

	private ListView actualListView;

	private ImageView mImage;

	private TextView mLoad;

	private TextView mTitle;

	private int titleId;

	private List<RssSource> titleData;

	private RssListAdapter adapter;
	/** 新闻数据 */
	private List<RssNews> listNews = new ArrayList<RssNews>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rss_list);
		initData();
		initView();
	}

	private void initData() {
		titleData = getRssSource();
		Intent intent = getIntent();
		titleId = intent.getExtras().getInt("id");
		adapter = new RssListAdapter(this);
	}

	private void initView() {
		mImage = (ImageView) findViewById(R.id.rss_list_logo);
		mImage.setImageResource(titleData.get(titleId).imgId);
		mLoad = (TextView) findViewById(R.id.rss_list_load_text);
		mTitle = (TextView) findViewById(R.id.rss_list_title);
		mTitle.setText(titleData.get(titleId).name);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.rss_refreshview);
		new GetDataTask().execute(titleId);
		mPullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						new GetDataTask().execute(titleId);
					}

				});
		// Add an end-of-list listener
		mPullToRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						Toast.makeText(RssListActivity.this, "加载完成!",
								Toast.LENGTH_SHORT).show();
					}
				});
		actualListView = mPullToRefreshListView.getRefreshableView();
		registerForContextMenu(actualListView);
		actualListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				RssNews rssNews = listNews.get(arg2 - 1);
				Intent intent = new Intent(RssListActivity.this,
						RssDetailActivity.class);
				intent.putExtra("list", rssNews.toJSONString());
				startActivity(intent);
			}
		});
	}

	/**
	 * 获取rss新闻数据
	 */
	private class GetDataTask extends AsyncTask<Integer, Void, List<RssNews>> {

		@Override
		protected void onPreExecute() {
			mLoad.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}

		@Override
		protected List<RssNews> doInBackground(Integer... params) {
			String xmlStr = HttpUtils.httpGet(titleData.get(params[0]).url,
					"utf-8");
			listNews = XMLTools.parseXML(xmlStr);
			return listNews;
		}

		@Override
		protected void onPostExecute(List<RssNews> result) {
			super.onPostExecute(result);
			adapter.setData(listNews);
			mPullToRefreshListView.onRefreshComplete();
			actualListView.setAdapter(adapter);
			mLoad.setVisibility(View.GONE);
		}
	}

	private List<RssSource> getRssSource() {
		List<RssSource> list = new ArrayList<RssSource>();
		RssSource rssSource = new RssSource();
		rssSource.name = "钛媒体";
		rssSource.imgId = R.drawable.taimeiti_logo;
		rssSource.url = CommonUrl.RSS_NEWS_TAIMEITI;
		list.add(rssSource);
		rssSource = new RssSource();
		rssSource.name = "虎嗅网";
		rssSource.imgId = R.drawable.huxiu_logo;
		rssSource.url = CommonUrl.RSS_NEWS_HUXIU;
		list.add(rssSource);
		return list;
	}
}
