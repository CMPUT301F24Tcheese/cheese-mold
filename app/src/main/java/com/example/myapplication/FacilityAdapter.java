package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder> {
    private List<Facility> facilityList;

    public FacilityAdapter(List<Facility> facilityList) {
        this.facilityList = facilityList;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the facility_item layout to use for each facility
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_item, parent, false);
        return new FacilityViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilityList.get(position);
        holder.facilityName.setText(facility.getName());
        holder.facilityDescription.setText(facility.getDescription());

        // Click Listener to open EditFacilityActivity
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), EditFacilityActivity.class);
            intent.putExtra("facilityId", facility.getId());
            intent.putExtra("street", facility.getStreet());
            intent.putExtra("city", facility.getCity());
            intent.putExtra("province", facility.getProvince());
            intent.putExtra("postalCode", facility.getPostalCode());
            view.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return facilityList.size(); // Return the total number of facilities
    }

    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView facilityName, facilityDescription;

        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView);
            // Make sure these IDs match your facility_item.xml
            facilityName = itemView.findViewById(R.id.textViewStreet);
            facilityDescription = itemView.findViewById(R.id.textViewCity);
        }
    }
}
