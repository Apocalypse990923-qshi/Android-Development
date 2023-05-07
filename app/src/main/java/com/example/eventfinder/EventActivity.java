package com.example.eventfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.eventfinder.Fragment.ArtistFragment;
import com.example.eventfinder.Fragment.DetailFragment;
import com.example.eventfinder.Fragment.VenueFragment;
import com.example.eventfinder.util.ApiCall;
import com.google.android.material.snackbar.Snackbar;
import androidx.constraintlayout.widget.ConstraintLayout;
/*
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.hw9.Adapters.AutoSuggestAdapter;
import com.example.hw9.Adapters.RecycleViewAdapter;
import com.example.hw9.utils.ApiCall;
*/
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import androidx.appcompat.app.ActionBar;
import com.example.eventfinder.Adapter.ViewPager2Adapter;
import com.example.eventfinder.util.SharedPrefUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class EventActivity extends AppCompatActivity implements TabLayoutMediator.TabConfigurationStrategy{
    private ConstraintLayout constraintLayout;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout2;
    private ArrayList<String> titles;
    private SharedPreferences pref;
    private JSONObject eventInfo;
    private JSONObject venueInfo;
    private int[] tabIcons;
    private boolean hasMusician;
    private JSONArray musicians;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        pref = SharedPrefUtil.getSharedPrefs(getApplicationContext());
        ActionBar actionBar = getSupportActionBar();
        constraintLayout = findViewById(R.id.EventActivityLayout);
        viewPager2 = findViewById(R.id.viewPager2);
        tabLayout2 = findViewById(R.id.tabLayout2);
        titles = new ArrayList<>();
        titles.add("DETAILS");
        titles.add("ARTIST(S)");
        titles.add("VENUE");
        tabIcons = new int[] {
                R.drawable.info_icon,
                R.drawable.artist_icon,
                R.drawable.venue_icon
        };
        hasMusician=false;
        musicians=new JSONArray();
        try {
            eventInfo = new JSONObject(getIntent().getStringExtra("event"));
            venueInfo = eventInfo.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0);
            isFavorite = pref.contains(eventInfo.getString("id"));
            hasMusician = search_musician(eventInfo.getJSONObject("_embedded").getJSONArray("attractions"));

            //set title
            //actionBar.setTitle(eventInfo.getString("name"));
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowCustomEnabled(true);
            LayoutInflater inflater = LayoutInflater.from(this);
            View customTitleView = inflater.inflate(R.layout.custom_action_bar_title, null);
            actionBar.setCustomView(customTitleView);
            TextView actionBarTitle = findViewById(R.id.action_bar_title);
            actionBarTitle.setText(eventInfo.getString("name"));
            actionBarTitle.setSelected(true); // Start the marquee effect
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setViewPagerAdapter();
        new TabLayoutMediator(tabLayout2, viewPager2,this).attach();
        tabLayout2.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabTitle = tab.getCustomView().findViewById(R.id.tab_title);
                ImageView tabIcon = tab.getCustomView().findViewById(R.id.tab_icon);
                tabTitle.setTextColor(ContextCompat.getColor(EventActivity.this, R.color.my_green));
                tabIcon.setColorFilter(ContextCompat.getColor(EventActivity.this, R.color.my_green));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabTitle = tab.getCustomView().findViewById(R.id.tab_title);
                ImageView tabIcon = tab.getCustomView().findViewById(R.id.tab_icon);
                tabTitle.setTextColor(ContextCompat.getColor(EventActivity.this, R.color.white));
                tabIcon.setColorFilter(ContextCompat.getColor(EventActivity.this, R.color.white));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //Log.d("get events",getIntent().getStringExtra("events"));

        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.green_back_btn);
    }

    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        //tab.setText(titles.get(position));
        //tab.setIcon(tabIcons[position]);
        View customView = LayoutInflater.from(this).inflate(R.layout.custom_tab_layout, null);
        ImageView tabIcon = customView.findViewById(R.id.tab_icon);
        TextView tabTitle = customView.findViewById(R.id.tab_title);

        tabIcon.setImageResource(tabIcons[position]); // Replace with your icon resource array
        tabTitle.setText(titles.get(position)); // Replace with your tab text array
        if(position==0){
            tabTitle.setTextColor(ContextCompat.getColor(this, R.color.my_green));
            tabIcon.setColorFilter(ContextCompat.getColor(this, R.color.my_green));
        }else{
            tabTitle.setTextColor(ContextCompat.getColor(this, R.color.white));
            tabIcon.setColorFilter(ContextCompat.getColor(this, R.color.white));
        }
        tab.setCustomView(customView);
    }

    public void setViewPagerAdapter() {
        // create fragments needed by viewPage2
        //SearchFragment searchFragment = new SearchFragment(this);
        //EventsFragment eventsFragment = new EventsFragment(this);
        //NavHostFragment navhostFragment  = new NavHostFragment();
        //FavoriteFragment favoriteFragment = new FavoriteFragment();
        DetailFragment detailFragment = new DetailFragment(eventInfo);
        ArtistFragment artistFragment = new ArtistFragment(hasMusician,musicians);
        VenueFragment venueFragment = new VenueFragment(venueInfo);
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(this);
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        //creates an ArrayList of Fragments
        //fragmentList.add(searchFragment);
        //fragmentList.add(navhostFragment);
        //fragmentList.add(favoriteFragment);
        fragmentList.add(detailFragment);
        fragmentList.add(artistFragment);
        fragmentList.add(venueFragment);
        viewPager2Adapter.setData(fragmentList); //sets the data for the adapter
        viewPager2.setAdapter(viewPager2Adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem heartButton = menu.findItem(R.id.HeartButton);
        if (isFavorite) {
            heartButton.setIcon(R.drawable.heart_fill);
        } else {
            heartButton.setIcon(R.drawable.heart_outline);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String url="";
        try {
            url = eventInfo.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String facebookURL = "https://www.facebook.com/sharer.php?u=";
        String twitterURL = "http://www.twitter.com/share?url=";
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.facebookButton:
                Log.d("share button", "facebook");
                Intent openFacebookURL = new Intent(Intent.ACTION_VIEW);
                openFacebookURL.setData(Uri.parse(facebookURL+url));
                startActivity(openFacebookURL);
                return true;
            case R.id.twitterButton:
                Log.d("share button","twitter");
                Intent openTwitterURL = new Intent(Intent.ACTION_VIEW);
                openTwitterURL.setData(Uri.parse(twitterURL+url));
                startActivity(openTwitterURL);
                return true;
            case R.id.HeartButton:
                Log.d("heart button","add event to favorite");
                String event_id = null;
                String event_name = null;
                try {
                    event_id = eventInfo.getString("id");
                    event_name = eventInfo.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SharedPreferences.Editor editor = pref.edit();
                if(isFavorite){
                    editor.remove(event_id).apply();
                    isFavorite=false;
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.heart_outline));
                    //Toast.makeText(this, event_name+" removed from favorites", Toast.LENGTH_SHORT).show();
                    Snackbar.make(constraintLayout, event_name+" removed from favorites", Snackbar.LENGTH_LONG).show();
                }else{
                    JSONObject value = new JSONObject();
                    try {
                        value.put("eventName",event_name);
                        value.put("venueName",eventInfo.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name"));
                        value.put("genre",eventInfo.getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name"));
                        value.put("date",eventInfo.getJSONObject("dates").getJSONObject("start").getString("localDate"));
                        value.put("time",eventInfo.getJSONObject("dates").getJSONObject("start").getString("localTime"));
                        value.put("id",event_id);
                        value.put("img",eventInfo.getJSONArray("images").getJSONObject(0).getString("url"));
                    } catch (JSONException e) {e.printStackTrace();}
                    editor.putString(event_id,value.toString());
                    editor.apply();
                    isFavorite=true;
                    item.setIcon(ContextCompat.getDrawable(this, R.drawable.heart_fill));
                    //Toast.makeText(this, event_name+" added to favorites", Toast.LENGTH_SHORT).show();
                    Snackbar.make(constraintLayout, event_name+" added to favorites", Snackbar.LENGTH_LONG).show();
                }
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean search_musician(JSONArray attractions){
        boolean flag=false;
        try{
            for(int i=0;i<attractions.length();i++){
                Log.d("attraction name", attractions.getJSONObject(i).getString("name"));
                Log.d("attraction type", attractions.getJSONObject(i).getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name"));
                if(attractions.getJSONObject(i).getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name").equals("Music")){
                    Log.d("start spotify function", "success!!!!!");
                    flag=true;
                    Spotify(attractions.getJSONObject(i).getString("name"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return flag;
    }
    private void Spotify(String musician){
        String spotifyUrl="https://cs571-hw8-379421.wl.r.appspot.com/artist?artist=";
        Log.d("Searching Musician on Spotify: ",musician);
        ApiCall.make(this, spotifyUrl+musician, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                Log.d("Spotify Response for ", musician+": "+response);
                try{
                    musicians.put(new JSONObject(response));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }
}
