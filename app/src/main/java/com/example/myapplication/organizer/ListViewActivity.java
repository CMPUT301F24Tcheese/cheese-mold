package com.example.myapplication.organizer;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ListViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private List<String> dataList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private TextView titleTextView;
    private TextView footerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        titleTextView = findViewById(R.id.titleTextView);
        footerTextView = findViewById(R.id.footerTextView);
        recyclerView = findViewById(R.id.recyclerViewLists);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listAdapter = new ListAdapter(dataList);
        recyclerView.setAdapter(listAdapter);

        String listType = getIntent().getStringExtra("listType");
        String eventId = getIntent().getStringExtra("event_id");

        setTitleForList(listType);

        if (listType != null && eventId != null) {
            loadListData(eventId, listType);
        } else {
            Toast.makeText(this, "Invalid data passed", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTitleForList(String listType) {
        String title = "List";
        if ("waiting".equals(listType)) {
            title = "Waiting List";
        } else if ("invited".equals(listType)) {
            title = "Invited List";
        } else if ("cancelled".equals(listType)) {
            title = "Cancelled List";
        }
        titleTextView.setText(title);
    }

    private void loadListData(String eventId, String listType) {
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> list = (List<String>) documentSnapshot.get(listType);
                        if (list != null && !list.isEmpty()) {
                            dataList.clear();
                            dataList.addAll(list);
                        } else {
                            dataList.clear();
                            dataList.add("No entries available");
                        }
                        listAdapter.notifyDataSetChanged();
                        footerTextView.setText("Total: " + (list != null ? list.size() : 0) + " people");
                    } else {
                        Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load data", Toast.LENGTH_SHORT).show());
    }
}
