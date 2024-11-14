/**
 * Activity for browsing Events
 * Used by the Administrator Only
 * @author Noah Vincent
 */

package com.example.myapplication.administrator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.EventArrayAdapter;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.FacilityArrayAdapter;
import com.example.myapplication.objects.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminBrowseEvents extends AppCompatActivity {

    private FirebaseFirestore db;
    private Button back;
    private TextView header;
    private ListView eventList;
    private EventArrayAdapter eventAdapter;
    private ArrayList<Event> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse);
        db = FirebaseFirestore.getInstance();

        back = findViewById(R.id.back_button);

        header = findViewById(R.id.browseHeader);
        header.setText("EVENTS");

        dataList = new ArrayList<Event>();

        eventList = findViewById(R.id.contentListView);
        eventAdapter = new EventArrayAdapter(this, dataList);
        eventList.setAdapter(eventAdapter);

        /**
         * must be set inside the onCreate, setting up this type of launcher makes it possible
         * for the delete method to be called properly from a few activities away.
         */
        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        dataList.clear();
                        eventAdapter.clear();
                        // searching firebase for all registered users
                        db.collection("events").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String id = document.getId();
                                                String title = document.getString("name");
                                                String description = document.getString("description");
                                                String posterURL = document.getString("posterUrl");
                                                String QRcode = document.getString("qrCodeUrl");
                                                String user = document.getString("creatorID");
                                                Event thing = new Event(id, title , description, posterURL, QRcode, user);
                                                dataList.add(thing);
                                                eventAdapter.notifyDataSetChanged();
                                            }
                                        } else {
                                            Log.d("AdminBrowseEvents", "Error getting documents: ", task.getException());
                                        }
                                    }
                                });
                    }
                });

        db.collection("events").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String id = document.getId();
                                String title = document.getString("name");
                                String description = document.getString("description");
                                String posterURL = document.getString("posterUrl");
                                String QRcode = document.getString("qrCodeUrl");
                                String user = document.getString("creatorID");
                                Event thing = new Event(id, title , description, posterURL, QRcode, user);
                                dataList.add(thing);
                                eventAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Log.d("AdminBrowseEvents", "Error getting documents: ", task.getException());
                        }
                    }
                });

        back.setOnClickListener(view -> {
            finish();
        });

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Event event = dataList.get(i);
                Intent intent = new Intent(AdminBrowseEvents.this, AdminViewEvent.class);
                intent.putExtra("event", (Parcelable) event);
                someActivityResultLauncher.launch(intent);
            }
        });

    }

}
