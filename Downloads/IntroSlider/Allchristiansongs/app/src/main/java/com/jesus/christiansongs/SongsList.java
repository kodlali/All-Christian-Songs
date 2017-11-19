package com.jesus.christiansongs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.jesus.christiansongs.player.Dataengine;
import com.jesus.christiansongs.player.MyDebug;
import com.jesus.christiansongs.player.Playbackservice;
import com.jesus.christiansongs.player.PlayerActivity;

import static com.jesus.christiansongs.R.drawable.playall;

public class SongsList extends ListActivity /*implements OnEditorActionListener*/{
	
	String TAG = SongsList.class.getName();
	MyListAdapter mLAdapter;
	ListView mList;
	int albumPosition;
	String name;
	//ImageView showPlayer;
	//Button playall;
	MinistryEngine ministry_Engine_obj;
	//private EditText seartch = null;
	
	private AdView adView;
	private AdRequest request;
	ArrayList<String> values,base_values;
	Vector<SongInfo> songsVector ;
	int songPosition;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//Remove title bar
		values=new ArrayList<String>();
		base_values=new ArrayList<String>();
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getSupportActionBar().setTitle(R.string.app_name);
		setContentView(R.layout.listclass);
		ministry_Engine_obj = new MinistryEngine();
		
		/*playall = (Button) findViewById(R.id.playall);
		playall.setOnClickListener(playAllListener);
		playall.setVisibility(View.VISIBLE);
		playall.setText("PlayAll");
		
		seartch = (EditText) findViewById(R.id.search);
		seartch.addTextChangedListener(new MyTextWater());
		//seartch.setOnEditorActionListener(this);
		
		showPlayer = (ImageView) findViewById(R.id.player);
		showPlayer.setOnClickListener(showlistener);*/
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			albumPosition = bundle.getInt("position");
			name = bundle.getString("album_name");
		}
		
		//TextView title = (TextView) findViewById(R.id.title);
		//title.setText(name);
		
		mLAdapter = new MyListAdapter(this);
        mList = (ListView) findViewById(android.R.id.list);
        try{
        	songsVector = MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).getAlbumSongs().get(name);
	        //if(MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).getSongNames().size() >0){
	        for(int i=0;i<songsVector.size();i++)
		        {
		        values.add(songsVector.get(i).getName());
		        base_values.add(songsVector.get(i).getName().toString());
		        }
	        mList.setAdapter(mLAdapter);
			// getListView().setBackgroundResource(R.drawable.bg6);
			mList.setCacheColorHint(Color.TRANSPARENT);
	        mList.setOnItemClickListener(listener_item);
	        
	        // Showing AD
			LinearLayout layout = (LinearLayout) findViewById(R.id.ll_adLayout);
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
	        /*}else{
        	  Toast.makeText(this,"No songs in this album",Toast.LENGTH_SHORT).show();
	        }*/
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	OnClickListener showlistener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (Playbackservice.hasInstance()) {
				Playbackservice serv = Playbackservice.get(SongsList.this);
				if (serv.ispalying()) {
					Intent ints = new Intent(getApplicationContext(),
							PlayerActivity.class);
					ints.putExtra("playerposition", "notstop");
					startActivity(ints);
				} else {
					Toast.makeText(getApplicationContext(),
							"Player Not Started", Toast.LENGTH_SHORT).show();

				}

			} else {
				Toast.makeText(getApplicationContext(), "Player Not Started",
						Toast.LENGTH_SHORT).show();
			}

		}
	};
	
	OnClickListener playAllListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			
			try{
			Dataengine.songlist.clear();
			for (int i = 0; i < songsVector.size(); i++) {
				
				HashMap<String, String> song = new HashMap<String, String>();
				String ss = songsVector.get(i).getUrl();
				//ss = ss.replace(" ", "%20");
				song.put("songpath", ss);
				song.put("songname", songsVector.get(i).getName());  
				song.put("moviename",MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).ministryName);
				Dataengine.songlist.add(song);
			}
			Intent ints = new Intent(getApplicationContext(), PlayerActivity.class);
			ints.putExtra("playerposition", "stop");
			startActivity(ints);
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			}
	};
	
	OnItemClickListener listener_item = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view,int position,long long_value) {
			
			String itemName = ((TextView) view.findViewById(R.id.list_item_label)).getText().toString();
			songPosition = base_values.indexOf(itemName);
			
			AlertDialog.Builder builder = new AlertDialog.Builder(SongsList.this);
			
			builder.setItems(R.array.array,new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			try{
				if (which == 0) {
					Dataengine.songlist.clear();
					// songsVector = MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).getAlbumSongs().get(name);
					
					String ss = songsVector.get(songPosition).getUrl();
					HashMap<String, String> song1 = new HashMap<String, String>();
					song1.put("songpath", ss);
					song1.put("songname", songsVector.get(songPosition).getName());  //listadapter.get(clickposition).getFilename()
					song1.put("moviename",MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).ministryName);  //listadapter.get(clickposition).getMoviename()
					
					Dataengine.songlist.add(song1);
					
					Intent ints = new Intent(SongsList.this,PlayerActivity.class);
					ints.putExtra("playerposition", "stop");
					startActivity(ints);

				} else if (which == 1) {

					if (null != Playbackservice.sInstance&&Playbackservice.sInstance.ispalying()) {
						String ss = songsVector.get(songPosition).getUrl();
						HashMap<String, String> song1 = new HashMap<String, String>();
						song1.put("songpath", ss);
						song1.put("songname", songsVector.get(songPosition).getName());  //listadapter.get(clickposition).getFilename()
						song1.put("moviename",MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).ministryName);  //listadapter.get(clickposition).getMoviename()
						
						Playbackservice.slist.add(song1);
						Toast.makeText(SongsList.this,
								songsVector.get(songPosition).getName()+"  Added to play list",
								Toast.LENGTH_SHORT).show();
					}else{								
						Dataengine.songlist.clear();
						String ss = songsVector.get(songPosition).getUrl();
						HashMap<String, String> song1 = new HashMap<String, String>();
						song1.put("songpath", ss);
						song1.put("songname", songsVector.get(songPosition).getName());  //listadapter.get(clickposition).getFilename()
						song1.put("moviename",MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).ministryName);  //listadapter.get(clickposition).getMoviename()
						Dataengine.songlist.add(song1);

					Intent ints = new Intent(SongsList.this,PlayerActivity.class);
						ints.putExtra("playerposition", "stop");
						startActivity(ints);
					}

					
				} else if (which == 2) {

					if (null != Playbackservice.sInstance&&Playbackservice.sInstance.ispalying()) {
						
						for (int i = 0; i < songsVector.size(); i++) {
							String ss = songsVector.get(i).getUrl();
							HashMap<String, String> song1 = new HashMap<String, String>();
							song1.put("songpath", ss);
							song1.put("songname", songsVector.get(i).getName());  //listadapter.get(clickposition).getFilename()
							song1.put("moviename",MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).ministryName);  //listadapter.get(clickposition).getMoviename()
							
							Playbackservice.slist.add(song1);
							
							
						}
						
						Toast.makeText(SongsList.this,
								MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).getAlbums().get(songPosition).toString()+"  Songs Added to play list",
								Toast.LENGTH_SHORT).show();
						
						
					}else{
						
						Dataengine.songlist.clear();
						for (int i = 0; i < songsVector.size(); i++) {
							String ss = songsVector.get(i).getUrl();
							HashMap<String, String> song1 = new HashMap<String, String>();
							song1.put("songpath", ss);
							song1.put("songname", songsVector.get(i).getName());  //listadapter.get(clickposition).getFilename()
							song1.put("moviename",MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).ministryName);  //listadapter.get(clickposition).getMoviename()
							
							Dataengine.songlist.add(song1);
						}
						Intent ints = new Intent(SongsList.this, PlayerActivity.class);
						ints.putExtra("playerposition", "stop");
						startActivity(ints);
					}
					
					
				}else if (which == 3){
					Intent downoad=new Intent(getApplicationContext(),MainActivity.class);
					downoad.putExtra("Path",songsVector.get(songPosition).getUrl());
					downoad.putExtra("songname",songsVector.get(songPosition).getName());
					downoad.putExtra("moviename",MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).ministryName);
					startActivity(downoad);
				}
				}catch(Exception e){
								e.printStackTrace();
							}
						}
					});
			AlertDialog dilaog = builder.create();
			dilaog.show();

		
		}
	};
	
	 /*OnItemClickListener listener1 = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				Dataengine.songlist.clear();

				String ss = MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).getAlbumSongs().get(name).get(position).getUrl();
				HashMap<String, String> song1 = new HashMap<String, String>();
				song1.put("songpath", ss);
				song1.put("songname", MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).getAlbumSongs().get(name).get(position).getName());  //listadapter.get(clickposition).getFilename()
				song1.put("moviename",MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).ministryName);  //listadapter.get(clickposition).getMoviename()
				Dataengine.songlist.add(song1);

				Intent ints = new Intent(
						getApplicationContext(),
						PlayerActivity.class);
				ints.putExtra("playerposition", "stop");
				startActivity(ints);

			}
		};*/
		
		public class MyListAdapter extends BaseAdapter implements Filterable{
	    	LayoutInflater mInflater= null;
	    	public MyListAdapter(Context context) {
	    		mInflater = LayoutInflater.from(context);
			}
	    	
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				try{
				return values.size();
				}catch (Exception e) {
					// TODO: handle exception
					
					e.printStackTrace();
					return 0;
				}
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				ViewHolder holder;

				if (convertView == null) {
					holder = new ViewHolder();

					convertView = mInflater.inflate(R.layout.list, null);
					holder.textView = (TextView) convertView.findViewById(R.id.list_item_label);
					// set image based on selected text
					holder.imageView = (ImageView) convertView.findViewById(R.id.list_item_image);

					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();

				}
				
				holder.textView.setText(values.get(position));
				
				holder.textView.setTextColor(Color.parseColor("#424242"));
				//Typeface tf = Typeface.createFromAsset(getAssets(),"Oxygen-Regular.ttf");
				//holder.textView.setTypeface(tf);
				//MyDebug.i("song name","==========================="+MyDebug.minstry_vect.elementAt(MyDebug.pos).getAlbumSongs().get(album_name).get(position).getName());
				holder.imageView.setImageResource(R.drawable.music_symbol);
				
				if ((getApplicationContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
					holder.textView.setTextSize(24);
				} else if ((getApplicationContext().getResources()
						.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE) {
					holder.textView.setTextSize(30);
				}

				return convertView;

			}
			
			@Override
			public Filter getFilter() {
				// TODO Auto-generated method stub
				return new SecretsFilter();
			}
	    	
	    }
	    
	    class ViewHolder {
	    	TextView textView;
	    	ImageView imageView;
	    }

		  private class SecretsFilter extends Filter {
		    @Override
		    // TODO(rogerta): the clone() method does not support generics.
		    @SuppressWarnings("unchecked")
		    protected FilterResults performFiltering(CharSequence prefix) {
		    	
		    	 FilterResults results = new FilterResults();
		    	
		            if (prefix == null || prefix.length() == 0) {
		                synchronized (values) {
		                    ArrayList<String> list = new ArrayList<String>();
		                    list.addAll(base_values);
		                    results.values = list;
		                    results.count = list.size();
		                }
		            } else {
		                String prefixString = prefix.toString().toLowerCase();
		                final int count = values.size();
		                System.out.println("the number of records getting here is............."+count);
		                final ArrayList<String> newValues = new ArrayList<String>(count);
		                
		                for (int i = 0; i < count; i++) {
		                    final String value = values.get(i);
		                    final String valueText = value.toString().toLowerCase();

		                    // First match against the whole, non-splitted value
		                    if (valueText.startsWith(prefixString)) {
		                        newValues.add(value);
		                        System.out.println("the value added here is"+value);
		                    } else {
		                        final String[] words = valueText.split(" ");
		                        final int wordCount = words.length;

		                        for (int k = 0; k < wordCount; k++) {
		                            if (words[k].startsWith(prefixString)) {
		                                newValues.add(value);
				                        System.out.println("the value added here is"+value);

		                                break;
		                            }
		                        }
		                    }
		                }

		                results.values = newValues;
		                results.count = newValues.size();
		            }

		            return results;
		    }

		    @SuppressWarnings("unchecked")
		    @Override
		    protected void publishResults(CharSequence prefix,
		                                  FilterResults results) {
		      
		    	//noinspection unchecked
                System.out.println("=============================="+results.count);

	            values = (ArrayList<String>) results.values;
	            if (results.count > 0) {
	            	mLAdapter.notifyDataSetChanged();
	            } else {
	            	mLAdapter.notifyDataSetInvalidated();
	            }
		    	
		    }
		  }

		private String getTextFilter() {
			/*if (seartch != null) {
				if (null != seartch.getText())
					return seartch.getText().toString();
				else
					return "";
			}*/
			return null;
		}

		private void hideSoftKeyboard() {
			// Hide soft keyboard, if visible
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(mList.getWindowToken(), 0);
		}

		protected void onSearchTextChanged() {
			// Set the proper empty string
			try {
				Filter filter = mLAdapter.getFilter();
				filter.filter(getTextFilter());
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

		/*
		 * 
		 * @Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

			// TODO Auto-generated method stub

			if (actionId == EditorInfo.IME_ACTION_DONE) {
				hideSoftKeyboard();
				if (TextUtils.isEmpty(getTextFilter())) {
					finish();
				}
				return true;
			}
			return false;

		} 
		
		*/


		class MyTextWater implements TextWatcher {
			
			int textlength;
			Vector <String> array_sort;
			
			public MyTextWater() {
				// TODO Auto-generated constructor stub
				// MainActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				onSearchTextChanged();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
				int count) {
				
			}

		}
}
