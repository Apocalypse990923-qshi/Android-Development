package com.example.eventfinder.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventfinder.R;
import com.example.eventfinder.util.CircleProgress;
import com.example.eventfinder.util.RoundedCornersTransformation;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecycleViewAdapter2 extends RecyclerView.Adapter<RecycleViewAdapter2.ViewHolder>{
    private JSONArray musicians;
    private Context context;
    public RecycleViewAdapter2(Context context, JSONArray musicians){
        this.context=context;
        this.musicians = musicians;
    }
    @NonNull
    @Override
    public RecycleViewAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.artist_list_row,parent,false);
        return new RecycleViewAdapter2.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter2.ViewHolder holder, int position) {
        JSONObject musician_info = null;
        try{
            musician_info = musicians.getJSONObject(position);
            assert musician_info != null;
            int sizeInPx = dpToPx(110);
            Picasso.get().load(musician_info.getJSONArray("albums").getJSONObject(0).getJSONArray("images").getJSONObject(0).getString("url"))
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.stat_notify_error)
                    .resize(sizeInPx,sizeInPx)
                    .transform(new RoundedCornersTransformation(dpToPx(15), 0))
                    .centerCrop()
                    .into(holder.album1);
            Picasso.get().load(musician_info.getJSONArray("albums").getJSONObject(1).getJSONArray("images").getJSONObject(0).getString("url"))
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.stat_notify_error)
                    .resize(sizeInPx,sizeInPx)
                    .transform(new RoundedCornersTransformation(dpToPx(15), 0))
                    .centerCrop()
                    .into(holder.album2);
            Picasso.get().load(musician_info.getJSONArray("albums").getJSONObject(2).getJSONArray("images").getJSONObject(0).getString("url"))
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.stat_notify_error)
                    .resize(sizeInPx,sizeInPx)
                    .transform(new RoundedCornersTransformation(dpToPx(15), 0))
                    .centerCrop()
                    .into(holder.album3);
            Picasso.get().load(musician_info.getJSONArray("images").getJSONObject(0).getString("url"))
                    .placeholder(android.R.drawable.stat_sys_download)
                    .error(android.R.drawable.stat_notify_error)
                    .resize(sizeInPx,sizeInPx)
                    .transform(new RoundedCornersTransformation(dpToPx(15), 0))
                    .centerCrop()
                    .into(holder.musicianImg);
            holder.musicianName.setText(musician_info.getString("name"));
            holder.musicianName.setSelected(true);
            holder.follower.setText(showFollower(musician_info.getJSONObject("followers").getInt("total")));
            holder.follower.setSelected(true);
            Intent openURL = new Intent(Intent.ACTION_VIEW);
            openURL.setData(Uri.parse(musician_info.getJSONObject("external_urls").getString("spotify")));
            holder.spotifyLink.setSelected(true);
            holder.spotifyLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(openURL);
                }
            });
            //holder.progressBar.setProgress(musician_info.getInt("popularity"),false);
            //holder.progressBar.setIndeterminate(false);
            holder.progressBar.SetMax(100);
            holder.progressBar.SetCurrent(musician_info.getInt("popularity"));
            //holder.popularity.setText(String.valueOf(musician_info.getInt("popularity")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private String showFollower(Integer follower){
        if(follower<1000){
            return String.valueOf(follower)+" Followers";
        }else if(follower<1000000){
            return String.valueOf(follower/1000)+"K Followers";
        }
        return String.valueOf(follower/1000000)+"M Followers";
    }

    @Override
    public int getItemCount() {
        return musicians.length();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView album1;
        public ImageView album2;
        public ImageView album3;
        public ImageView musicianImg;
        public TextView musicianName;
        public TextView follower;
        public TextView spotifyLink;
        //public ProgressBar progressBar;
        public CircleProgress progressBar;
        public TextView popularity;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            album1 = itemView.findViewById(R.id.album1);
            album2 = itemView.findViewById(R.id.album2);
            album3 = itemView.findViewById(R.id.album3);
            musicianImg = itemView.findViewById(R.id.musicianImg);
            musicianName = itemView.findViewById(R.id.musicianName);
            follower = itemView.findViewById(R.id.follower);
            spotifyLink = itemView.findViewById(R.id.spotifyLink);
            progressBar = itemView.findViewById(R.id.progressBar3);
            //popularity = itemView.findViewById(R.id.popularity);
        }
    }
}
