package cn.com.scitc.swl.rssnews.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.scitc.swl.rssnews.R;
import cn.com.scitc.swl.rssnews.activity.RssDetailActivity;
import cn.com.scitc.swl.rssnews.adapter.RssListAdapter;
import cn.com.scitc.swl.rssnews.model.RssNews;
import cn.com.scitc.swl.rssnews.service.FileService;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class CollectFragment extends Fragment {

	private ListView actualListView;

	private PullToRefreshListView mPullToRefreshListView;
	/** 无收藏时的提示 */
	private TextView tishi;
	private ImageView imageView;

	private RssListAdapter adapter;

	/** 新闻数据 */
	private List<RssNews> listNews = new ArrayList<RssNews>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_collect, null);
		tishi = (TextView) view.findViewById(R.id.collect_tv);
		imageView = (ImageView) view.findViewById(R.id.collect_image);
		mPullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.rss_collect_refreshview);
		adapter = new RssListAdapter(getActivity());
		mPullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						new ReadCollectList().execute();
					}

				});

		actualListView = mPullToRefreshListView.getRefreshableView();
		registerForContextMenu(actualListView);
		// item点击事件
		actualListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				RssNews rssNews = listNews.get(arg2 - 1);
				Intent intent = new Intent(getActivity(),
						RssDetailActivity.class);
				intent.putExtra("list", rssNews.toJSONString());
				startActivity(intent);
			}
		});
		// 设置长按事件
		actualListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, final int position, long arg3) {
						// 弹出对话框
						final AlertDialogFragment dialog = new AlertDialogFragment(
								getActivity(), "你确定要取消收藏吗？");
						dialog.show(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								FileService.delFile(
										listNews.get(position - 1).imgName
												+ ".txt", "rssCollect");
								new ReadCollectList().execute();
								dialog.dismiss();
								Toast.makeText(getActivity(), "取消收藏成功！", Toast.LENGTH_SHORT).show();
							}
						});
						return true;
					}
				});
		new ReadCollectList().execute();
		return view;
	}

	class ReadCollectList extends AsyncTask<Void, Void, List<RssNews>> {

		@Override
		protected List<RssNews> doInBackground(Void... v) {
			listNews = FileService.readListFile("rssCollect");
			System.out.println(listNews.toString());
			if (!listNews.toString().equals("[]")) {
				return listNews;
			}
			return null;
		}

		@Override
		protected void onPostExecute(List<RssNews> result) {
			super.onPostExecute(result);
			if (result != null) {
				adapter.setData(result);
				actualListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				tishi.setVisibility(View.GONE);
				imageView.setVisibility(View.GONE);
			} else if (result == null) {
				adapter.removeAll();
				adapter.notifyDataSetChanged();
				tishi.setVisibility(View.VISIBLE);
				imageView.setVisibility(View.VISIBLE);
			}
			mPullToRefreshListView.onRefreshComplete();
		}
	}
}
