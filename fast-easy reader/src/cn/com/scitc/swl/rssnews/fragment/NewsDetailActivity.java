package cn.com.scitc.swl.rssnews.fragment;

import java.io.File;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import cn.com.scitc.swl.rssnews.R;
import cn.com.scitc.swl.rssnews.constants.CommonUrl;
import cn.com.scitc.swl.rssnews.model.News;
import cn.com.scitc.swl.rssnews.service.HttpUtils;

public class NewsDetailActivity extends Activity {

	/** webview组件 */
	private WebView mWebView;
	/** 加载提示 */
	private TextView proText;
	/** 新闻id */
	private Long mNewsId;
	/** 新闻图片 */
	private String mNewsImgName;
	/** 新闻标题 */
	private TextView mNewsTitle;
	/** 新闻时间 */
	private TextView mNewsTime;
	/** 新闻数据 */
	private News mNews = new News();
	/** 文本内容 */
	private StringBuffer content = new StringBuffer();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_detail);
		initView();
		new LoadData().execute(mNewsId);
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		mNewsId = getIntent().getLongExtra("newsId", 0);
		String mNewsImg = getIntent().getStringExtra("newsImg");
		System.out.println("mNewsImg===>" + mNewsImg);
		if (!mNewsImg.equals("") && mNewsImg != null) {
			int start = mNewsImg.lastIndexOf("/");
			mNewsImgName = mNewsImg.substring(start + 1);
		}
		mWebView = (WebView) findViewById(R.id.news_webview);
		proText = (TextView) findViewById(R.id.progress_text);
		mNewsTitle = (TextView) findViewById(R.id.newsdetail_time_title);
		mNewsTime = (TextView) findViewById(R.id.newsdetail_time_tv);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
		mWebView.getSettings().setDefaultFontSize(16);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
			mWebView.goBack(); // goBack()表示返回WebView的上一页面
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 加载新闻详情数据 */
	class LoadData extends AsyncTask<Long, Void, News> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			proText.setVisibility(View.VISIBLE);
		}

		@Override
		protected News doInBackground(Long... arg0) {
			String jsonString = HttpUtils.httpPost(CommonUrl.NES_DETAIL_PATH
					+ arg0[0], "utf-8");
			System.out.println("===jsonString=>" + jsonString);
			try {
				JSONObject jsonObject = new JSONObject(jsonString);
				JSONObject jsonObject2 = jsonObject.getJSONObject("yi18");
				mNews.id = jsonObject2.optLong("id");
				mNews.title = jsonObject2.optString("title");
				mNews.message = jsonObject2.optString("message");
				mNews.time = jsonObject2.optString("time");
				mNews.tag = jsonObject2.optString("tag");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return mNews;
		}

		@Override
		protected void onPostExecute(News result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result != null) {
				mNewsTitle.setText(result.title);
				mNewsTime.setText(result.time);
				File dir = new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/rssCache");
				String sb = "<img src=\"file:///" + dir.getAbsolutePath() + "/"
						+ mNewsImgName + "\" />";
				content.append(sb);
				content.append("<FONT face=\"verdana\" style=\"letter-spacing:1px;text-indent:30px\" color=#3C3C3C>");
				content.append(result.message);
				content.append("</FONT>");
				System.out.println(sb);
				mWebView.loadDataWithBaseURL(null, content.toString(),
						"text/html", "UTF-8", null);
			}
			proText.setVisibility(View.GONE);
		}
	}
}
