package com.jesus.christiansongs;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class SplashScreenActivity extends Activity {

	private final int STOPSPLASH = 0;
	// time in milliseconds
	private final long SPLASHTIME = 3000;

	private ImageView splash;
	String versionName = "";

	/** Called when the activity is first created. */

	@SuppressWarnings("static-access")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		//requestWindowFeature(getWindow().FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			//	WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.splash);
		splash = (ImageView) findViewById(R.id.imag_splash);
		if (null != splash) {
			splash.setImageResource(R.drawable.music_splash);

		}

		Animation hyperspaceJump = AnimationUtils.loadAnimation(this,
				R.anim.stypes);
		splash.startAnimation(hyperspaceJump);
		splash.setScaleType(ScaleType.FIT_XY);

		showSplashAndDownloadXmls();
	}

	public void showSplashAndDownloadXmls() {

		Runnable r = new Runnable() {
			public void run() {
				Message msg = new Message();
				msg.what = STOPSPLASH;
				splashHandler.sendMessageDelayed(msg, SPLASHTIME);
			}
		};
		new Thread(r).start();

	}

	// handler for splash screen
	private Handler splashHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case STOPSPLASH:
				Log.i("Message", "what=" + msg.what);
				// remove SplashScreen from view
				if (splash == null) {
					Log.i("Splash", "null");
				} else {
					// splash.setVisibility(View.GONE);
				}

					Intent intent = new Intent(SplashScreenActivity.this,
							AllChristianSongs.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);  
				
				

				//intent.putExtra("tabactivity","soccer");
				

				

				//Set the transition -> method available from Android 2.0 and beyond  
				overridePendingTransition(R.anim.rotate_in,R.anim.rotate_out);
				finish();
				break;
			}
			// super.handleMessage(msg);
		}
	};

	@Override
	protected void onDestroy() {
		
		Log.i("Main program Destroy", "called");
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		
		super.onPause();
	}

	@Override
	protected void onResume() {
		
		super.onResume();
	}

	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);

	}

}
