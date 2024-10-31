package com.example.myapplication.administrator;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.objects.Event;
import com.example.myapplication.objects.EventArrayAdapter;
import com.example.myapplication.objects.Facility;
import com.example.myapplication.objects.FacilityArrayAdapter;
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

        db.collection("events").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String title = document.getString("name");
                                String description = document.getString("description");
                                String posterURL = document.getString("posterUrl");
                                String QRcode = document.getString("qrCodeUrl");
                                String user = document.getString("creatorID");
                                Event thing = new Event(title , description, posterURL, QRcode, user);
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
    }

}
