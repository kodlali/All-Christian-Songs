package com.jesus.christiansongs;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

public class SplashActivity extends Activity{
	
	ImageView splash;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		//requestWindowFeature(getWindow().FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		//		WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.splash);
		splash = (ImageView) findViewById(R.id.imag_splash);
		if (null != splash) {
			splash.setImageResource(R.drawable.ic_launcher);

		}
		
		checkSharedPrifereces();
		
		new ParsingTask().execute();
	}
	
	private void checkSharedPrifereces() {
		
		
	}

	class ParsingTask extends AsyncTask<Void,Void,Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			//parsingXML();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
		}
		
	}
}
	
	
