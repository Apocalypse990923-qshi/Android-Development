package com.example.eventfinder.Adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
    private List<String> listData;
    public AutoCompleteAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        listData = new ArrayList<>();
    }
    public void setData(List<String> list) {
        listData.clear();
        listData.addAll(list);
    }
    @Override
    public int getCount() {
        return listData.size();
    }
    @Nullable
    @Override
    public String getItem(int position) {
        return listData.get(position);
    }
    /**
     * Used to Return the full object directly from adapter.
     *
     * @param position
     * @return
     */
    public String getObject(int position) {
        return listData.get(position);
    }
    @NonNull
    @Override
    public Filter getFilter() {
        Filter dataFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.values = listData;
                    filterResults.count = listData.size();
                }
                return filterResults;
            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return dataFilter;
    }
}
