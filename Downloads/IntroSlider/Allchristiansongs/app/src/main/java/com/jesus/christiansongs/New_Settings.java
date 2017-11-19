package com.jesus.christiansongs;

import com.jesus.christiansongs.player.MyDebug;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class New_Settings extends Activity {
	public static String[] langs = { "Telugu", "English", "Hindi", "Malayalam",
			"Gujarathi", "Kannada" };
	SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LinearLayout layout = new LinearLayout(this);
		layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		layout.setOrientation(1);
		setContentView(layout);
		sharedPreferences = getSharedPreferences(
				getString(R.string.pref_visual_settings), 0);
		for (int i = 0; i < langs.length; i++) {
			TextView tv = new TextView(this);
			tv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					MyDebug.i("song name","..........................................."
									+ (String) ((TextView) v).getText());
					Editor edit = sharedPreferences.edit();
					edit.putString(getString(R.string.pref_visual_settings),
							(String) ((TextView) v).getText());
					edit.commit();
				}
			});
			tv.setText(langs[i]);
			layout.addView(tv);
		}
	}
}
