package com.example.eventfinder.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.SharedPreferences;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventfinder.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.eventfinder.util.SharedPrefUtil;
import com.example.eventfinder.util.RoundedCornersTransformation;
import android.text.method.ScrollingMovementMethod;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    private JSONArray eventsArray;
    private Context context;
    private OnEventClickListener eventClickListener;
    private OnHeartButtonClickListener heartButtonClickListener;
    private SharedPreferences pref;
    // constructor
    public RecycleViewAdapter(Context context, JSONArray array, OnEventClickListener eventClickListener, OnHeartButtonClickListener heartButtonClickListener){
        this.context = context;
        this.eventClickListener = eventClickListener;
        this.heartButtonClickListener = heartButtonClickListener;
        this.eventsArray = array;
        this.pref = SharedPrefUtil.getSharedPrefs(context);
    }
    @NonNull
    @Override
    // get view pre-defined view for the viewHolder class
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.events_list_row,parent,false);
        View view = LayoutInflater.from(context).inflate(R.layout.events_list_row,parent,false);
        return new ViewHolder(view, eventClickListener);
    }

    @Override
    // bind object of specific position in the input data array with the view through the view holder
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject event_info = null;
        try {
            event_info = eventsArray.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            assert event_info != null;
            String id = event_info.getString("id");
            //isFavorite?
            if(pref.contains(id)){
                holder.heartButton.setBackground(ContextCompat.getDrawable(context, R.drawable.heart_fill));
            }else{
                holder.heartButton.setBackground(ContextCompat.getDrawable(context, R.drawable.heart_outline));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            holder.eventName.setText(event_info.getString("eventName"));
            holder.eventName.setSelected(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            holder.venueName.setText(event_info.getString("venueName"));
            holder.venueName.setSelected(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            holder.genre.setText(event_info.getString("genre"));
            holder.genre.setSelected(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            holder.date.setText(showDate(event_info.getString("date")));
            holder.date.setSelected(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.time.setText("");
        try {
            holder.time.setText(showTime(event_info.getString("time")));
            holder.time.setSelected(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.eventName.postDelayed(new Runnable() {
            public void run() {
                holder.eventName.setSelected(true);
            }
        },500);
        holder.venueName.postDelayed(new Runnable() {
            public void run() {
                holder.venueName.setSelected(true);
            }
        },500);
        holder.genre.postDelayed(new Runnable() {
            public void run() {
                holder.genre.setSelected(true);
            }
        },500);
        holder.date.postDelayed(new Runnable() {
            public void run() {
                holder.date.setSelected(true);
            }
        },500);
        holder.time.postDelayed(new Runnable() {
            public void run() {
                holder.time.setSelected(true);
            }
        },500);
        try {
            Log.d("event image",event_info.getString("img"));
            Picasso.get().load(event_info.getString("img"))
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.stat_notify_error)
                    .resize(dpToPx(100),dpToPx(100))
                    .transform(new RoundedCornersTransformation(dpToPx(15), 0))
                    .centerCrop()
                    .into(holder.img);
            /*int sizeInPx = dpToPx(100);
            Glide.with(this.context)
                    .load(event_info.getString("img"))
                    .apply(new RequestOptions()
                            .placeholder(android.R.drawable.stat_sys_download)
                            .error(android.R.drawable.stat_notify_error)
                            .override(sizeInPx, sizeInPx)
                            .centerCrop()
                            .transform(new RoundedCorners(sizeInPx / 6)))
                    .into(holder.img);*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
        holder.heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (heartButtonClickListener != null) {
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        String event_id = null;
                        try {
                            event_id = eventsArray.getJSONObject(pos).getString("id");
                            SharedPreferences.Editor editor = pref.edit();
                            if(pref.contains(event_id)){
                                editor.remove(event_id).apply();
                                holder.heartButton.setBackground(ContextCompat.getDrawable(context, R.drawable.heart_outline));
                                heartButtonClickListener.onHeartButtonClick(eventsArray.getJSONObject(pos).getString("eventName"), pos, false);
                            }else{
                                /*
                                JSONObject value = new JSONObject();
                                value.put("eventName",eventsArray.getJSONObject(pos).getString("name"));
                                value.put("venueName",eventsArray.getJSONObject(pos).getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name"));
                                value.put("genre",eventsArray.getJSONObject(pos).getJSONArray("classifications").getJSONObject(0).getJSONObject("segment").getString("name"));
                                value.put("date",eventsArray.getJSONObject(pos).getJSONObject("dates").getJSONObject("start").getString("localDate"));
                                try {
                                    value.put("time",eventsArray.getJSONObject(pos).getJSONObject("dates").getJSONObject("start").getString("localTime"));
                                } catch (JSONException e) {e.printStackTrace();}
                                value.put("id",event_id);
                                editor.putString(event_id,value.toString());
                                 */
                                editor.putString(event_id,eventsArray.getJSONObject(pos).toString());
                                editor.apply();
                                holder.heartButton.setBackground(ContextCompat.getDrawable(context, R.drawable.heart_fill));
                                heartButtonClickListener.onHeartButtonClick(eventsArray.getJSONObject(pos).getString("eventName"), pos,true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private String showDate(String str){
        String[] date = str.split("-");
        return date[1]+"/"+date[2]+"/"+date[0];
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty() && payloads.get(0) instanceof Boolean) {
            boolean isFavorite = (Boolean) payloads.get(0);
            if (isFavorite) {
                holder.heartButton.setBackground(ContextCompat.getDrawable(context, R.drawable.heart_fill));
            } else {
                holder.heartButton.setBackground(ContextCompat.getDrawable(context, R.drawable.heart_outline));
            }
        } else {
            onBindViewHolder(holder, position);
        }
    }

    public void removeItem(int position) {
        eventsArray.remove(position);
        notifyItemRemoved(position);
    }

    public void updateDataSet(JSONArray newEventsArray) {
        this.eventsArray = newEventsArray;
        notifyDataSetChanged();
    }

    public void updateHeartButtons() {
        for (int i = 0; i < eventsArray.length(); i++) {
            try {
                JSONObject event = eventsArray.getJSONObject(i);
                String eventId = event.getString("id");
                boolean isFavorite = pref.contains(eventId);
                // Assuming you have a method in ViewHolder to update the heart button state
                notifyItemChanged(i, isFavorite);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public int getItemCount() {
        return eventsArray.length();
    }
    // view holder is passed in here
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        OnEventClickListener eventClickListener;
        public ImageView img;
        public TextView eventName;
        public TextView venueName;
        public TextView genre;
        public TextView date;
        public TextView time;
        public Button heartButton;
        //public boolean isFavorite;
        public ViewHolder(@NonNull View itemView, OnEventClickListener eventClickListener) {
            super(itemView);
            img = itemView.findViewById(R.id.eventImage);
            eventName = itemView.findViewById(R.id.eventName);
            venueName = itemView.findViewById(R.id.venueName);
            genre = itemView.findViewById(R.id.genre);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            heartButton = itemView.findViewById(R.id.heartButton);
            this.eventClickListener = eventClickListener;
            itemView.setOnClickListener(this);

            eventName.requestFocus();
            venueName.requestFocus();
            genre.requestFocus();
            date.requestFocus();
            time.requestFocus();
            eventName.setSelected(true);
            venueName.setSelected(true);
            genre.setSelected(true);
            date.setSelected(true);
            time.setSelected(true);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            String id = null;
            try {
                id = eventsArray.getJSONObject(pos).getString("id");
                eventClickListener.onEventClick(pos,id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Log.d("Click Event",String.format("%d",getAdapterPosition()));
            //Log.d("Event id",id);
        }
    }

    // for click event
    public interface OnEventClickListener{
        void onEventClick(int pos, String id);
    }

    //for click heart
    public interface OnHeartButtonClickListener{
        void onHeartButtonClick(String name, int pos, boolean flag);
    }
}
