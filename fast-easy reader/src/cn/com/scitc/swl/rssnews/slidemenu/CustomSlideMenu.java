package cn.com.scitc.swl.rssnews.slidemenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.scitc.swl.rssnews.R;
import cn.com.scitc.swl.rssnews.constants.AccessTokenKeeper;
import cn.com.scitc.swl.rssnews.constants.WBConstans;
import cn.com.scitc.swl.rssnews.fragment.AlertDialogFragment;
import cn.com.scitc.swl.rssnews.http.DownloadImg;
import cn.com.scitc.swl.rssnews.http.DownloadImg.ImageCalback;
import cn.com.scitc.swl.rssnews.model.User;
import cn.com.scitc.swl.rssnews.service.FileService;
import cn.com.scitc.swl.rssnews.tools.BitmapCompressTools;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;

public class CustomSlideMenu implements View.OnClickListener {

	private static ImageView userAvatar;
	private static TextView userName;

	private static RelativeLayout home;
	private static RelativeLayout favorite;
	private static RelativeLayout cache;
	private static RelativeLayout about;

	public CustomSlideMenu() {
	}

	public static SlidingMenu initSlidingMenu(final Activity activity) {
		final SlidingMenu slidingMenu = new SlidingMenu(activity);
		slidingMenu.setMode(SlidingMenu.LEFT);// 设置左右滑菜单，左侧显示
		slidingMenu.setTouchModeAbove(SlidingMenu.LEFT);// 设置使菜单滑动触碰屏幕的范围
		slidingMenu.setBehindOffsetRes(R.dimen.behindoffsetres);// 剩余主屏幕的宽度
		slidingMenu.setBehindWidthRes(R.dimen.left_drawer_avaar_size);// 设置菜单的宽度
		slidingMenu.setFadeEnabled(true);// 设置是否渐变
		slidingMenu.attachToActivity(activity, SlidingMenu.LEFT);// slidingmenu附加在左边
		slidingMenu.setFadeDegree(0.3F);
		slidingMenu.setMenu(R.layout.left_drawer_fragment);// 设置布局文件
		// slidingMenu.toggle();
		slidingMenu.setOnOpenedListener(new OnOpenedListener() {
			@Override
			public void onOpened() {
			}
		});

		final WeiboAuth mWeiboAuth = new WeiboAuth(activity,
				WBConstans.APP_KEY, WBConstans.REDIRECT_URL, WBConstans.SCOPE);
		home = (RelativeLayout) activity.findViewById(R.id.home_btn);
		favorite = (RelativeLayout) activity.findViewById(R.id.favorite_btn);
		cache = (RelativeLayout) activity.findViewById(R.id.cache_btn);
		about = (RelativeLayout) activity.findViewById(R.id.about_btn);
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				slidingMenu.toggle(true);
			}
		});
		favorite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				slidingMenu.toggle(true);
			}
		});
		cache.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				FileService.delDir("rssCache");
				Toast.makeText(activity, "清除缓存成功", Toast.LENGTH_SHORT).show();
				slidingMenu.toggle(true);
			}
		});
		about.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Toast.makeText(activity, "敬请期待..", Toast.LENGTH_SHORT).show();
				slidingMenu.toggle(true);
			}
		});

		userName = (TextView) activity.findViewById(R.id.tishi_text);
		userAvatar = (ImageView) activity.findViewById(R.id.weibo_btn);
		/** 点击事件，授权登录 */
		userAvatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/** 如果为已登录状态，点击为注销 */
				if (AccessTokenKeeper.readAccessToken(activity)
						.isSessionValid()) {
					final AlertDialogFragment dialog = new AlertDialogFragment(
							activity, "你确定要注销吗？");
					dialog.show(new OnClickListener() {

						@Override
						public void onClick(View v) {
							AccessTokenKeeper.clear(activity);
							FileService.delDir("rssPerson");
							Toast.makeText(activity, "注销成功", Toast.LENGTH_SHORT)
									.show();
							CustomSlideMenu.updateUserInfo(activity);
							dialog.dismiss();
						}
					});
					return;
				}
				SsoHandler mSsoHandler = new SsoHandler(activity, mWeiboAuth);
				mSsoHandler.authorize(new WeiboAuthListener() {

					@Override
					public void onWeiboException(WeiboException e) {
						e.printStackTrace();
					}

					@Override
					public void onComplete(Bundle values) {
						Oauth2AccessToken mAccessToken = Oauth2AccessToken
								.parseAccessToken(values);
						if (mAccessToken.isSessionValid()) {
							// 保存 Token 到 SharedPreferences
							AccessTokenKeeper.writeAccessToken(activity,
									mAccessToken);
							Toast.makeText(activity, "授权成功", Toast.LENGTH_SHORT)
									.show();
							updateUserInfo((Context) activity);
						} else {
							// 以下几种情况，您会收到 Code：
							// 1. 当您未在平台上注册的应用程序的包名与签名时；
							// 2. 当您注册的应用程序包名与签名不正确时；
							// 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
							Toast.makeText(activity, "授权失败", Toast.LENGTH_LONG)
									.show();
						}
					}

					@Override
					public void onCancel() {

					}
				});
			}
		});
		updateUserInfo((Context) activity);
		return slidingMenu;
	}

	/**
	 * 更新用户信息
	 * 
	 * @param c
	 */
	public static void updateUserInfo(Context c) {
		if (AccessTokenKeeper.readAccessToken(c).isSessionValid()) {
			/** 本地存储的用户信息 */
			if ((FileService.readImgFromSdcard("avatar.png",
					Context.MODE_PRIVATE, "rssPerson")) != null) {
				String name = new String(FileService.readImgFromSdcard(
						"avatar.txt", Context.MODE_PRIVATE, "rssPerson"));
				userName.setText(name);
				byte[] data = FileService.readImgFromSdcard("avatar.png",
						Context.MODE_PRIVATE, "rssPerson");
				userAvatar.setImageBitmap(BitmapCompressTools
						.decodeSampledBitmapFromByte(data, 100, 100));
			} else {
				UsersAPI usersAPI = new UsersAPI(
						AccessTokenKeeper.readAccessToken(c));
				usersAPI.show(Long.parseLong(AccessTokenKeeper.readAccessToken(
						c).getUid()), new RequestListener() {

					@Override
					public void onWeiboException(WeiboException e) {
						// TODO Auto-generated method stub
						e.printStackTrace();
					}

					@Override
					public void onComplete(String response) {
						// TODO Auto-generated method stub
						if (!TextUtils.isEmpty(response)) {
							// 调用 User#parse 将JSON串解析成User对象
							User user = User.parse(response);
							userName.setText(user.screen_name);
							// 存储用户昵称
							FileService.savaImgToSdcard("avatar.txt",
									Context.MODE_PRIVATE,
									user.screen_name.getBytes(), "rssPerson");
							DownloadImg downloadImg = new DownloadImg(
									user.avatar_hd);
							downloadImg.DownloadImage(new ImageCalback() {

								@Override
								public void getImage(byte[] data) {
									// TODO Auto-generated method stub
									// 存储用户头像
									FileService.savaImgToSdcard("avatar.png",
											Context.MODE_PRIVATE, data,
											"rssPerson");
									userAvatar.setImageBitmap(BitmapFactory
											.decodeByteArray(data, 0,
													data.length));
								}
							});
						}
					}
				});
			}
			return;
		}
		userName.setText(R.string.left_drawer_no_login_tip);
		userAvatar.setImageResource(R.drawable.ic_drawer_weibo);
		userAvatar.setClickable(true);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

}
