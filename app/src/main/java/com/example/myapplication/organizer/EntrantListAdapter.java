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

/**
 * NOTE: !!! This file does not have any usage for current version, therefore NO Test Case for this java class !!!
 * A RecyclerView Adapter for displaying a list of entrants and allowing selection through checkboxes.
 */
public class EntrantListAdapter extends RecyclerView.Adapter<EntrantListAdapter.EntrantViewHolder> {

    private List<String> entrantList, entrantDisplay;
    private Set<String> selectedEntrants;

    /**
     * Constructor for EntrantListAdapter.
     *
     * @param entrantList         List of entrant IDs (device IDs).
     * @param selectedEntrants    List of initially selected entrant IDs.
     * @param entrantListDisplay  List of entrant display names.
     */
    public EntrantListAdapter(List<String> entrantList, ArrayList<String> selectedEntrants, ArrayList<String> entrantListDisplay) {
        this.entrantList = entrantList;
        this.selectedEntrants = new HashSet<>(selectedEntrants); // Use a Set for efficient lookups
        this.entrantDisplay = entrantListDisplay;
    }

    /**
     * Called when RecyclerView needs a new {@link EntrantViewHolder} to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new EntrantViewHolder that holds a View for the entrant item.
     */
    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entrant_item, parent, false);
        return new EntrantViewHolder(view);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item.
     * @param position The position of the item within the adapter's data set.
     */
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

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The number of items in the data set.
     */
    @Override
    public int getItemCount() {
        return entrantList.size();
    }

    /**
     * Retrieves the set of selected entrants.
     *
     * @return A set of selected entrant IDs.
     */
    public Set<String> getSelectedEntrants() {
        return selectedEntrants;
    }

    /**
     * ViewHolder class for holding entrant item views.
     */
    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantNameTextView;
        CheckBox checkboxSelect;

        /**
         * Constructor for EntrantViewHolder.
         *
         * @param itemView The View representing an entrant item.
         */
        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantNameTextView = itemView.findViewById(R.id.entrantName);
            checkboxSelect = itemView.findViewById(R.id.checkboxSelect);
        }
    }
}
