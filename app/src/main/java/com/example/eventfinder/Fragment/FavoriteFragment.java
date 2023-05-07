package com.example.eventfinder.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eventfinder.EventActivity;
import com.google.android.material.snackbar.Snackbar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.eventfinder.Adapter.RecycleViewAdapter;
import com.example.eventfinder.R;
import com.example.eventfinder.util.ApiCall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import com.example.eventfinder.util.SharedPrefUtil;

public class FavoriteFragment extends Fragment implements RecycleViewAdapter.OnEventClickListener,RecycleViewAdapter.OnHeartButtonClickListener{
    private ConstraintLayout constraintLayout;
    private Context context;
    private JSONArray favoriteEvents;
    private RecyclerView recyclerView;
    private ConstraintLayout noFavorite;
    private ProgressBar progressBar;
    private RecycleViewAdapter adapter;
    private SharedPreferences pref;
    public FavoriteFragment(){}
    /*public FavoriteFragment(Context context){
        this.context=context;
    }*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = SharedPrefUtil.getSharedPrefs(getContext());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        constraintLayout = view.findViewById(R.id.FavoriteLayout);
        noFavorite = view.findViewById(R.id.noFavorite);
        recyclerView = view.findViewById(R.id.favoriteList);
        progressBar = view.findViewById(R.id.progressBar2);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        try {
                Map<String, ?> allEntries = pref.getAll();
                favoriteEvents = new JSONArray();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    String jsonString = entry.getValue().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        favoriteEvents.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(favoriteEvents.length()>0){
                    noFavorite.setVisibility(View.GONE);
                    adapter = new RecycleViewAdapter(getContext(), favoriteEvents, this,this);
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }else{
                    noFavorite.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
        } catch (Exception e) {
                e.printStackTrace();
        }
    }

    @Override
    public void onEventClick(int pos, String id){
        Log.d("Click Event",String.format("%d",pos));
        Log.d("Event id",id);
        String eventDetailUrl = "https://cs571-hw8-379421.wl.r.appspot.com/detail?event_id="+id;
        ApiCall.make(getContext(), eventDetailUrl,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try { // returned type is an array of business object
                    Log.d("event detail response", response);
                    // navigate to detail activity
                    Intent intent = new Intent(getContext(), EventActivity.class);
                    intent.putExtra("event",response);
                    startActivity(intent);
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
    @Override
    public void onHeartButtonClick(String name,int pos, boolean flag){
        assert flag == false;
        //Toast.makeText(getContext(), name+" removed from favorites", Toast.LENGTH_SHORT).show();
        Snackbar.make(constraintLayout, name+" removed from favorites", Snackbar.LENGTH_LONG).show();
        if(favoriteEvents.length()==1){
            recyclerView.setVisibility(View.GONE);
            noFavorite.setVisibility(View.VISIBLE);
        }
        adapter.removeItem(pos);
        recyclerView.setAdapter(adapter);
        favoriteEvents.remove(pos);
    }
    @Override
    public void onResume() {
        super.onResume();
        updateFavoriteEvents();
    }
    private void updateFavoriteEvents() {
        //pref = requireContext().getSharedPreferences("Favorites", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = pref.getAll();
        favoriteEvents = new JSONArray();

        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            try {
                JSONObject event = new JSONObject(entry.getValue().toString());
                favoriteEvents.put(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Assuming you have a method in your RecyclerView adapter to update the dataset
/*
        if(adapter!=null){
            Log.d("favoriteEvents changed!!!!!!!!!!",favoriteEvents.toString());
            adapter.updateDataSet(favoriteEvents);
            recyclerView.setAdapter(adapter);
        }
*/
        adapter = new RecycleViewAdapter(getContext(), favoriteEvents, this,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        if(favoriteEvents.length()==0){
            noFavorite.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            noFavorite.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
