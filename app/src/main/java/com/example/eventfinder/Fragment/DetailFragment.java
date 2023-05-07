package com.example.eventfinder.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.core.text.HtmlCompat;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.eventfinder.R;
import com.example.eventfinder.util.ApiCall;
import com.example.eventfinder.util.RoundedCornersTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {
    private TextView artist;
    private TextView venue;
    private TextView date;
    private TextView time;
    private TextView genres;
    private TextView price;
    private LinearLayout statusCard;
    private TextView status;
    private TextView ticketUrl;
    private ImageView seatMap;
    private JSONObject eventInfo;
    private TableRow artistRow;
    private TableRow timeRow;
    private TableRow priceRow;
    private ProgressBar progressBar;
    private ConstraintLayout detailCard;
    private List<String> attractions_list;
    public DetailFragment(JSONObject eventInfo){
        this.eventInfo = eventInfo;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        artist = view.findViewById(R.id.Artist);
        venue = view.findViewById(R.id.Venue);
        date = view.findViewById(R.id.Date);
        time = view.findViewById(R.id.Time);
        genres = view.findViewById(R.id.Genres);
        price = view.findViewById(R.id.Price);
        statusCard = view.findViewById(R.id.StatusCard);
        status = view.findViewById(R.id.Status);
        ticketUrl = view.findViewById(R.id.TicketUrl);
        seatMap = view.findViewById(R.id.seatMap);
        artistRow = view.findViewById(R.id.artistRow);
        timeRow = view.findViewById(R.id.timeRow);
        priceRow = view.findViewById(R.id.priceRow);

        progressBar = view.findViewById(R.id.progressBar4);
        detailCard = view.findViewById(R.id.detailCard);
        progressBar.setVisibility(View.VISIBLE);
        detailCard.setVisibility(View.GONE);
        try {
            if(eventInfo.getJSONObject("_embedded").has("attractions")){
                search_attractions(eventInfo.getJSONObject("_embedded").getJSONArray("attractions"));
                artist.setText(showAttractions(attractions_list));
                artist.setSelected(true);
            }else{
                artistRow.setVisibility(View.GONE);
            }
            venue.setText(eventInfo.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name"));
            venue.setSelected(true);
            date.setText(showDate(eventInfo.getJSONObject("dates").getJSONObject("start").getString("localDate")));
            date.setSelected(true);
            if(eventInfo.getJSONObject("dates").getJSONObject("start").has("localTime")){
                time.setText(showTime(eventInfo.getJSONObject("dates").getJSONObject("start").getString("localTime")));
                time.setSelected(true);
            }else{
                timeRow.setVisibility(View.GONE);
            }
            genres.setText(showGenres(eventInfo.getJSONArray("classifications").getJSONObject(0)));
            genres.setSelected(true);
            if(eventInfo.has("priceRanges")){
                price.setText(showPrice(eventInfo.getJSONArray("priceRanges").getJSONObject(0)));
                price.setSelected(true);
            }else{
                priceRow.setVisibility(View.GONE);
            }
            setStatus(eventInfo.getJSONObject("dates").getJSONObject("status").getString("code"));
            ticketUrl.setText(HtmlCompat.fromHtml("<u>"+eventInfo.getString("url")+"</u>", HtmlCompat.FROM_HTML_MODE_LEGACY));
            ticketUrl.setSelected(true);
            Intent openURL = new Intent(Intent.ACTION_VIEW);
            openURL.setData(Uri.parse(eventInfo.getString("url")));
            ticketUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(openURL);
                }
            });
            Picasso.get().load(eventInfo.getJSONObject("seatmap").getString("staticUrl"))
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.stat_notify_error)
                    .resize(dpToPx(350),dpToPx(350))
                    .transform(new RoundedCornersTransformation(dpToPx(15), 0))
                    .centerCrop()
                    .into(seatMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                detailCard.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }, 1000);
    }

    public int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void search_attractions(JSONArray attractions){
        attractions_list = new ArrayList<>();
        try{
            for(int i=0;i<attractions.length();i++){
                attractions_list.add(attractions.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String showAttractions(List<String> attractions){
        String ret = attractions.get(0);
        for(int i=1;i<attractions.size();i++){
            ret += " | "+attractions.get(i);
        }
        return ret;
    }

    private String showDate(String str){
        String[] date = str.split("-");
        String[] dict = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        int year = Integer.valueOf(date[0]);
        int month = Integer.valueOf(date[1]);
        int day = Integer.valueOf(date[2]);
        return dict[month-1]+" "+String.valueOf(day)+", "+String.valueOf(year);
    }

    private String showTime(String str){
        String[] time = str.split(":");
        int hour = Integer.valueOf(time[0]);
        int minute = Integer.valueOf(time[1]);
        if(hour>12){
            hour-=12;
            return String.valueOf(hour)+":"+(minute<10?"0"+String.valueOf(minute):String.valueOf(minute))+" PM";
        }
        return String.valueOf(hour)+":"+(minute<10?"0"+String.valueOf(minute):String.valueOf(minute))+" AM";
    }

    private String showGenres(JSONObject genres){
        String ret="";
        try{
            if(genres.has("segment") && !genres.getJSONObject("segment").getString("name").equals("Undefined")){
                ret += genres.getJSONObject("segment").getString("name");
            }
            if(genres.has("genre") && !genres.getJSONObject("genre").getString("name").equals("Undefined")){
                ret += " | "+genres.getJSONObject("genre").getString("name");
            }
            if(genres.has("subGenre") && !genres.getJSONObject("subGenre").getString("name").equals("Undefined")){
                ret += " | "+genres.getJSONObject("subGenre").getString("name");
            }
            if(genres.has("type") && !genres.getJSONObject("type").getString("name").equals("Undefined")){
                ret += " | "+genres.getJSONObject("type").getString("name");
            }
            if(genres.has("subType") && !genres.getJSONObject("subType").getString("name").equals("Undefined")){
                ret += " | "+genres.getJSONObject("subType").getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(ret.length()>0){
            return ret;
        }
        return "Undefined";
    }

    private String showPrice(JSONObject price){
        String ret="";
        try{
            ret = price.getString("min")+" - "+price.getString("max")+" (USD)";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void setStatus(String status_code){
        if(status_code.equals("onsale")){
            statusCard.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.green_card));
            status.setText("On Sale");
        }else if(status_code.equals("offsale")){
            statusCard.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.red_card));
            status.setText("Off Sale");
        }else if(status_code.equals("canceled")){
            statusCard.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.black_card));
            status.setText("Canceled");
        }else if(status_code.equals("postponed")){
            statusCard.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.yellow_card));
            status.setText("Postponed");
        }else if(status_code.equals("rescheduled")){
            statusCard.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.yellow_card));
            status.setText("Rescheduled");
        }else{
            statusCard.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.yellow_card));
            status.setText("Unknown");
        }
    }
}
