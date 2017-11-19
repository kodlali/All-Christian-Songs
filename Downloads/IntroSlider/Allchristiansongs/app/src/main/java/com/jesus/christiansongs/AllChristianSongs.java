package com.jesus.christiansongs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

public class AllChristianSongs extends AppCompatActivity implements OnEditorActionListener {

    private static final String TAG = AllChristianSongs.class.getName();
    private static final int DIALOG_WORK_PROG = 3333;
    MyListAdapter mLAdapter;
    ListView mList;
    //ImageView player;
    MinistryEngine ministry_Engine_obj;
    TextView title;
    SharedPreferences sharedPreferences = null;
    ListPreference listPreference;
    String selectedLanguage;
    int value;
    //Button playAll;
    ArrayList<String> values, baseValues;
    ProgressDialog progressDialog = null;

    private AdView adView;
    private AdRequest adRequest;
    //private EditText seartch = null;
    private String[] languages_array;

    ProgressDialog workProgress_UP = null;
    private Handler my_UI_Handler_Channels = new Handler();
    boolean m_prgisShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        languages_array = getResources().getStringArray(R.array.languages_array);

        MyDebug.minstryVect = new Vector<MinistryEngine>();
        ministry_Engine_obj = new MinistryEngine();

        new ProgressTask().execute();
        //my_UI_Handler_Channels.post(Show_Progress_Bar);
        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.listclass);

        /*title = (TextView) findViewById(R.id.title);
        title.setText("Change Language");
        title.setOnClickListener(languageChangeListener);*/

        /*playAll = (Button) findViewById(R.id.playall);
        playAll.setText("Exit");
        playAll.setOnClickListener(listener_playall);

        player = (ImageView) findViewById(R.id.player);
        player.setOnClickListener(playerListener);*/

        // Showing AD
        LinearLayout layout = (LinearLayout) findViewById(R.id.ll_adLayout);
        // AdView adView =(AdView) findViewById(R.id.adview);
        //adView = new AdView(this, AdSize.BANNER, Dataengine.MY_BANNER_UNIT_ID);

        // Create an ad.
        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(Dataengine.AD_UNIT_ID);
        layout.addView(adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device.
        adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);

        /*seartch = (EditText) findViewById(R.id.search);
        seartch.addTextChangedListener(new MyTextWater());
        seartch.setOnEditorActionListener(this);*/

    }


    protected void sharedPreferencesChecking() {

        if ("none" == sharedPreferences.getString(getString(R.string.pref_visual_settings), "none")) {
            AlertDialogDisplay();
        } else {
            selectedLanguage = sharedPreferences.getString(getString(R.string.pref_visual_settings), "English");
            dataParsing(Arrays.asList(languages_array).indexOf(selectedLanguage));
            //new LongProcess().onPostExecute(Integer.valueOf(Arrays.asList(languages_array).indexOf(selectedLanguage)));
        }

    }

    private void dataParsing(int s) {
        if (isNetworkOnline()) {
            Log.d("Language Vector name : ", s + "");
            Log.d("Ministry Vector size : ", MyDebug.minstryVect.size() + "");
            new RetrieveFeed().execute(new Integer(s));
            //setToAdapter();
        } else {
        }
    }

    public class RetrieveFeed extends AsyncTask <Integer, Integer, Void >{

        @Override
        protected Void doInBackground(Integer...position) {
            try {
                int post = position[0].intValue();
                new ParsingXML().parsingDatak(post);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setToAdapter();
        }
    }


    public void setToAdapter() {
        values = new ArrayList<String>();
        baseValues = new ArrayList<String>();
        if (MyDebug.minstryVect.size() > 0) {
            for (int i = 0; i < MyDebug.minstryVect.size(); i++) {
                Log.d("kodali", "My Debug Ministry Vector not Zeroooooo");
                values.add(MyDebug.minstryVect.elementAt(i).ministryName);
                baseValues.add(MyDebug.minstryVect.elementAt(i).ministryName);
            }
        } else {
            Log.d("kodali", "My Debug Ministry Vector Zeroooooo :::: ");
        }
        mLAdapter = new MyListAdapter(this);
        mList = (ListView) findViewById(android.R.id.list);
        mList.setAdapter(mLAdapter);
        mList.setCacheColorHint(Color.TRANSPARENT);
        mList.setOnItemClickListener(listener1);
        //mLAdapter.notifyDataSetChanged();
        //my_UI_Handler_Channels.post(Cancel_Progress_Bar);
    }


    public boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }


    OnClickListener listener_playall = new OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
        }
    };


    private void AlertDialogDisplay() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pref_visual_settings);
        builder.setItems(languages_array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Do something with the selection
                //new LongProcess().execute(item);
                Editor editor = sharedPreferences.edit();
                editor.putString(getString(R.string.pref_visual_settings), languages_array[item]);
                editor.commit();
                dialog.dismiss();
                dataParsing(item);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressLint("NewApi")
    private void popupMenuDisplay(View v) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(AllChristianSongs.this, v);
        //Inflating the Popup using xml file  
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener  
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(AllChristianSongs.this, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        popup.show();//showing popup menu  

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_FIRST_USER) {
                value = data.getIntExtra("language", RESULT_FIRST_USER);
                Toast.makeText(this, "You have chosen the language: " + value, Toast.LENGTH_LONG).show();
                //title.setText(name);
            }

        }
        ;
    }

    OnClickListener playerListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (Playbackservice.hasInstance()) {
                Playbackservice serv = Playbackservice.get(AllChristianSongs.this);
                if (serv.ispalying()) {
                    Intent ints = new Intent(getApplicationContext(), PlayerActivity.class);
                    ints.putExtra("playerposition", "notstop");
                    startActivity(ints);
                } else {
                    Toast.makeText(getApplicationContext(), "Player Yet to start", Toast.LENGTH_SHORT).show();

                }

            } else {
                Toast.makeText(getApplicationContext(), "Player Yet to start", Toast.LENGTH_SHORT).show();
            }


        }
    };

    OnItemClickListener listener1 = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position,
                                long arg3) {
            try {
                Intent albumIntent = new Intent(getApplicationContext(), AlbumList.class);
                String itemName = ((TextView) view.findViewById(R.id.list_item_label)).getText().toString();
                albumIntent.putExtra("position", baseValues.indexOf(itemName));
                albumIntent.putExtra("ministry_name", itemName);
                startActivity(albumIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public class MyListAdapter extends BaseAdapter implements Filterable {
        LayoutInflater mInflater = null;

        public MyListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            try {
                return values.size();
            } catch (Exception e) {
                e.printStackTrace();
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
            //Typeface tf = Typeface.createFromAsset(getAssets(), "Oxygen-Bold.ttf");
            //holder.textView.setTypeface(tf);

            holder.imageView.setImageResource(R.drawable.folder_image);

            if ((getApplicationContext().getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
                holder.textView.setTextSize(18);
            } else if ((getApplicationContext().getResources()
                    .getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                holder.textView.setTextSize(26);
            }

            return convertView;

        }

        @Override
        public Filter getFilter() {
            return new SecretsFilter();
        }

    }

    class ViewHolder {
        TextView textView;
        ImageView imageView;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
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
                    list.addAll(baseValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                String prefixString = prefix.toString().toLowerCase();
                final int count = values.size();
                final ArrayList<String> newValues = new ArrayList<String>(count);

                for (int i = 0; i < count; i++) {
                    final String value = values.get(i);
                    final String valueText = value.toString().toLowerCase();

                    // First match against the whole, non-splitted value
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                        System.out.println("the value added here is" + value);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                System.out.println("the value added here is" + value);

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
                try {
                    mLAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } /*else {
              mLAdapter.notifyDataSetInvalidated();
          }*/

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
        //inputMethodManager.hideSoftInputFromWindow(seartch.getWindowToken(), 0);
    }

    protected void onSearchTextChanged() {
        // Set the proper empty string
        try {
            Filter filter = mLAdapter.getFilter();
            filter.filter(getTextFilter());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

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
            // MainActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        }

        @Override
        public void afterTextChanged(Editable s) {
            onSearchTextChanged();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

    }

    private class ProgressTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
    		/* progressDialog = ProgressDialog.show(AllChristianSongs.this, "",
    			"Please wait while we load data from the server");*/
            showDialog(DIALOG_WORK_PROG);
        }

        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    removeDialog(DIALOG_WORK_PROG);
                    sharedPreferencesChecking();
                }
            });

            super.onPostExecute(result);
			
			/*if(progressDialog!=null)
				progressDialog.dismiss();*/
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        // mDialog2 = null;
        progressDialog = null;
    }

    // Progress Bar Task ...
    final Runnable Show_Progress_Bar = new Runnable() {
        public void run() {
            progress_Show();
        }
    };

    @Override
    protected Dialog onCreateDialog(int dialogID) {
        switch (dialogID) {
            case DIALOG_WORK_PROG: {

                workProgress_UP = new ProgressDialog(this);
                workProgress_UP.setMessage("Loading...");
                workProgress_UP.setIndeterminate(true);
                workProgress_UP.setCancelable(true);
                return workProgress_UP;
            }
        }
        return null;
    }

    private void progress_Show() {
        showDialog(DIALOG_WORK_PROG);
    }

    final Runnable Cancel_Progress_Bar = new Runnable() {
        public void run() {
            progress_Stop();
        }
    };

    private void progress_Stop() {
        try {
            if (null != workProgress_UP) {
                workProgress_UP.dismiss();
            }
        } catch (Exception e) {
            //MyDebug.e(e);
            e.printStackTrace();
        }
    }


}
