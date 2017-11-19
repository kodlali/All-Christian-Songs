package com.jesus.christiansongs;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.jesus.christiansongs.player.Dataengine;

public class Settings extends PreferenceActivity {
	private AdView adView;
	private AdRequest request;
	ListPreference listPreference;
	SharedPreferences sharedPreferences;
	
	public void onCreate(Bundle b) {
		super.onCreate(b);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		addPreferencesFromResource(R.xml.add_settings);
		setContentView(R.layout.preference);	
		
		sharedPreferences = getSharedPreferences(getString(R.string.pref_visual_settings),0);
		
		
		listPreference = (ListPreference) getPreferenceScreen().findPreference("language");
		listPreference.setOnPreferenceChangeListener(onPreferenceChangeListener);
		
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.LL_GoogleAdM2);
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
		
	}
	
	
	OnPreferenceChangeListener onPreferenceChangeListener = new OnPreferenceChangeListener() {
		
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			Editor edit=sharedPreferences.edit();
			edit.putString(getString(R.string.pref_visual_settings), newValue.toString());
			edit.commit();
			Intent data = new Intent();
			if (getParent() == null) {
			    setResult(Activity.RESULT_FIRST_USER, data);
			} else {
			    getParent().setResult(Activity.RESULT_CANCELED, data);
			}
			finish();
			return true;
		}
	};
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(adView!=null)
			adView.destroy();
	}
	
}
