package cn.com.scitc.swl.rssnews.activity;

import cn.com.scitc.swl.rssnews.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	private final int SPLASH_DISPLAY_LENGHT = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.TYPE_STATUS_BAR,
				WindowManager.LayoutParams.TYPE_STATUS_BAR);
		setContentView(R.layout.activity_splash);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(SplashActivity.this, MainActivity.class);
				SplashActivity.this.startActivity(intent);
				SplashActivity.this.finish();
			}
		}, SPLASH_DISPLAY_LENGHT);
	}
}
