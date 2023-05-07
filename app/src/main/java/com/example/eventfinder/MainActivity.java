package com.example.eventfinder;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
/*
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.hw9.Adapters.AutoSuggestAdapter;
import com.example.hw9.Adapters.RecycleViewAdapter;
import com.example.hw9.utils.ApiCall;
*/
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import androidx.appcompat.app.ActionBar;
import com.example.eventfinder.Adapter.ViewPager2Adapter;
import com.example.eventfinder.Fragment.NavHostFragment;
import com.example.eventfinder.Fragment.FavoriteFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity implements TabLayoutMediator.TabConfigurationStrategy{
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private ArrayList<String> titles;

    private JSONObject events;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestLocationPermission();

        ActionBar actionBar = getSupportActionBar();
        viewPager2 = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);
        titles = new ArrayList<>();
        titles.add("SEARCH");
        titles.add("FAVORITES");
        /*try {
            events = new JSONObject(getIntent().getStringExtra("events"));
            //actionBar.setTitle(events.getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
        setViewPagerAdapter();
        new TabLayoutMediator(tabLayout, viewPager2,this).attach();
        //Log.d("get events",getIntent().getStringExtra("events"));

        // showing the back button in action bar
        //actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = LayoutInflater.from(this);
        View customTitleView = inflater.inflate(R.layout.custom_action_bar_title, null);
        actionBar.setCustomView(customTitleView);
    }

    public void setViewPagerAdapter() {
        // create fragments needed by viewPage2
        //SearchFragment searchFragment = new SearchFragment(this);
        //EventsFragment eventsFragment = new EventsFragment(this);
        NavHostFragment navhostFragment  = new NavHostFragment();
        FavoriteFragment favoriteFragment = new FavoriteFragment();
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(this);
        ArrayList<Fragment> fragmentList = new ArrayList<>();
        //creates an ArrayList of Fragments
        //fragmentList.add(searchFragment);
        fragmentList.add(navhostFragment);
        fragmentList.add(favoriteFragment);
        viewPager2Adapter.setData(fragmentList); //sets the data for the adapter
        viewPager2.setAdapter(viewPager2Adapter);
    }
    @Override
    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
        tab.setText(titles.get(position));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        // first parameter is the file for icon and second one is menu
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    // function to the button on press

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, you can proceed with accessing the location
            Log.d("Permission", "granted");
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can proceed with accessing the location
                Log.d("Permission", "granted");
            } else {
                // Permission denied, you can show a message or disable the location-related functionality in your app
                Log.d("Permission", "not granted!!!");
            }
        }
    }


}
