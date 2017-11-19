package com.jesus.christiansongs.player;

import java.util.Iterator;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.jesus.christiansongs.MoreApps;
import com.jesus.christiansongs.R;


public class PlayerActivity extends Activity {
	String song = null;
	MediaPlayer mm = null;
	private String TAG = "Playsong";
	private ImageView playpause = null;
	private ImageView next = null;
	private ImageView previous = null;
	private int STATE;
	private SeekBar iseekbar = null;
	private TextView com_time = null;
	private TextView max_time = null;
	public static final int SHIFT_PREVIOUS_SONG = -1;
	public static final int SHIFT_NEXT_SONG = 1;
	private Handler mUiHandler;
	private static final int MSG_UPDATE_PROGRESS = 0x1;
	private Utilities utils;
	private TextView MovieNamev = null;
	private TextView songtitleview = null;
	private TextView playertitlev = null;
	private String playerneedposition = null;
	protected Dialog dialog;
	private ImageView playlist=null;
	ListView listplay=null;
	
	private PopupWindow pw;

	public static final String SERVICECMD = "com.android.music.musicservicecommand";

	public static final String CMDNAME = "command";
	public static final String CMDTOGGLEPAUSE = "togglepause";
	public static final String CMDSTOP = "stop";
	public static final String CMDPAUSE = "pause";
	public static final String CMDPREVIOUS = "previous";
	public static final String CMDNEXT = "next";

	private AdView adView;
	private AdRequest request;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		//		WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			playerneedposition = bundle.getString("playerposition");
		}

		setContentView(R.layout.player);
		
		Playbackservice.isactive = true;
		mUiHandler = new Handler();
		Playbackservice.addActivity(this);
		playpause = (ImageView) findViewById(R.id.play);
		next = (ImageView) findViewById(R.id.next);
		previous = (ImageView) findViewById(R.id.previous);
		iseekbar = (SeekBar) findViewById(R.id.seekBar1);
		playlist=(ImageView) findViewById(R.id.playlist);
		com_time = (TextView) findViewById(R.id.com_time);
		max_time = (TextView) findViewById(R.id.max_time);
		MovieNamev = (TextView) findViewById(R.id.moviename);
		songtitleview = (TextView) findViewById(R.id.songname);
		playertitlev = (TextView) findViewById(R.id.movietitle);
		iseekbar.setOnSeekBarChangeListener(seekbarlistener);
		playlist.setOnClickListener(mlistener);
		playpause.setOnClickListener(mlistener);
		next.setOnClickListener(mlistener);
		previous.setOnClickListener(mlistener);
		utils = new Utilities();

		// Showing AD
		LinearLayout layout = (LinearLayout) findViewById(R.id.adplayer);
		// Create an ad.
	    adView = new AdView(this);
	    adView.setAdSize(AdSize.BANNER);
	    adView.setAdUnitId(Dataengine.AD_UNIT_ID);
		layout.addView(adView);
		// Create an ad request. Check logcat output for the hashed device ID to
	    // get test ads on a physical device.
	    request = new AdRequest.Builder()
	        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	        .build();
		adView.loadAd(request);

		MyDebug.i(TAG, "===============oncreate =================");

		com_time.setTypeface(Typeface.SERIF, Typeface.BOLD);
		max_time.setTypeface(Typeface.SERIF, Typeface.BOLD);
		MovieNamev.setTypeface(Typeface.SERIF, Typeface.BOLD);
		songtitleview.setTypeface(Typeface.SERIF, Typeface.BOLD);
		playertitlev.setTypeface(Typeface.SERIF, Typeface.BOLD);

		if ((getApplicationContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
			com_time.setTextSize(22);
			max_time.setTextSize(22);
			MovieNamev.setTextSize(22);
			songtitleview.setTextSize(22);
			playertitlev.setTextSize(22);

		} else if ((getApplicationContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE) {
			com_time.setTextSize(28);
			max_time.setTextSize(28);
			MovieNamev.setTextSize(28);
			songtitleview.setTextSize(28);
			playertitlev.setTextSize(34);
		}

	}

	@Override
	protected void onPause() {
		MyDebug.i(TAG, "===============onpause =================");
		playerneedposition = "start";
		Playbackservice.isactive = false;
		super.onPause();
	}

	@Override
	protected void onResume() {
		MyDebug.i(TAG, "===============onresume =================");
		Playbackservice.isactive = true;
		super.onResume();
	}

	@Override
	public void sendBroadcast(Intent intent) {

		super.sendBroadcast(intent);
	}

	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			//Log.i(TAG, "===================onrecied called=============");
			Bundle b = intent.getExtras();
			Set<String> set = b.keySet();
			Iterator it = set.iterator();
			while (it.hasNext() == true) {
				Toast.makeText(getApplicationContext(), "" + it.next(),
						Toast.LENGTH_SHORT).show();
			}

			/*
			 * String action = intent.getAction(); String cmd =
			 * intent.getStringExtra("command"); Log.v("tag ", action + " / " +
			 * cmd); String artist = intent.getStringExtra("artist"); String
			 * album = intent.getStringExtra("album"); String track =
			 * intent.getStringExtra("track");
			 * Log.v("tag",artist+":"+album+":"+track);
			 * Toast.makeText(Playsong.this,track,Toast.LENGTH_SHORT).show();
			 */

		}
	};

	public void getsongdetails() {
		
	}

	OnSeekBarChangeListener seekbarlistener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mUiHandler.removeCallbacks(mUpdateTimeTask);
			int totalDuration = Playbackservice.get(PlayerActivity.this)
					.getcomplietposition();
			int currentPosition = utils.progressToTimer(seekBar.getProgress(),
					totalDuration);

			// forward or backward to certain seconds
			Playbackservice.get(PlayerActivity.this).seekto(currentPosition);

			// update timer progress again
			updateProgressBar();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			mUiHandler.removeCallbacks(mUpdateTimeTask);
			MyDebug.i("", "=============onstart============");
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			MyDebug.i("", "=============onprogresschanged============");
		}

	};

	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
		mUiHandler.postDelayed(mUpdateTimeTask, 100);
	}

	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {

			if (Playbackservice.get(PlayerActivity.this).ispalying()) {
				long totalDuration = Playbackservice.get(PlayerActivity.this)
						.getcomplietposition();
				long currentDuration = Playbackservice.get(PlayerActivity.this)
						.getposition();

				// Displaying Total Duration time
				com_time.setText(""
						+ utils.milliSecondsToTimer(currentDuration));
				// Displaying time completed playing
				max_time.setText("" + utils.milliSecondsToTimer(totalDuration));
				MovieNamev.setText(Playbackservice.get(PlayerActivity.this)
						.getMovieTitle());
				songtitleview.setText(Playbackservice.get(PlayerActivity.this)
						.getSongTitle());
				playertitlev.setText(Playbackservice.get(PlayerActivity.this)
						.getMovieTitle());
				// Updating progress bar
				int progress = (int) (utils.getProgressPercentage(
						currentDuration, totalDuration));
				// Log.d("Progress", ""+progress);
				iseekbar.setProgress(progress);
				// getsongdetails();

			}

			// Running this thread after 100 milliseconds
			mUiHandler.postDelayed(mUpdateTimeTask, 100);
		}
	};


	private void updateseekbar() {

		mUiHandler.removeMessages(MSG_UPDATE_PROGRESS);
		mUiHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, 500);
	}

	OnClickListener mlistener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (v.getId() == playpause.getId()) {
				MyDebug.i(TAG, "================palypause===========");
				Playbackservice service2 = Playbackservice.get(PlayerActivity.this);
				STATE = service2.playerActions();
				setstate(STATE);

			} else if (v.getId() == next.getId()) {
				setstate(1);
				Playbackservice.get(PlayerActivity.this).shiftcurrentsong(
						SHIFT_NEXT_SONG);

			} else if (v.getId() == previous.getId()) {
				setstate(1);
				Playbackservice.get(PlayerActivity.this).shiftcurrentsong(
						SHIFT_PREVIOUS_SONG);

			}else if(v.getId()==playlist.getId()){
				
				//getPopUpWindow();
				
				    dialog = new Dialog(PlayerActivity.this);
	                dialog.setContentView(R.layout.playlist);
	                dialog.setTitle("Play List");
	                dialog.setCancelable(true);
	                
	                listplay=(ListView) dialog.findViewById(android.R.id.list); 
	                listplay.setAdapter(new ImagetAdpater(PlayerActivity.this));
	                dialog.show();
	                listplay.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
						dialog.dismiss();
							
						}
					});
	                
	                
	                
	                
				
			}

		}

	};

	
	private void getPopUpWindow(){

		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		LayoutInflater inflater = (LayoutInflater) PlayerActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Inflate the view from a predefined XML layout
		View layout = inflater.inflate(R.layout.sample,
				(ViewGroup) findViewById(R.id.sample));
		layout.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.popup_background));
		
		// create a 300px width and 470px height PopupWindow

		LinearLayout home_layout = (LinearLayout) layout.findViewById(R.id.layout_AboutApp);
		
		home_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pw.dismiss();
				new AlertDialog.Builder(PlayerActivity.this)
				.setTitle("About App")
				.setMessage(R.string.aboutapp)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
						public void onClick(DialogInterface dialog, int whichButton){
							
						}
				})
				
				.setCancelable(true)
				.show();
			}
		});
		
		LinearLayout feedback_layout = (LinearLayout) layout
		.findViewById(R.id.layout_feedback);
		feedback_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pw.dismiss();
				Intent sendIntent;
				sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.setType("plain/text");
				sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				          new String[] { "kvr.androidapps@gmail.com" });
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Feed Back On All Christian Songs");
				sendIntent.putExtra(Intent.EXTRA_TEXT, "");
				startActivity(Intent.createChooser(sendIntent, "Send Mail"));

			}
		});
		
		LinearLayout moreapps_layout = (LinearLayout) layout.findViewById(R.id.layout_MoreApps);
		moreapps_layout.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent(PlayerActivity.this,MoreApps.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(intent);
			pw.dismiss();
		}
  });
		
		LinearLayout share_layout = (LinearLayout) layout.findViewById(R.id.shareApp);
		share_layout.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("text/plain");
			share.putExtra(Intent.EXTRA_TEXT,Html.fromHtml("************"));
			startActivity(Intent.createChooser(share,"Share Using"));
			pw.dismiss();
		}
  });
		
		/*
		 * LinearLayout settings = (LinearLayout) layout.findViewById(R.id.settings);
		settings.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			startActivity(new Intent().setAction(Constants.SETTINGS_ACTION));
			pw.dismiss();
		}
  });*/
		
		pw = new PopupWindow(layout, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		pw.showAtLocation(layout, Gravity.RIGHT, 15,-100);

		if (null == pw.getBackground()) {

			pw.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.popup_background));

		} else {
			pw.setBackgroundDrawable(new BitmapDrawable());
		}

		if (null != pw) {
		}

		pw.setOutsideTouchable(true);
		pw.setTouchable(true);
		layout.setClickable(true);

		pw.setFocusable(true);
		pw.getContentView().setClickable(true);
		pw.getContentView().setFocusableInTouchMode(true);

		// pw.setAnimationStyle(R.style.Theme_Transparent);
		pw.getContentView().setVerticalFadingEdgeEnabled(true);
		
		layout.setOnKeyListener(new View.OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				MyDebug.i("NewsTab","hi cusror is in back buton event occureing.##########");
				boolean res = false;
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getRepeatCount() == 0) {
					// do something on back.
					// Log.e("keydown","back");
					if (pw.isShowing()) {
						// Log.e("keydown","pw showing");

						pw.dismiss();
						getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
											 WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
						res = true;
					}
				} else {
					res = false;
				}
				return res;
			}
		});

		pw.getContentView().setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				pw.dismiss();
				getWindow().setFlags(
						WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
						WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);

			}
		});

	
	}
	
	
	class ImagetAdpater extends BaseAdapter  {
		Context context;
		LayoutInflater mInflater;

		public ImagetAdpater(PlayerActivity lIstclass) {
			context = lIstclass;
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {

			if (null != Playbackservice.slist && Playbackservice.slist.size() > 0) {
				return Playbackservice.slist.size();
			} else {
				return 0;
			}

		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();

				// get layout from mobile.xml

				convertView = mInflater.inflate(R.layout.list, null);

				// set value into textview
				holder.textView = (TextView) convertView
						.findViewById(R.id.list_item_label);

				// set image based on selected text
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.list_item_image);
				ImageView arrowImg =(ImageView) convertView.findViewById(R.id.arrow);
				arrowImg.setVisibility(View.INVISIBLE);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();

			}

			holder.textView.setTextColor(Color.parseColor("#424242"));
			// holder.textView.setTypeface(face,Typeface.BOLD);
		//	String cond = Playbackservice.slist.get(position).getCondition();
			
				holder.imageView.setImageResource(R.drawable.music_icon_not);
				String name = Playbackservice.slist.get(position).get("songname");
				name = name.toLowerCase();
				name = name.replace(".mp3", "");
				holder.textView.setText(name);
		
			holder.textView.setTypeface(Typeface.SERIF, Typeface.BOLD);
			if ((getApplicationContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
				holder.textView.setTextSize(24);
			} else if ((getApplicationContext().getResources()
					.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE) {
				holder.textView.setTextSize(30);
			}
			holder.textView.setTextColor(Color.parseColor("#424242"));

			return convertView;

		}

		

	}

	class ViewHolder {
		TextView textView;
		ImageView imageView;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_UPDATE_PROGRESS:
			updateseekbar();
			break;
		}
		return true;
	}

	protected void setstate(int state) {
		if (state == 0) {
			MyDebug.i(TAG, "================pause===========");

			playpause.setImageResource(R.drawable.play);

		} else if (state == 1) {
			MyDebug.i(TAG, "================paly===========");
			playpause.setImageResource(R.drawable.pause);

		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		MyDebug.i(TAG, "===============onstart =================");
		if (Playbackservice.hasInstance()) {
			MyDebug.i(TAG,"===========playback service aleredy started==========");
			Playbackservice service1 = Playbackservice.get(this);
			if (!service1.ispalying()
					|| playerneedposition.equalsIgnoreCase("stop")) {

				service1.stopplayer();
				if(Dataengine.songlist.size()>0){
					service1.addsong();
					service1.startplayer();
				}
				
			}

		} else {
			MyDebug.i(TAG,
					"===========playback service newly started==========");
			startService(new Intent(PlayerActivity.this, Playbackservice.class));

		}
		iseekbar.setProgress(0);
		iseekbar.setMax(100);
		updateProgressBar();
	}

	protected void onDestroy() {
		super.onDestroy();
		// Playbackservice.removeActivity(this);
		Playbackservice service1 = Playbackservice.get(this);
		if (!service1.ispalying()) {
			MyDebug.i(TAG,
					"=========playsongon destory now stop service===============");
			try{
				stopService(new Intent(PlayerActivity.this, Playbackservice.class));
				service1.stopNotification();
			}catch (Exception e) {
				
			}
			
		}

		Playbackservice.isactive = false;

		if (adView != null)
			adView.destroy();
	};

}
