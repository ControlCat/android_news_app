package cn.com.scitc.swl.rssnews.fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.com.scitc.swl.rssnews.R;
import cn.com.scitc.swl.rssnews.adapter.NewsListAdapter;
import cn.com.scitc.swl.rssnews.constants.CommonUrl;
import cn.com.scitc.swl.rssnews.model.News;
import cn.com.scitc.swl.rssnews.model.NewsList;
import cn.com.scitc.swl.rssnews.pullrefresh.PullToRefreshBase;
import cn.com.scitc.swl.rssnews.pullrefresh.PullToRefreshBase.OnRefreshListener;
import cn.com.scitc.swl.rssnews.pullrefresh.PullToRefreshListView;
import cn.com.scitc.swl.rssnews.service.HttpUtils;

public class NewsFragment extends Fragment {
	/** 请求页码 */
	private int pageNo = 1;
	/** 下拉刷新 */
	private PullToRefreshListView mPullToRefreshListView;

	private ListView mListView;
	/** 新闻数据 */
	private ArrayList<News> mNewsData = new ArrayList<News>();

	private NewsListAdapter adapter;
	/** 设置新闻列表 */
	public final static int SET_NEWSLIST = 0;
	public final static int GET_FAILED = 1;
	/** 格式时间 */
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("MM-dd HH:mm");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		adapter = new NewsListAdapter(getActivity());
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		@SuppressWarnings("unused")
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.news_fragment, null);
		mPullToRefreshListView = new PullToRefreshListView(getActivity());
		mPullToRefreshListView.setPullLoadEnabled(false);
		mPullToRefreshListView.setScrollLoadEnabled(true);
		mPullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// Thread myThread = new GetNewsThread();
						// myThread.start();

						if (isOnline()) {
							pageNo = 1;
							new GetNewsTask().execute(pageNo);
						} else {
							noNetwork();
						}
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						if (isOnline()) {
							Toast.makeText(getActivity(), "下一页",
									Toast.LENGTH_SHORT).show();
							new GetNewsTask().execute(pageNo);
						} else {
							noNetwork();
						}

					}
				});
		mPullToRefreshListView.doPullRefreshing(true, 500);
		mListView = mPullToRefreshListView.getRefreshableView();
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("newsId", mNewsData.get(position).id);
				intent.putExtra("newsImg", mNewsData.get(position).img);
				intent.setClass(getActivity(), NewsDetailActivity.class);
				startActivity(intent);
			}
		});
		return mPullToRefreshListView;
	}

	/**
	 * 获取新闻的异步任务
	 * 
	 * @author ASHENG
	 * 
	 */
	public class GetNewsTask extends AsyncTask<Integer, Void, ArrayList<News>> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected ArrayList<News> doInBackground(Integer... arg0) {
			// TODO Auto-generated method stub
			String jsonString = HttpUtils.httpPost(CommonUrl.NES_LIST_PATH
					+ "page=" + arg0[0], "utf-8");
			if (jsonString != null && !TextUtils.isEmpty(jsonString)) {
				NewsList newsList = NewsList.parse(jsonString);
				ArrayList<News> news = newsList.newsList;
				return news;
			} else {
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<News> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				if (pageNo == 1) {
					mNewsData = result;
				} else {
					mNewsData.addAll(result);
				}
				adapter.setData(mNewsData);
				System.out.println("page===>" + pageNo);
				if (pageNo == 1) {
					mListView.setAdapter(adapter);
				}
				adapter.notifyDataSetChanged();
				// Toast.makeText(getActivity(), "加载数据成功", Toast.LENGTH_SHORT)
				// .show();
				pageNo++;
			}
			mPullToRefreshListView.onPullDownRefreshComplete();
			mPullToRefreshListView.onPullUpRefreshComplete();
			mPullToRefreshListView.setHasMoreData(true);
			setLastUpdateTime();

		}

	}

	/**
	 * 此方法表示fragment是否可见
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			if (mNewsData == null && mNewsData.size() == 0) {
				Toast.makeText(getActivity(), "数据为空", Toast.LENGTH_SHORT)
						.show();
				pageNo = 1;
				new GetNewsTask().execute(pageNo);
			}
		}
	}

	/*
	 * 获取刷新时间
	 */
	private void setLastUpdateTime() {
		String text = formatDateTime(System.currentTimeMillis());
		mPullToRefreshListView.setLastUpdatedLabel(text);
	}

	private String formatDateTime(long time) {
		if (0 == time) {
			return "";
		}
		return mDateFormat.format(new Date(time));
	}

	/**
	 * Check the Network Connection
	 * 
	 * @return
	 */
	public boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	/**
	 * No Network Connection
	 */
	public void noNetwork() {
		Toast.makeText(getActivity(), "当前无网络", Toast.LENGTH_SHORT).show();
		mPullToRefreshListView.onPullDownRefreshComplete();
		mPullToRefreshListView.onPullUpRefreshComplete();
		mPullToRefreshListView.setHasMoreData(true);
	}
}
