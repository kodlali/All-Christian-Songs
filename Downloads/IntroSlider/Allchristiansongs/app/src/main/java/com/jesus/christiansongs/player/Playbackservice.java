package com.jesus.christiansongs.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class Playbackservice extends Service implements
		MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener,
		MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener,
		Useractions {

	private MediaPlayer mp = null;
	public static ArrayList<HashMap<String, String>> slist = new ArrayList<HashMap<String, String>>();
	private String TAG = "Playbackservice";
	private ProgressDialog dialog = null;

	public static boolean isactive = false;

	public int position = 0;
	private static final ArrayList<PlayerActivity> sActivities = new ArrayList<PlayerActivity>(
			5);

	/**
	 * Object used for PlaybackService startup waiting.
	 */
	private static final Object[] sWait = new Object[0];
	/**
	 * The appplication-wide instance of the PlaybackService.
	 */
	public static Playbackservice sInstance;

	@Override
	public void onCreate() {
		super.onCreate();

		if (null != Dataengine.songlist && Dataengine.songlist.size() > 0)
			addsong();
		MyDebug.i(TAG, "=========service c lasss in oncreate method=========="
				+ slist.size());
		mp = new MediaPlayer();
		
		
		mp.setOnCompletionListener(this);
		mp.setOnPreparedListener(this);
		sInstance = this;
	}

	public void addsong() {
		position = 0;
		slist.clear();
		for (int i = 0; i < Dataengine.songlist.size(); i++) {
			slist.add(Dataengine.songlist.get(i));
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		MyDebug.i(TAG, "===========onstartcommand=============");
		startplayer();
		return START_STICKY;
	}

	public void startplayer() {
		if (isactive) {
			showDilog();
		}

		listenPhoneCall();
		MyDebug.i(TAG, "============onstartplayer==========" + position);
		try {
			if(null!=slist&&slist.size()>0){
				mp.reset();
				mp.setDataSource(slist.get(position).get("songpath"));
				mp.prepareAsync();
			}
			
		}
		catch(IndexOutOfBoundsException e){
			
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Exception e1) {
			// TODO: handle exception
			e1.printStackTrace();
		}
	}

	public void stopplayer() {
		unListenPhoneCall();
		if (null != mp && mp.isPlaying()) {
			mp.stop();
			try{
				stopNotification();
			}catch (Exception e) {
				// TODO: handle exception
			}
			
		}
	}

	@Override
	public void onDestroy() {
		MyDebug.i(TAG, "===========service is stopped===================");
		try{
		if (null != dialog) {
			dialog.dismiss();
		}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		MyDebug.i(TAG, "=========oncomplete===============" + position);
		if (slist.size() - 1 > position) {
			position += 1;
			startplayer();
		} else {
			stopplayer();
			int sis = sActivities.size() - 1;
			sActivities.get(sis).setstate(mp.isPlaying() ? 1 : 0);
			stopNotification();
			stopSelf();
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		MyDebug.i("onError", "==================MediaPlayer="+mp);
		MyDebug.i("onError", "==================what="+what);
		MyDebug.i("onError", "==================extra="+extra);
		if (isactive) {
			disDilog();
		}
		return false;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {

	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
		startNotification();
		if (isactive)
			disDilog();
		MyDebug.i(TAG, "=========onprepared============");
	}

	PhoneStateListener phoneStateListener = new PhoneStateListener() {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			try{
			AudioManager audioManager = (AudioManager) sActivities.get(0)
					.getSystemService(Context.AUDIO_SERVICE);

			if (state == TelephonyManager.CALL_STATE_RINGING) {

				// int ff=playerActions();

				// For example to set the volume of played media to maximum.
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
				// Toast.makeText(sActivities.get(0),"===Mute sound====",Toast.LENGTH_SHORT).show();
				/*
				 * if( sActivities.get(0).getWindow().isActive()){
				 * sActivities.get(0).setstate(ff); }
				 */

				// Incoming call: Pause music
			} else if (state == TelephonyManager.CALL_STATE_IDLE) {
				// Toast.makeText(sActivities.get(0),"===UNMuted sound====",Toast.LENGTH_SHORT).show();
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
				// unListenPhoneCall();
				// Not in call: Play music
				// playerActions();
			} else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
				audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
				// A call is dialing, active or on hold
			}
			super.onCallStateChanged(state, incomingNumber);
			
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};

	public void listenPhoneCall() {
		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if (mgr != null) {
			mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

		}
	}

	public void unListenPhoneCall() {
		TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		if (mgr != null) {
			mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
	}

	/**
	 * Return the PlaybackService instance, creating one if needed.
	 */
	public static Playbackservice get(Context context) {
		if (sInstance == null) {
			context.startService(new Intent(context, Playbackservice.class));

			while (sInstance == null) {
				try {
					synchronized (sWait) {
						sWait.wait();
					}
				} catch (InterruptedException ignored) {
				}
			}
		}

		return sInstance;
	}

	/**
	 * Returns true if a PlaybackService instance is active.
	 */
	public static boolean hasInstance() {
		return sInstance != null;
	}

	public static void addActivity(PlayerActivity activity) {
		sActivities.add(activity);
	}

	/**
	 * Remove an Activity from the registered PlaybackActivities
	 * 
	 * @param activity
	 *            The Activity to be removed
	 */
	public static void removeActivity(PlayerActivity activity) {
		sActivities.remove(activity);
	}

	public int playerActions() {

		MyDebug.i(TAG, "================playeractoions===========");
		if (null != mp && mp.isPlaying()) {
			mp.pause();
			stopNotification();
			return 0;
		} else {
			mp.start();
			startNotification();
			return 1;
		}

	}

	public void seekto(int sek) {
		mp.seekTo(sek);
	}

	public int getposition() {

		return mp.getCurrentPosition();

	}

	public int getcomplietposition() {
		return mp.getDuration();
	}

	public boolean ispalying() {
		return mp.isPlaying();
	}

	public void shiftcurrentsong(int shiftSong) {

		if (shiftSong == 1) {
			if (slist.size() - 1 > position) {
				position += shiftSong;
				stopplayer();
				startplayer();
				MyDebug.i(TAG,
						"================player start next song===========");
			} else {
				Toast.makeText(getApplicationContext(),
						"No songs are avilable", Toast.LENGTH_SHORT).show();
				sActivities.get(0).setstate(mp.isPlaying() ? 1 : 0);
			}

		} else if (shiftSong == -1) {

			if (position + shiftSong >= 0
					&& slist.size() - 1 > position + shiftSong) {
				position += shiftSong;
				stopplayer();
				startplayer();
				MyDebug.i(TAG,
						"================player start previous song===========");
			} else {
				sActivities.get(0).setstate(mp.isPlaying() ? 1 : 0);
				Toast.makeText(getApplicationContext(),
						"No songs are avilable", Toast.LENGTH_SHORT).show();
			}

		}

	}

	private void showDilog() {
      try{
    	  dialog = new ProgressDialog(sActivities.get(sActivities.size() - 1));
  		dialog.setMessage("Loading ....");
  		dialog.show();
      }catch (Exception e) {
    	  
	  }
		
	}

	private void disDilog() {
		dialog.dismiss();
	}

	@Override
	public String getSongTitle() {

		String songname = slist.get(position).get("songname");
		songname=songname.toLowerCase();
		songname = songname.replace(".mp3","");
		return songname;
	}

	@Override
	public String getMovieTitle() {
		String moviename = slist.get(position).get("moviename");
		return moviename;
	}

	@Override
	public void startNotification() {
		// TODO Auto-generated method stub
		startService(new Intent(sActivities.get(sActivities.size() - 1),
				MyNotificationService.class));

	}

	@Override
	public void stopNotification() {
		// TODO Auto-generated method stub
		try{
			stopService(new Intent(sActivities.get(sActivities.size() - 1),
					MyNotificationService.class));
		}catch (Exception e) {
			// TODO: handle exception
		}
		

	}

}
