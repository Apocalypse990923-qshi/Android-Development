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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import androidx.navigation.Navigation;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.eventfinder.EventActivity;
import com.example.eventfinder.R;
import com.example.eventfinder.Adapter.AutoCompleteAdapter;
import com.example.eventfinder.Adapter.RecycleViewAdapter;
import com.example.eventfinder.util.ApiCall;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventsFragment extends Fragment implements AdapterView.OnItemSelectedListener,RecycleViewAdapter.OnEventClickListener, RecycleViewAdapter.OnHeartButtonClickListener{
    private Context context;
    private ConstraintLayout constraintLayout;
    private Button backButton;
    private JSONObject events_result;
    private RecyclerView recyclerView;
    private ConstraintLayout noResult;
    private ProgressBar progressBar;
    private RecycleViewAdapter adapter;
    public EventsFragment(){}
    /*public EventsFragment(Context context){
        this.context=context;
    }*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_events, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        constraintLayout = view.findViewById(R.id.ResultsLayout);
        backButton = view.findViewById(R.id.backButton); // bind submit button
        noResult = view.findViewById(R.id.noResults);
        //recyclerView = view.findViewById(R.id.recycleList);
        recyclerView = view.findViewById(R.id.resultList);
        progressBar = view.findViewById(R.id.progressBar);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setVisibility(View.GONE);
        noResult.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_EventsFragment_to_SearchFragment);
            }
        });
        boolean hasResult=false;
        if (getArguments() != null) {
            String eventsResultString = getArguments().getString("events_result");
            try {
                events_result = new JSONObject(eventsResultString);
                //if(events_result.getJSONObject("page").getInt("totalElements")>0){
                if(events_result.getInt("events_num")>0){
                    hasResult=true;
                    //noResult.setVisibility(View.GONE);

                    adapter = new RecycleViewAdapter(getContext(), parseData(events_result), this,this);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    //progressBar.setVisibility(View.GONE);
                    //recyclerView.setVisibility(View.VISIBLE);
                //}else{
                    //noResult.setVisibility(View.VISIBLE);
                    //progressBar.setVisibility(View.GONE);
                    //recyclerView.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(hasResult){
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    noResult.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }, 1000);
        }else{
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    noResult.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }, 1000);
        }
    }

    private JSONArray parseData(JSONObject events_result){
        JSONArray ret_array = new JSONArray();
        try{
            //JSONArray temp_array = events_result.getJSONObject("_embedded").getJSONArray("events");
            JSONArray temp_array = events_result.getJSONArray("events");
            List<JSONObject> temp_list = new ArrayList<>();
            for (int i = 0; i < temp_array.length(); i++) {
                JSONObject obj = new JSONObject();
                /*
                obj.put("eventName",temp_array.getJSONObject(i).getString("name"));
                obj.put("venueName",temp_array.getJSONObject(i).getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name"));
                obj.put("genre",temp_array.getJSONObject(i).getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name"));
                obj.put("date",temp_array.getJSONObject(i).getJSONObject("dates").getJSONObject("start").getString("localDate"));
                obj.put("time",temp_array.getJSONObject(i).getJSONObject("dates").getJSONObject("start").getString("localTime"));
                obj.put("id",temp_array.getJSONObject(i).getString("id"));
                obj.put("img",temp_array.getJSONObject(i).getJSONArray("images").getJSONObject(0).getString("url"));
                 */
                obj.put("eventName",events_result.getJSONArray("events").getJSONObject(i).getString("name"));
                obj.put("venueName",events_result.getJSONArray("events").getJSONObject(i).getString("venue"));
                obj.put("genre",events_result.getJSONArray("events").getJSONObject(i).getString("genre"));
                obj.put("date",events_result.getJSONArray("events").getJSONObject(i).getString("localDate"));
                obj.put("time",events_result.getJSONArray("events").getJSONObject(i).getString("localTime"));
                obj.put("id",events_result.getJSONArray("events").getJSONObject(i).getString("id"));
                obj.put("img",events_result.getJSONArray("events").getJSONObject(i).getString("image_url"));
                //ret_array.put(obj);
                temp_list.add(obj);
            }
            //sort
            Collections.sort(temp_list, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    String date1 = o1.optString("date");
                    String date2 = o2.optString("date");
                    if(date1.compareTo(date2)==0){
                        String time1 = o1.optString("time");
                        String time2 = o2.optString("time");
                        return time1.compareTo(time2);
                    }
                    return date1.compareTo(date2);
                }
            });
            for (JSONObject jsonObject : temp_list) {
                ret_array.put(jsonObject);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        return ret_array;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
                    //navigate to detail activity
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
        if(flag){
            //Toast.makeText(getContext(), name+" added to favorites", Toast.LENGTH_SHORT).show();
            Snackbar.make(constraintLayout, name+" added to favorites", Snackbar.LENGTH_LONG).show();
        }else{
            //Toast.makeText(getContext(), name+" removed from favorites", Toast.LENGTH_SHORT).show();
            Snackbar.make(constraintLayout, name+" removed from favorites", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.updateHeartButtons();
        }
    }


}
