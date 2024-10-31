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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class EventArrayAdapter extends ArrayAdapter<Event> {

    public EventArrayAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    private void setFacility(String creatorID, TextView facility) {
        FirebaseFirestore db = FirebaseFirestore.getInstance(); // Get the instance of Firebase Firestore database
        db.collection("Facilities").document(creatorID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                if(document.exists()) {
                    String address = "Location: " + document.getString("name");
                    facility.setText(address);
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

        Event event = getItem(position);
        TextView name = view.findViewById(R.id.content_header);
        TextView facility = view.findViewById(R.id.content_subheader);

        assert event != null;
        name.setText(event.getTitle());
        if (event.getCreatorID() != null) {
            setFacility(event.getCreatorID(), facility);
        }
        return view;
    }

}
