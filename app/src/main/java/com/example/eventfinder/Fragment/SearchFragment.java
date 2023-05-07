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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import androidx.navigation.Navigation;
import android.os.Handler;
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
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;

import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.eventfinder.R;
import com.example.eventfinder.Adapter.AutoCompleteAdapter;
//import com.example.eventfinder.Adapter.RecycleViewAdapter;
import com.example.eventfinder.util.ApiCall;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    private Button searchButton;
    private Button clearButton;
    private EditText distance;
    private EditText location;
    private TableRow locationRow;
    private Spinner category;
    private String selectedCategory;
    private String selectedKeyword;
    private Switch autoDetect;
    private AutoCompleteTextView autoCompleteTextView;
    private AutoCompleteAdapter autocompleteadapter;
    private Context context;
    private Handler handler;
    private final String autoCompleteURL = "https://cs571-hw8-379421.wl.r.appspot.com/autocomplete?text=";
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private JSONObject events_result;
    public SearchFragment(){}
    /*public SearchFragment(Context context){
        this.context=context;
    }*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        selectedCategory = "All";
        category = view.findViewById(R.id.categorySpinner); // bind category dropdown
        autoCompleteTextView = view.findViewById(R.id.keyword);
        location = view.findViewById(R.id.location);
        locationRow = view.findViewById(R.id.locationInputRow);
        autoDetect = view.findViewById(R.id.autoDetect);
        distance = view.findViewById(R.id.distance);
        searchButton = view.findViewById(R.id.searchButton); // bind submit button
        clearButton = view.findViewById(R.id.clearButton);
        // set spinner options
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categoryOptions,
                R.layout.spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        category.setAdapter(spinnerAdapter);
        category.setOnItemSelectedListener(this);

        // set auto complete
        autocompleteadapter = new AutoCompleteAdapter(getContext(), android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setThreshold(2);
        autoCompleteTextView.setAdapter(autocompleteadapter);
        autoCompleteTextView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    // to be used
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectedKeyword = parent.getItemAtPosition(position).toString();
                    }
                });
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
                        makeAutoCompleteCall(autoCompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });
        // set checkBox onClick event
        autoDetect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // perform logic
                    location.getText().clear();
                    location.setVisibility(View.GONE);
                    locationRow.setVisibility(View.GONE);
                } else {
                    location.setVisibility(View.VISIBLE);
                    locationRow.setVisibility(View.VISIBLE);
                }
            }
        });
        // search
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("submitButtonActivity", "the form is submitted");
                if (checkAll()) { // all input are valid
                    // read inputs
                    Log.d("submit result", "input are correct");
                    // if checked, call ipinfo API
                    getLocation(autoDetect.isChecked(),view);
                    /*
                    String eventsResultString = events_result.toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("events_result", eventsResultString);
                    Navigation.findNavController(view).navigate(R.id.action_SeatchFragment_to_EventsFragment, bundle);
                     */
                } else {
                    Log.d("submit result", "input are incorrect");
                }
            }
        });

        // clear
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("clearButtonActivity", "the form is cleared");
                autoCompleteTextView.getText().clear();
                distance.getText().clear();
                distance.setText(String.valueOf(10));
                category.setSelection(0);
                location.getText().clear();
                autoDetect.setChecked(false);
                selectedCategory = "Default";
                selectedKeyword = "";
                //recyclerView.setVisibility(View.GONE);
                //noResult.setVisibility(View.GONE);
            }
        });
    }
    private boolean checkAll() {
        if (autoCompleteTextView.getText().toString().length() == 0) {
            autoCompleteTextView.setError("This field is required");
            return false;
        }
        if (distance.getText().toString().length() == 0) {
            distance.setError("This field is required");
            return false;
        }
        if(!autoDetect.isChecked() && location.getText().toString().length() == 0){
            location.setError("This field is required");
        }
        return true;
    }

    private void getLocation(boolean isAutoDetect, View view){
        if (isAutoDetect) {
            String ipinfoURL = "https://ipinfo.io/?token=2109da12929018";
            List<String> loc_ret = new ArrayList<>();
            ApiCall.make(getContext(), ipinfoURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //parsing logic, please change it as per your requirement
                    Log.d("ipinfo Response", response);
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        String loc = responseObject.getString("loc");
                        String[] tmp = loc.split(",");
                        for (String x : tmp) {
                            Log.d("loc", x);
                            loc_ret.add(x);
                        }
                        searchEvents(loc_ret, view);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
        } else { // call without ipinfo
            String googlemapURL = "https://maps.googleapis.com/maps/api/geocode/json?address="+location.getText().toString()+"&key=AIzaSyAzgqTav7G_0oLAfY__HQ4WjMtUn9Oitmo";
            List<String> loc_ret = new ArrayList<>();
            ApiCall.make(getContext(), googlemapURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //parsing logic, please change it as per your requirement
                    Log.d("googlemap Response", response);
                    try {
                        JSONObject responseObject = new JSONObject(response);
                        String lat = Double.toString(responseObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                        String lng = Double.toString(responseObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                        loc_ret.add(lat);
                        loc_ret.add(lng);
                        searchEvents(loc_ret, view);
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

    private void searchEvents(List<String> loc_info, View view){
        String keyword = autoCompleteTextView.getText().toString();
        List<JSONObject> events = new ArrayList<>();
        int dis = Integer.parseInt(distance.getText().toString());
        String category = "";
        if (selectedCategory.equals("All")) {
            category = "Default";
        } else {
            category = selectedCategory;
        }
        String latitude=loc_info.get(0);
        String longitude=loc_info.get(1);
        //String EventSearchURL = "https://cs571-hw8-379421.wl.r.appspot.com/search_events?";
        String EventSearchURL = "https://cs571-hw6-376322.wl.r.appspot.com/search_events?";
        EventSearchURL += "keyword="+keyword + "&category=" + category + "&radius=" + dis + "&latitude=" + latitude +"&longitude="+longitude;
        ApiCall.make(getContext(), EventSearchURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                Log.d("Events Response", response);
                try {
                    JSONObject responseObject = new JSONObject(response);
                    //events_result = responseObject;
                    String eventsResultString = responseObject.toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("events_result", eventsResultString);
                    Navigation.findNavController(view).navigate(R.id.action_SeatchFragment_to_EventsFragment, bundle);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        selectedCategory = adapterView.getItemAtPosition(i).toString();
        Log.d("selected cate", selectedCategory);
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void makeAutoCompleteCall(String text) {
        ApiCall.make(getContext(), autoCompleteURL+text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                Log.d("AutoComplete Response", response);
                List<String> stringList = new ArrayList<>();
                try {
                    if (response.length() > 3) {
                        // get rid of square brackets
                        String tmp = response.substring(1, response.length() - 1);
                        // split by ","
                        String[] resArr = tmp.split(",");
                        for (String res : resArr) {
                            Log.d("autoComplete item", res.substring(1, res.length() - 1));
                            stringList.add(res.substring(1, res.length() - 1));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //IMPORTANT: set data here and notify
                autocompleteadapter.setData(stringList);
                autocompleteadapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
    }
}
