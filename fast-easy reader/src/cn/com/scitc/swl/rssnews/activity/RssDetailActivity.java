package cn.com.scitc.swl.rssnews.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.StatusesAPI;

import cn.com.scitc.swl.rssnews.R;
import cn.com.scitc.swl.rssnews.constants.AccessTokenKeeper;
import cn.com.scitc.swl.rssnews.model.RssNews;
import cn.com.scitc.swl.rssnews.service.FileService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ProgressBar;

public class RssDetailActivity extends Activity implements View.OnClickListener {

	private WebView webView;

	private ProgressBar bar;

	private Button btnBack;
	private Button btnShare;
	private CheckBox btnCollect;
	private Button btnRefresh;
	/** 底部工具栏 */
	private LinearLayout linearLayout;
	/** 新闻数据 */
	private String list;
	/** 新闻数据 */
	private RssNews news = new RssNews();
	/** 用于控制是否第一次收藏 */
	private int cilckCount = 0;

	private StatusesAPI statusesAPI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rss_detail);
		initView();
		initData();
		new Thread(new Runnable() {

			@Override
			public void run() {
				webView.loadUrl(news.link);
			}
		}).start();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		bar = (ProgressBar) findViewById(R.id.deatail_pb);
		bar.setMax(100);
		btnBack = (Button) findViewById(R.id.rss_btn_back);
		btnBack.setOnClickListener(this);
		btnShare = (Button) findViewById(R.id.rss_btn_share);
		btnShare.setOnClickListener(this);
		btnCollect = (CheckBox) findViewById(R.id.rss_btn_collect);
		btnCollect.setOnClickListener(this);
		btnCollect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// 此处为第一次收藏，如果已收藏的话就不在缓存和提示
				if (arg1 && cilckCount == 0) {
					FileService.savaImgToSdcard(news.imgName + ".txt",
							Context.MODE_PRIVATE, list.getBytes(), "rssCollect");
					Toast.makeText(RssDetailActivity.this, "收藏成功啦!",
							Toast.LENGTH_SHORT).show();

					List<RssNews> list = FileService.readListFile("rssCollect");
					System.out.println("list===>" + list.toString());
				} else if (arg1 == false) {
					FileService.delFile(news.imgName + ".txt", "rssCollect");
					Toast.makeText(RssDetailActivity.this, "取消收藏成功!",
							Toast.LENGTH_SHORT).show();
					cilckCount = 0;
				}
			}
		});
		btnRefresh = (Button) findViewById(R.id.rss_btn_refresh);
		btnRefresh.setOnClickListener(this);
		linearLayout = (LinearLayout) findViewById(R.id.rss_bottom_ll);
		webView = (WebView) findViewById(R.id.rss_webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setDefaultTextEncodingName("UTF-8");
		webView.setWebViewClient(new WebClient());
		webView.setWebChromeClient(new WebChrome());
	}

	private void initData() {
		list = getIntent().getExtras().getString("list");
		try {
			JSONObject jsonObject = new JSONObject(list);
			news = RssNews.parse(jsonObject);
			System.out.println("jsonObject===>" + news.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (FileService.readImgFromSdcard(news.imgName + ".txt",
				Context.MODE_PRIVATE, "rssCollect") != null) {
			cilckCount++;
			btnCollect.setChecked(true);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack(); // goBack()表示返回WebView的上一页面
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class WebClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}

	private class WebChrome extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			bar.setProgress(newProgress);
			if (newProgress == 100) {
				bar.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
				linearLayout.setVisibility(View.VISIBLE);
			}
			super.onProgressChanged(view, newProgress);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rss_btn_back:
			finish();
			break;
		case R.id.rss_btn_collect:
			break;
		case R.id.rss_btn_share:
			// 判断是否已经登录
			if (AccessTokenKeeper.readAccessToken(this).isSessionValid()) {

				final AlertDialog dialog = new AlertDialog.Builder(this)
						.create();
				dialog.show();
				Window window = dialog.getWindow();
				//解决对话框中输入框不弹出输入法
				window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
				window.setContentView(R.layout.share_alert_dialog);
				TextView title = (TextView) window
						.findViewById(R.id.dialog_title);
				final EditText text = (EditText) window
						.findViewById(R.id.share_edittext);
				title.setText("分享内容");
				window.findViewById(R.id.share_btn_ok).setOnClickListener(
						new View.OnClickListener() {

							@Override
							public void onClick(View v) {

								// 已经登录就创建分享接口
								statusesAPI = new StatusesAPI(
										AccessTokenKeeper
												.readAccessToken(RssDetailActivity.this));
								statusesAPI
										.update(text.getText().toString()
												+ "                                                              #"
												+ news.title
												+ "#                                                              "
												+ news.link, "0.0", "0.0",
												new RequestListener() {

													@Override
													public void onWeiboException(
															WeiboException e) {
														e.printStackTrace();
													}

													@Override
													public void onComplete(
															String response) {
														if (!TextUtils
																.isEmpty("response"))
															Toast.makeText(
																	RssDetailActivity.this,
																	"分享成功",
																	Toast.LENGTH_SHORT)
																	.show();
														System.out
																.println("response===>"
																		+ response);
													}
												});
								dialog.dismiss();
							}
						});
				window.findViewById(R.id.share_btn_cancel).setOnClickListener(
						new View.OnClickListener() {

							@Override
							public void onClick(View arg0) {
								dialog.cancel();
							}
						});

			} else {
				Toast.makeText(RssDetailActivity.this, "你还没有登录哦！赶快登录吧！",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.rss_btn_refresh:
			webView.reload();
			break;
		}
	}
}
