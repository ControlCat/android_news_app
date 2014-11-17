package cn.com.scitc.swl.rssnews.activity;

import java.util.ArrayList;
import java.util.Date;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.scitc.swl.rssnews.R;
import cn.com.scitc.swl.rssnews.adapter.NewsFragmentPagerAdapter;
import cn.com.scitc.swl.rssnews.constants.CommonUrl;
import cn.com.scitc.swl.rssnews.constants.Constants;
import cn.com.scitc.swl.rssnews.customview.ColumnHorizontalScrollView;
import cn.com.scitc.swl.rssnews.fragment.CollectFragment;
import cn.com.scitc.swl.rssnews.fragment.NewsFragment;
import cn.com.scitc.swl.rssnews.fragment.SubscibeFragment;
import cn.com.scitc.swl.rssnews.model.NewsClassify;
import cn.com.scitc.swl.rssnews.model.RssNews;
import cn.com.scitc.swl.rssnews.service.HttpUtils;
import cn.com.scitc.swl.rssnews.slidemenu.CustomSlideMenu;
import cn.com.scitc.swl.rssnews.tools.BaseTools;
import cn.com.scitc.swl.rssnews.tools.XMLTools;

public class MainActivity extends FragmentActivity implements
		View.OnClickListener {

	private ColumnHorizontalScrollView mColumnHorizontalScrollView;
	LinearLayout mRadioGroup_content;
	LinearLayout ll_more_columns;
	RelativeLayout rl_column;
	private ViewPager mViewPager;
	private SlidingMenu slidingMenu;

	/** 顶部按钮 */
	private Button mProfile;

	private ArrayList<NewsClassify> newsClassify = new ArrayList<NewsClassify>();

	private int columnSelectIndex = 0;

	public ImageView shade_left;

	public ImageView shade_right;

	private int mScreenWidth = 0;

	private int mItemWidth = 0;

	private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	// 退出键处理
	private long TIME_DIFF = 2 * 1000;
	private long mLastBackTime = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		slidingMenu = CustomSlideMenu.initSlidingMenu(this);
		mScreenWidth = BaseTools.getWindowsWidth(this);
		mItemWidth = mScreenWidth / 7;//
		initView();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				String xmlStr = HttpUtils.httpGet(CommonUrl.RSS_NEWS_TAIMEITI,
						"UTF-8");
				// System.out.println("XML" + xmlStr);
				ArrayList<RssNews> list = XMLTools.parseXML(xmlStr);
				for (int i = 0; i < list.size(); i++) {
					System.out.println("==title==>" + list.get(i).title);
					// System.out.println(list.get(i).link);
					// System.out.println("==description==>"
					// + list.get(i).description);
					// System.out.println("==content==>" + list.get(i).content);
					// System.out.println(list.get(i).pubDate);
					if (list.get(i).imgUrl != null
							&& !"".equals(list.get(i).imgUrl)) {
						System.out.println("==imgUrl==>" + list.get(i).imgUrl);
						System.out.println("==imgName==>" + list.get(i).imgName);
					}
				}
				// List<RssNews> list1 = list.subList(0, list.size() / 2);
				// List<RssNews> list2 = list.subList(list.size() / 2,
				// list.size());
				// System.out.println(list.toString());
			}
		});
	}

	private void initView() {
		mColumnHorizontalScrollView = (ColumnHorizontalScrollView) findViewById(R.id.mColumnHorizontalScrollView);
		mRadioGroup_content = (LinearLayout) findViewById(R.id.mRadioGroup_content);
		mViewPager = (ViewPager) findViewById(R.id.mViewPager);
		mProfile = (Button) findViewById(R.id.top_profile);
		mProfile.setOnClickListener(this);
		setChangelView();
	}

	private void setChangelView() {
		initColumnData();
		initTabColumn();
		initFragment();
	}

	private void initColumnData() {
		newsClassify = Constants.getData();
	}

	/**
	 * */
	private void initTabColumn() {
		mRadioGroup_content.removeAllViews();
		int count = newsClassify.size();
		mColumnHorizontalScrollView.setParam(this, mScreenWidth,
				mRadioGroup_content, ll_more_columns, rl_column);
		for (int i = 0; i < count; i++) {
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					mItemWidth, LayoutParams.WRAP_CONTENT);
			params.leftMargin = 15;
			params.rightMargin = 20;
			TextView localTextView = new TextView(this);
			localTextView.setTextAppearance(this,
					R.style.top_category_scroll_view_item_text);
			// localTextView.setBackground(R.drawable.radio_text_sel);
			localTextView.setGravity(Gravity.CENTER);
			localTextView.setPadding(5, 0, 5, 0);
			localTextView.setId(i);
			localTextView.setText(newsClassify.get(i).getTitle());
			localTextView.setTextColor(getResources().getColorStateList(
					R.color.top_category_scroll_text_color_day));
			if (columnSelectIndex == i) {
				localTextView.setSelected(true);
			}
			localTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
						View localView = mRadioGroup_content.getChildAt(i);
						if (localView != v)
							localView.setSelected(false);
						else {
							localView.setSelected(true);
							mViewPager.setCurrentItem(i);
						}
					}
				}
			});
			mRadioGroup_content.addView(localTextView, i, params);
		}
	}

	/**
	 * */
	private void selectTab(int tab_postion) {
		columnSelectIndex = tab_postion;
		for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
			View checkView = mRadioGroup_content.getChildAt(tab_postion);
			int k = checkView.getMeasuredWidth();
			int l = checkView.getLeft();
			int i2 = l + k / 2 - mScreenWidth / 2;
			mColumnHorizontalScrollView.smoothScrollTo(i2, 0);
		}
		for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
			View checkView = mRadioGroup_content.getChildAt(j);
			boolean ischeck;
			if (j == tab_postion) {
				ischeck = true;
			} else {
				ischeck = false;
			}
			checkView.setSelected(ischeck);
		}
	}

	/**
	 * 初始化fragment
	 * */
	private void initFragment() {
		Bundle data = new Bundle();
		data.putString("text", "订阅");
		SubscibeFragment subscibeFragment = new SubscibeFragment();
		subscibeFragment.setArguments(data);
		fragments.add(subscibeFragment);

		Bundle data2 = new Bundle();
		data.putString("text", "推荐");
		NewsFragment newfragment = new NewsFragment();
		subscibeFragment.setArguments(data2);
		fragments.add(newfragment);

		Bundle data3 = new Bundle();
		data3.putString("text", "收藏");
		CollectFragment collectFragment = new CollectFragment();
		subscibeFragment.setArguments(data3);
		fragments.add(collectFragment);

		NewsFragmentPagerAdapter mAdapetr = new NewsFragmentPagerAdapter(
				getSupportFragmentManager(), fragments);
		mViewPager.setAdapter(mAdapetr);
		mViewPager.setOnPageChangeListener(pageListener);
	}

	/**
	 * 页面改变监听事件
	 * */
	public OnPageChangeListener pageListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			mViewPager.setCurrentItem(position);
			selectTab(position);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * 按钮监听事件
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.top_profile:
			slidingMenu.showMenu();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Check the Network Connection
	 * 
	 * @return
	 */
	public boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

	@SuppressLint("ShowToast")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long now = new Date().getTime();
			if (now - mLastBackTime < TIME_DIFF) {
				return super.onKeyDown(keyCode, event);
			} else {
				mLastBackTime = now;
				Toast.makeText(this, "重复刚才的动作将退出应用", 2000).show();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
