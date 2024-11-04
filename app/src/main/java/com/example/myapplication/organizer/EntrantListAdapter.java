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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntrantListAdapter extends RecyclerView.Adapter<EntrantListAdapter.EntrantViewHolder> {

    private List<String> entrantList;
    private Set<String> selectedEntrants = new HashSet<>(); // Track selected entrants

    public EntrantListAdapter(List<String> entrantList, List<String> preSelectedEntrants) {
        this.entrantList = entrantList;
        if (preSelectedEntrants != null) {
            this.selectedEntrants.addAll(preSelectedEntrants); // Pre-select these entrants
        }
    }

    @NonNull
    @Override
    public EntrantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entrant_item, parent, false);
        return new EntrantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntrantViewHolder holder, int position) {
        String fullName = entrantList.get(position);
        holder.entrantNameTextView.setText(fullName);
        holder.checkboxSelect.setChecked(selectedEntrants.contains(fullName)); // Pre-select if previously selected

        holder.checkboxSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedEntrants.add(fullName);
            } else {
                selectedEntrants.remove(fullName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entrantList.size();
    }

    public Set<String> getSelectedEntrants() {
        return selectedEntrants;
    }

    public static class EntrantViewHolder extends RecyclerView.ViewHolder {
        TextView entrantNameTextView;
        ImageView profileImageView;
        CheckBox checkboxSelect;

        public EntrantViewHolder(@NonNull View itemView) {
            super(itemView);
            entrantNameTextView = itemView.findViewById(R.id.entrantName);
            profileImageView = itemView.findViewById(R.id.profileImage);
            checkboxSelect = itemView.findViewById(R.id.checkboxSelect);
        }
    }
}
