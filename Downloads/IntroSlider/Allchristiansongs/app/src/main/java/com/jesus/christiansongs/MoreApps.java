package com.jesus.christiansongs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.jesus.christiansongs.player.MyDebug;

public class MoreApps extends Activity {
		
	private static final int DIALOG_WORK_PROG =1000;
	private  WebView mWebView;
	String UrlToLoad 			= 	"https://play.google.com/store/search?q=kodali";
	ProgressDialog	workProgress_UP 			=	null;
	private  Handler my_UI_Handler_Channels 	= 	new Handler();
	boolean m_prgisShowing 		= 	false;
	private AdView adView;
	private AdRequest request;
	//Context appContext = this.getApplicationContext();
	 
	 /** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.webview_androidmarket);
	    Bundle myExt = getIntent().getExtras();
	    if(null != myExt){
	    	UrlToLoad=myExt.getString("URL");
	    }
	   
	    
	    mWebView = (WebView) findViewById(R.id.webView_playstore);
	    mWebView.getSettings().setJavaScriptEnabled(true);
	   // mWebView.getSettings().setLoadWithOverviewMode(true);
	   // mWebView.getSettings().setUseWideViewPort(true);
	 
	    if(null != UrlToLoad && !UrlToLoad.equals("")){
	    	mWebView.loadUrl(UrlToLoad);
	    }
	    mWebView.setWebViewClient(new MyWebClient());
	   
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && (null != mWebView) && mWebView.canGoBack()) {
	    	mWebView.goBack();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	final Runnable Show_Progress_Bar = new Runnable() 
	{
	    public void run() 
	    {
	    	progress_Show();
	    }
	};
	
	@Override
	protected Dialog onCreateDialog(int dialogID) 
	{
		MyDebug.i("onCreateDialog","=========== Dialog Create called...!");
	    switch (dialogID) 
	    {
	        case DIALOG_WORK_PROG: 
	        {
	        	
	        	workProgress_UP = new ProgressDialog(this);
	        	workProgress_UP.setMessage("Loading...");
	        	workProgress_UP.setIndeterminate(true);
	        	workProgress_UP.setCancelable(true);
	            return workProgress_UP;
	        }
	    }
	    return null;
	 }
	private void progress_Show()
	{
		try{
		m_prgisShowing=true;
		showDialog(DIALOG_WORK_PROG);
		}catch (Exception e) {
			// TODO: handle exception
			
		}
	}
	
	final Runnable Cancel_Progress_Bar = new Runnable() 
	{
	    public void run() 
	    {
	    	progress_Stop();	
	    }
	};
	
	private void progress_Stop()
	{
		try{
	    	if(m_prgisShowing&&null!=workProgress_UP){
	    		workProgress_UP.dismiss();
	    	}
	    	m_prgisShowing=false;
	   	}catch(Exception e){
			
	   		e.printStackTrace();
		}
	}
	final class MyWebClient extends WebViewClient
	{
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) 
	{
		my_UI_Handler_Channels.post(Show_Progress_Bar); 
		super.onPageStarted(view, url, favicon);
	}
	
	 @Override
	 public boolean shouldOverrideUrlLoading(WebView view, String url) {
	     view.loadUrl(url);
	     return true;
	 }
	
	
	@Override
	public void onPageFinished(WebView view, String url) 
	{
		// TODO Auto-generated method stub
		super.onPageFinished(view, url);
		MyDebug.i("WebView","page finished....");
		MyDebug.i("WebView","dismiss dialogue");
		// The following is for remove  the progress bar from the title!!!
		
		
		setProgressBarIndeterminateVisibility(false);
		if(m_prgisShowing){
			my_UI_Handler_Channels.post(Cancel_Progress_Bar);
			//stopThread();
		}
			
	}
	  public void stopThread()
	    {
	    	if(threadToKill != null){
	    		threadToKill.interrupt();
	    		threadToKill = null;
	    	}
	    }
	 Thread threadToKill = 	null;
	
	
	@Override
	public void onLoadResource(WebView view, String url) {
		super.onLoadResource(view, url);
	}
	
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(null != mWebView){
			try{
				mWebView.clearHistory();
			}catch(Exception e){
				
			}
		}
		 if (adView != null) {
		      adView.destroy();
		    }
		    super.onDestroy();
		  
	}
}
