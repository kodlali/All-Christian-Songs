package com.jesus.christiansongs.player;

import java.util.Iterator;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class RecieverTest extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle b = intent.getExtras();
		if (null != b) {
			Set<String> set = b.keySet();
			Iterator it = set.iterator();
			while (it.hasNext() == true) {
				//Log.i("RecieverTest", "==================song details====="+ it.next());
			}
		}
	}

}
