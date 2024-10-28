package com.example.myapplication.objects;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FacilityArrayAdapter extends ArrayAdapter<Facility> {

    public FacilityArrayAdapter(Context context, ArrayList<Facility> facilities) {
        super(context, 0, facilities);
    }

    private void setOrganizer(String user_id, TextView organizer) {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // Get the instance of Firebase Firestore database
        db.collection("users").document(user_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if(document.exists()) {
                    String name = document.getString("Firstname") + ' ' + document.getString("Lastname");
                    organizer.setText(name);
                } else {
                    Log.d("ArrayApadpter", "No such document"); // Log a message if the document does not exist
                }
            } else {
                Log.w("ArrayAdapter", "Error getting documents.", task.getException()); // Log a warning if there was an error retrieving the document
            }
        });
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.content_array_adapter, parent, false);
        } else {
            view = convertView;
        }

        Facility facility = getItem(position);
        TextView facilityName = view.findViewById(R.id.content_header);
        TextView facilityOrganizer = view.findViewById(R.id.content_subheader);

        assert facility != null;
        facilityName.setText(facility.getName());
        setOrganizer(facility.getId(), facilityOrganizer);
        return view;
    }

}
