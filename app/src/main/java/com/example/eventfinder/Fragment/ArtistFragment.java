package com.example.eventfinder.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventfinder.Adapter.RecycleViewAdapter;
import com.example.eventfinder.R;
import com.example.eventfinder.Adapter.RecycleViewAdapter2;

import org.json.JSONArray;

public class ArtistFragment extends Fragment {
    private boolean hasMusician;
    private JSONArray musicians;
    private RecyclerView recyclerView;
    private ConstraintLayout noArtist;
    private RecycleViewAdapter2 adapter;;
    public ArtistFragment(boolean hasMusician, JSONArray musicians){
        this.hasMusician=hasMusician;
        this.musicians=musicians;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artist, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        noArtist = view.findViewById(R.id.noArtist);
        recyclerView = view.findViewById(R.id.artistList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if(hasMusician){
            noArtist.setVisibility(View.GONE);
            adapter = new RecycleViewAdapter2(getContext(), musicians);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
        }else{
            recyclerView.setVisibility(View.GONE);
            noArtist.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }
}
