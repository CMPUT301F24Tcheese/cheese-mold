package com.example.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.objects.Facility;
import com.example.myapplication.organizer.EditFacilityActivity;

import java.util.List;

// Adapter class for managing and displaying a list of facilities in a RecyclerView
public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder> {

    private List<Facility> facilityList; // List to store Facility objects that will be displayed

    // Constructor to initialize the adapter with a list of facilities
    public FacilityAdapter(List<Facility> facilityList) {
        this.facilityList = facilityList; // Assign the provided list of facilities to the local variable
    }

    // Called when the RecyclerView needs a new ViewHolder to display a facility
    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate a new view from the facility_item XML layout for each item in the list
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_item, parent, false);
        return new FacilityViewHolder(view); // Return a new ViewHolder instance
    }

    // Called by RecyclerView to display data at a specific position
    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        Facility facility = facilityList.get(position); // Retrieve the Facility object for the current position
        holder.facilityName.setText(facility.getName()); // Set the facility name in the corresponding TextView
        holder.facilityDescription.setText(facility.getDescription()); // Set the facility description in the corresponding TextView

        // Set an OnClickListener on the item view to open EditFacilityActivity when clicked
        holder.itemView.setOnClickListener(view -> {
            // Create an Intent to start the EditFacilityActivity
            Intent intent = new Intent(view.getContext(), EditFacilityActivity.class);
            // Pass the facility's ID and address information to the EditFacilityActivity
            intent.putExtra("facilityId", facility.getId()); // Pass the unique ID of the facility
            intent.putExtra("street", facility.getStreet()); // Pass the street address
            intent.putExtra("city", facility.getCity()); // Pass the city
            intent.putExtra("province", facility.getProvince()); // Pass the province
            intent.putExtra("postalCode", facility.getPostalCode()); // Pass the postal code
            view.getContext().startActivity(intent); // Start the EditFacilityActivity
        });
    }

    // Returns the total number of items that the adapter will display
    @Override
    public int getItemCount() {
        return facilityList.size(); // Return the size of the facility list
    }

    // ViewHolder class that holds references to each component of the facility item layout
    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView facilityName, facilityDescription; // TextViews to display the facility name and description

        // Constructor for initializing the ViewHolder with the item view layout
        public FacilityViewHolder(@NonNull View itemView) {
            super(itemView); // Call the parent class constructor
            // Link the TextViews with their corresponding views in the facility_item layout
            facilityName = itemView.findViewById(R.id.textViewStreet); // Find the TextView for displaying the facility name
            facilityDescription = itemView.findViewById(R.id.textViewCity); // Find the TextView for displaying the facility description
        }
    }
}
