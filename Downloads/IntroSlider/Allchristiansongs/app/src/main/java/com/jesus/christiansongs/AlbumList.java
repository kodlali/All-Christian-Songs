package com.jesus.christiansongs;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
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
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.jesus.christiansongs.player.Dataengine;
import com.jesus.christiansongs.player.MyDebug;
import com.jesus.christiansongs.player.Playbackservice;
import com.jesus.christiansongs.player.PlayerActivity;

import static com.jesus.christiansongs.R.layout.player;

public class AlbumList extends ListActivity implements OnEditorActionListener {
	
	MyListAdapter mLAdapter;
	ListView mList;
	//ImageView player;
	MinistryEngine ministry_Engine_obj;
	
	//Button playAll;
	//private EditText seartch = null;

	private AdView adView;
	private AdRequest request;
	ArrayList<String> values,base_values;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//Remove title bar
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		ministry_Engine_obj = new MinistryEngine();
		setContentView(R.layout.listclass);
	
		Bundle bundle = getIntent().getExtras();
		if(bundle != null){
			MyDebug.selectedPoisition = bundle.getInt("position");
		}
		
		values = new ArrayList<String>(MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).getAlbums());
        base_values= new ArrayList<String>(MyDebug.minstryVect.elementAt(MyDebug.selectedPoisition).getAlbums());
		
		
		/*playAll =(Button) findViewById(R.id.playall);
        playAll.setText("Back");
        playAll.setOnClickListener(listener_playall);
		
		player = (ImageView) findViewById(R.id.player);
		player.setOnClickListener(playerListener);*/
		
		//TextView title = (TextView) findViewById(R.id.title);
        //title.setText(bundle.getString("ministry_name"));
		
		mLAdapter = new MyListAdapter(this);
        mList = (ListView) findViewById(android.R.id.list);
        mList.setAdapter(mLAdapter);
		// getListView().setBackgroundResource(R.drawable.bg6);
		mList.setCacheColorHint(Color.TRANSPARENT);
        
        mList.setOnItemClickListener(listener1);
        
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
		
		/*seartch = (EditText) findViewById(R.id.search);
		seartch.addTextChangedListener(new MyTextWater());
		seartch.setOnEditorActionListener(this);*/
	
	}
	
	
OnClickListener listener_playall = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	OnClickListener playerListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (Playbackservice.hasInstance()) {
				Playbackservice serv = Playbackservice.get(AlbumList.this);
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
				Toast.makeText(getApplicationContext(), "Player Not Started",Toast.LENGTH_SHORT).show();
			}

		
		}
	};
	
	 OnItemClickListener listener1 = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position,
					long arg3) {
				Intent albumIntent = new Intent(getApplicationContext(), SongsList.class);
				//albumIntent.putExtra("position",position);
				String itemName = ((TextView) view.findViewById(R.id.list_item_label)).getText().toString();
				albumIntent.putExtra("position",base_values.indexOf(itemName));
				albumIntent.putExtra("album_name",itemName);
				startActivity(albumIntent);
			}
		};
		
		public class MyListAdapter extends BaseAdapter implements Filterable{
	    	LayoutInflater mInflater= null;
	    	public MyListAdapter(Context context) {
	    		mInflater = LayoutInflater.from(context);
			}
	    	
			@Override
			public int getCount() {
				return values.size();
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

					convertView = mInflater.inflate(R.layout.list, null);
					holder.textView = (TextView) convertView.findViewById(R.id.list_item_label);
					// set image based on selected text
					holder.imageView = (ImageView) convertView.findViewById(R.id.list_item_image);

					convertView.setTag(holder);
				} else {
					holder = (ViewHolder) convertView.getTag();

				}

				holder.textView.setText(values.get(position));  //ministry_Engine_obj.getAlbums().get(position).toString()
				holder.textView.setTextColor(Color.parseColor("#424242"));
				//Typeface tf = Typeface.createFromAsset(getAssets(),"Oxygen-Bold.ttf");
				//holder.textView.setTypeface(tf);
				holder.imageView.setImageResource(R.drawable.folder_image);
				
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

		@Override
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



		class MyTextWater implements TextWatcher {

			
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
