package com.example.myapplication.organizer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntrantListAdapter extends RecyclerView.Adapter<EntrantListAdapter.EntrantViewHolder> {

    private List<String> entrantList, entrantDisplay;
    private Set<String> selectedEntrants;

    // Constructor that takes in the list of entrants and the previously selected entrants
    public EntrantListAdapter(List<String> entrantList, ArrayList<String> selectedEntrants, ArrayList<String> entrantListDisplay) {
        this.entrantList = entrantList;
        this.selectedEntrants = new HashSet<>(selectedEntrants); // Use a Set for efficient lookups
        this.entrantDisplay = entrantListDisplay;
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entrant_item, parent, false);
        return new EntrantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        String device = entrantList.get(position);
        String fullName = entrantDisplay.get(position);
        holder.entrantNameTextView.setText(fullName);

        // Set the checkbox state based on whether the entrant is in the selectedEntrants set
        holder.checkboxSelect.setChecked(selectedEntrants.contains(fullName));

        // Update the selectedEntrants set when the checkbox state changes
        holder.checkboxSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedEntrants.add(device);
            } else {
                selectedEntrants.remove(device);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entrantList.size();
    }

    // Method to retrieve the selected entrants
    public Set<String> getSelectedEntrants() {
        return selectedEntrants;
    }

    // Inner ViewHolder class for EntrantListAdapter
    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantNameTextView;
        CheckBox checkboxSelect;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantNameTextView = itemView.findViewById(R.id.entrantName);
            checkboxSelect = itemView.findViewById(R.id.checkboxSelect);
        }
    }
}
