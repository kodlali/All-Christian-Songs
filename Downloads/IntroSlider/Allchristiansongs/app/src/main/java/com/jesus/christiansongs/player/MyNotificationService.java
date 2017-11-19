/**
*  ==============================================================================
*  Name        : MyNotificationService.java
*  Part of     : Leadapps FLV Radio Player
*  Interface   : 
*  Description : 
*  Version     : 1.0.0
*
*  Copyright (c) 2008-2009 ARIJA SOFTWARE PVT LTD.
*  This material, including documentation and any related
*  programs, is protected by copyright controlled by
*  ARIJA SOFTWARE PVT LTD.
*  ================================================================================
*/
package com.jesus.christiansongs.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.jesus.christiansongs.R;

import static com.jesus.christiansongs.R.string.notification;


public class MyNotificationService extends Service
{
    private NotificationManager mNotifyMgr;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder 
    {
        MyNotificationService getService() 
        {
            return MyNotificationService.this;
        }
    }
    
    @Override
    public void onCreate() 
    {
    	mNotifyMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showMyRadioNotification();
    }

    @Override
    public void onDestroy() 
    {
        // Cancel the persistent notification.
    	mNotifyMgr.cancel(notification);
        // Tell the user we stopped.
        Toast.makeText(this,"player  and notification Stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) 
    {
        return mBinder;
    }
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    /**
     * Show a notification while this service is running.
     */
    private void showMyRadioNotification() 
    {
        CharSequence status_text = "Playing..";//MyMediaEngine.cur_Channel_NAME;
        // Set the icon, scrolling text and timestamp
        NotificationCompat.Builder nc = new NotificationCompat.Builder(getApplicationContext());

        nc.setSmallIcon(R.drawable.music_icon_not);
        //nc.setAutoCancel(true);
        nc.setContentTitle("Playing...");
        //nc.setContentText("Showing notification content");
        nc.setTicker("Ticker 1");
        nc.setNumber(2);

          //R.drawable.music_icon_not, status_text, System.currentTimeMillis()

        nc.getNotification().flags = Notification.FLAG_NO_CLEAR;
        Intent myPlayerIntent = new Intent(this, PlayerActivity.class);
        myPlayerIntent.putExtra("playerposition","notstop");
        myPlayerIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        myPlayerIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        // The PendingIntent to launch our activity if the user selects this notification
        /*PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, arijaStreamPlayer.class), 0);*/
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
        		myPlayerIntent, 0);
        nc.setContentIntent(contentIntent);
        mNotifyMgr.notify(1, nc.build());
        // Set the info for the views that show in the notification panel.
        /*notification.setContentIntent(resultPendingIntent);
        notification.setLatestEventInfo(this, getText(R.string.app_name),
        		status_text, contentIntent);*/
        // Send the notification.
        // We use a layout id because it is a unique number.  We use it later to cancel.
        //mNotifyMgr.notify(notification, nc);
    }
}

